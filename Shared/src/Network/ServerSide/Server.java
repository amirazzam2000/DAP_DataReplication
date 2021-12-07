package Network.ServerSide;

import java.util.ArrayList;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private final ArrayList<ClientDedicatedServer> dedicatedServers;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private int port;
    private Communication communication;


    public Server(int port, Communication communication) {
        this.dedicatedServers = new ArrayList<>();
        this.port = port;
        this.communication = communication;
    }

    public void setPort(int port){
        this.port = port;
    }
    /**
     * Starts the server, listening for client connections
     */
    public void startServer() {
        start();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
            isRunning = true;

            while (isRunning) {
                System.out.println("Waiting for a client...");
                Socket socket = serverSocket.accept();

                System.out.println("Client connected");
                ClientDedicatedServer dedicatedServer =
                        new ClientDedicatedServer(socket,
                                dedicatedServers, this.communication);
                dedicatedServers.add(dedicatedServer);
                dedicatedServer.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            for (ClientDedicatedServer ds :
                    dedicatedServers) {
                ds.stopDedicatedServer();
            }
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    for (ClientDedicatedServer ds :
                            dedicatedServers) {
                        ds.stopDedicatedServer();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    for (ClientDedicatedServer ds :
                            dedicatedServers) {
                        ds.stopDedicatedServer();
                    }
                }
            }
        }
    }

    public void stopServer(){
        isRunning = false;
    }
}