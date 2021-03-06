package ClientCommunication;

import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import Network.ServerSide.Server;

import java.util.concurrent.TimeUnit;

public class ClientManager implements Runnable{
    private Server receiver;
    private ConnectionConfig connectionConfig;
    private Config myConfig;

    public static boolean running = true;


    public ClientManager(Server receiver,
                         String myName,
                         ConnectionConfig connectionConfig,
                         Config myConfig) {

        System.out.println("-------------- " + myConfig.getPid());
        this.receiver = receiver;
        this.connectionConfig = connectionConfig;
        this.myConfig = myConfig;

    }

    public ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }

    public Config getMyConfig() {
        return myConfig;
    }


    public void startServerCommunication(Config config) {
        //Server setup
        this.receiver.setPort(config.getPublicPort());
        this.receiver.startServer();


        Thread t = new Thread(this);
        t.start();


    }


    @Override
    public void run() {
        System.out.println("------------Starting------------");

        while(ClientManager.running){

            // SEND PACKET
            try {
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException ignored){}
        }

        this.receiver.stopServer();
    }
}
