import Clocks.DirectClock;
import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import Network.ClientSide.Client;
import Network.ClientSide.ClientCommunicationManager;
import Network.Packets.Packet;
import Network.Packets.Protocols;
import Network.ServerSide.Server;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoreCommunication implements Runnable{
    private Server receiver;
    private Client sender;
    private DirectClock clock;
    private HashMap<String, Integer> requests;
    private ConnectionConfig connectionConfig;
    private Config myConfig;
    private HashMap<String, CommunicationServers> communicationManagers;

    public static boolean running = true;

    final int NUM_LIGHTWEIGHTS = 3;
    final String[] clientNames ={"NodeA1","NodeA2","NodeA3"};


    public CoreCommunication(Server receiver, Client sender, String[] serverNames,
                             String myName, ConnectionConfig connectionConfig, Config myConfig,
                             HashMap<String, CommunicationServers> communicationManagers) {

        System.out.println("-------------- " + myConfig.getPid());
        this.receiver = receiver;
        this.sender = sender;
        this.requests = new HashMap<>();

        this.clock = new DirectClock(myConfig.getName());


        for (String name : serverNames) {
                this.requests.put(name, Integer.MAX_VALUE);
        }

        this.communicationManagers = communicationManagers;
        this.connectionConfig = connectionConfig;
        this.myConfig = myConfig;

    }

    public ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }

    public Config getMyConfig() {
        return myConfig;
    }

    public HashMap<String, CommunicationServers> getCommunicationManagers() {
        return communicationManagers;
    }

    private void requestCS(){
        String myName = myConfig.getName();
        clock.ticks();
        requests.put(myName, clock.getValue(myName));
        broadcastMsg(Protocols.REQUEST, requests.get(myName));
        while(!okayCS()){
            try {
                this.checkChannel();
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseCS(){
        requests.put(myConfig.getName(), Integer.MAX_VALUE);
        broadcastMsg(Protocols.RELEASED, clock.getValue(myConfig.getName()));
    }

    private boolean okayCS(){
        String nodeName;
        //printTimeStamps();
        for(Map.Entry<String, Integer> node: requests.entrySet()) {
            nodeName = node.getKey();
            if(isGreater(requests.get(myConfig.getName()),
                        myConfig.getPid(),
                        requests.get(nodeName),
                        connectionConfig.getConfigOf(nodeName).getPid()))
                return false;
            if(isGreater(requests.get(myConfig.getName()),
                        myConfig.getPid(),
                        clock.getValue(nodeName),
                        connectionConfig.getConfigOf(nodeName).getPid()))
                return false;
        }
        return true;
    }

    private boolean isGreater( int entry1, int pid1, int entry2, int pid2){
        if(entry2 == Integer.MAX_VALUE) return false;
        return ((entry1>entry2) || ((entry1==entry2) && (pid1>pid2)));
    }

    private void broadcastMsg(int protocol, Object data){
        for (String nodeName : communicationManagers.keySet()) {
            Config nodeConnected = connectionConfig.getConfigOf(nodeName);
            if (checkValidServerName(nodeConnected.getName())){
                clock.sendAction();
                CommunicationServers comAux =
                        communicationManagers.get(nodeConnected.getName());

                if (comAux != null){
                    comAux.setPacket(new Packet(
                            nodeConnected,
                            myConfig,
                            protocol,
                            data));
                    System.out.println("broadcasting to " + nodeConnected.getName());
                    comAux.setSend();
                }else{
                    System.out.println("rotten message!");
                }
            }
        }
    }

    public boolean checkValidServerName(String name) {
        if (name.equals(myConfig.getName()))
            return false;

        final String[] clientNames ={"NodeA1","NodeA2","NodeA3"};
        for(String n : clientNames){
            if(n.equals(name))
                return true;
        }
        return false;
    }

    private void checkChannel() {
        // the communication server reserved under the node ID refers to the
        // receiver server
        if(communicationManagers.get(myConfig.getName()).isMessagesAvailable()){
            Packet[] messages =
                    communicationManagers.get(myConfig.getName()).getMessageFromQueue();
            for (Packet message :
                    messages) {
                    this.handleMsg(message);
            }

        }
    }

    private void handleMsg(Packet message){
        int timeStamp = (Integer) (message.getData());
        clock.receiveAction(message.getSource().getName(), timeStamp);
        if (message.getProtocolID() == Protocols.REQUEST){
            requests.put(message.getSource().getName(), timeStamp);
            communicationManagers.get(message.getSource().getName())
                    .setPacket(new Packet(connectionConfig.getConfigOf(message.getSource().getName()),
                    myConfig,
                    Protocols.ACK,
                    clock.getValue(myConfig.getName())));

        }else if (message.getProtocolID() == Protocols.RELEASED){
            requests.put(message.getSource().getName(), Integer.MAX_VALUE);
        }
    }

    private void checkAllPortsConnected() {
        for (ClientCommunicationManager communicationManager:
             sender.getActiveCommunications()) {
            while (!communicationManager.isOn()){
                try {
                    TimeUnit.NANOSECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startServerCommunication(Config config, String[] clientNames) {
        //Server setup
        this.receiver.setPort(config.getPort());
        this.receiver.startServer();

        //Client setup
        Config childConfig;
        for (int i = 0; i < NUM_LIGHTWEIGHTS; i++){

            if(!clientNames[i].equals(config.getName())){
                childConfig =
                        this.getConnectionConfig().getConfigOf(clientNames[i]);
                try {
                    System.out.println("Trying to connect to " + clientNames[i]);
                    this.sender.addCommunication(childConfig.getAddress(),
                            childConfig.getPort(),
                            this.getCommunicationManagers()
                                    .get(clientNames[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.checkAllPortsConnected();

        Thread t = new Thread(this);
        t.start();


    }


    @Override
    public void run() {
        System.out.println("------------Starting------------");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");


        while(CoreCommunication.running){
            this.requestCS();

            System.out.println("----------------> access granted! " + dtf.format(LocalDateTime.now()));
            // SEND PACKET
            try {
                TimeUnit.SECONDS.sleep(3);
            }catch (InterruptedException ignored){}

            this.releaseCS();
        }

        this.receiver.stopServer();
        this.sender.closeCommunications();

    }
}
