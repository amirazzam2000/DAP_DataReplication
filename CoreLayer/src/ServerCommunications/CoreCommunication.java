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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CoreCommunication implements Runnable{
    private Server receiver;
    private Client sender;
    private HashMap<String, Integer> requests;
    private ConnectionConfig connectionConfig;
    private Config myConfig;
    private HashMap<String, CommunicationServers> communicationManagers;

    public static boolean running = true;

    final int NUM_LIGHTWEIGHTS = 3;
    final String[] clientNames ={"NodeA1","NodeA2","NodeA3"};


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

        final String[] clientNames ={"NodeA1","NodeA2","NodeA3"};
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

            if(myConfig.getName().equals("NodeA3")){
                Config conf = connectionConfig.getConfigOf("NodeB2");
                try {
                    System.out.println("Trying to connect to NodeB2");

                    communicationManagers.put("NodeB2",
                            new CommunicationServers());

                    this.sender.addCommunication(
                            conf.getAddress(),
                            conf.getPort(),
                            this.getCommunicationManagers().get("NodeB2"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("connected to NodeB2");

            }
            else if(myConfig.getName().equalsIgnoreCase("NodeA2") ){
                Config conf = connectionConfig.getConfigOf("NodeB1");
                try {
                    System.out.println("Trying to connect to NodeB1");

                    communicationManagers.put("NodeB1",
                            new CommunicationServers());

                    this.sender.addCommunication(
                            conf.getAddress(),
                            conf.getPort(),
                            this.getCommunicationManagers().get("NodeB1"));

                    System.out.println("connected to NodeB1");
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

        while(CoreCommunication.running){

            // SEND PACKET
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            if (myConfig.getName().equals("NodeA2") || myConfig.getName().equals("NodeA3")){
                if ((ResourceManager.get().getUpdateCounter() % 10 == 0)
                        && ResourceManager.get().getUpdateCounter() != 0
                ) {

                    assert comAux != null;
                    System.out.println("sending the information down to " + destination.getName());

                    int[] testArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                    comAux.setPacket(new Packet(
                            destination,
                            myConfig,
                            Protocols.UPDATE_LAYER1,
                            ResourceManager.get().getArrayValues()

                    ));


                    comAux.setSend();

                    ResourceManager.get().resetUpdateCounter();

                }
            }
        }

        this.receiver.stopServer();
        this.sender.closeCommunications();

    }
}
