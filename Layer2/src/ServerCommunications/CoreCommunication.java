package ServerCommunications;

import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import Network.ClientSide.Client;
import Network.ClientSide.ClientCommunicationManager;
import Network.Packets.Packet;
import Network.ServerSide.Server;

import java.io.IOException;
import java.util.HashMap;
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
    final String[] clientNames ={"NodeC1","NodeC2"};


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

    public boolean checkValidServerName(String name) {
        if (name.equals(myConfig.getName()))
            return false;

        for(String n : clientNames){
            if(n.equals(name))
                return true;
        }
        return false;
    }

    public void startServerCommunication(Config config, String[] clientNames) {
        //Server setup
        this.receiver.setPort(config.getPort());
        this.receiver.startServer();

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
            }catch (InterruptedException ignored){}

        }

        this.receiver.stopServer();
        this.sender.closeCommunications();

    }

    @Override
    public void broadcastMsg(int protocol, Object data) {}
}
