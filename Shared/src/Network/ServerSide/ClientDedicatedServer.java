package Network.ServerSide;

import Network.Packets.Packet;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientDedicatedServer extends Thread {

    // to read and write objects on the channel
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    // to read and write primitive data on the channel
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private boolean isRunning;
    private Communication communication;

    public ClientDedicatedServer(Socket socket,
                                 ArrayList<ClientDedicatedServer> dedicatedServers,
                                 Communication communication) throws IOException {
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        isRunning = true;
        this.communication = communication;

        System.out.println("Socket Opened!");

    }

    @Override
    public void run() {
        Packet output;
        while(isRunning){
            try {
                Object packet;
                try{
                    packet = objectInputStream.readObject();
                }catch (SocketException e){
                    this.stopDedicatedServer();
                    System.out.println("Socket connection closed");
                    break;
                }

                if(packet instanceof Packet){
                    assert packet != null;
                    output =
                            communication.analyzeIncomingPacket((Packet) packet);

                    if (output != null){
                        send(output);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Packet output) throws IOException {
        objectOutputStream.writeObject(output);
    }


    public void stopDedicatedServer(){
        isRunning = false;
    }
}
