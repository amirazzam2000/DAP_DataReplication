package ServerCommunications;

import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import Network.ClientSide.Client;
import Network.ClientSide.ClientCommunicationManager;
import Network.Packets.Packet;
import Network.Packets.Protocols;
import Network.ServerSide.Server;
import ResourceManagement.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CoreCommunication implements Runnable, IServer {
    private Server receiver;
    private Client sender;
    private HashMap<String, Integer> requests;
    private ConnectionConfig connectionConfig;
    private Config myConfig;
    private HashMap<String, CommunicationServers> communicationManagers;

    public static boolean running = true;

    final int NUM_LIGHTWEIGHTS = 2;
    final String[] clientNames ={"NodeB1","NodeB2"};


    public CoreCommunication(Server receiver, Client sender, String[] serverNames,
                             ConnectionConfig connectionConfig, Config myConfig,
                             HashMap<String, CommunicationServers> communicationManagers) {

        System.out.println("-------------- " + myConfig.getPid());
        this.receiver = receiver;
        this.sender = sender;
        this.requests = new HashMap<>();

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

    public void broadcastMsg(int protocol, Object data){

        //Broadcasting message
        for (String nodeName : communicationManagers.keySet()) {
            Config nodeConnected = connectionConfig.getConfigOf(nodeName);

            if (checkValidServerName(nodeConnected.getName())){
                CommunicationServers comAux =
                        communicationManagers.get(nodeConnected.getName());

                if (comAux != null){

                    comAux.resetAckFlag();

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

        // Wait for ACKs
        for (String nodeName : communicationManagers.keySet()) {
            Config nodeConnected = connectionConfig.getConfigOf(nodeName);

            if (checkValidServerName(nodeConnected.getName())){
                CommunicationServers comAux =
                        communicationManagers.get(nodeConnected.getName());

                if (comAux != null){
                    while (!comAux.isAckFlag()){
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        }catch (InterruptedException ignored){}
                    }
                }
            }
        }
    }

    public boolean checkValidServerName(String name) {
        if (name.equals(myConfig.getName()))
            return false;

        for(String n : clientNames){
            if(n.equals(name))
                return true;
        }
        return false;
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

        if (Objects.equals(myConfig.getName(), "NodeB2")){
            //Client setup
            Config childConfig;
            for (int i = 0; i < NUM_LIGHTWEIGHTS; i++){

                if(!clientNames[i].equals(config.getName())){
                    childConfig =
                            this.getConnectionConfig().getConfigOf(clientNames[i]);
                    try {
                        System.out.println("Trying to connect to " + clientNames[i]);
                        this.sender.addCommunication(
                                childConfig.getAddress(),
                                childConfig.getPort(),
                                this.getCommunicationManagers().get(clientNames[i]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            this.checkAllPortsConnected();
        }

        Thread t = new Thread(this);
        t.start();


    }


    @Override
    public void run() {
        System.out.println("------------Starting------------");
        Config destination;
        ArrayList<Config> com = new ArrayList<>();

        destination = connectionConfig.getConfigOf("NodeC1");
        com.add(destination);

        destination = connectionConfig.getConfigOf("NodeC2");
        com.add(destination);

        while(CoreCommunication.running){

            // SEND PACKET
            try {

                TimeUnit.SECONDS.sleep(10);
                if (Objects.equals(myConfig.getName(), "NodeB2")){


                    for (Config nodeConfig : com) {

                        System.out.println("sending the information down to " + nodeConfig.getName());


                        communicationManagers.get(nodeConfig.getName()).setPacket(new Packet(
                                nodeConfig,
                                myConfig,
                                Protocols.UPDATE_LAYER2,
                                ResourceManager.get().getArrayValues()

                        ));


                        communicationManagers.get(nodeConfig.getName()).setSend();
                    }


                }
            }catch (InterruptedException ignored){}

        }

        this.receiver.stopServer();
        this.sender.closeCommunications();

    }
}
