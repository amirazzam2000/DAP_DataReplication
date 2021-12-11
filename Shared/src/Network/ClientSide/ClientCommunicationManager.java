package Network.ClientSide;

import Network.Packets.Packet;
import Network.ServerSide.Communication;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class ClientCommunicationManager extends Thread {
    private DataOutputStream dos;
    private ObjectOutputStream oos;
    private DataInputStream dis;
    private ObjectInputStream ois;
    private boolean running;

    private int token;
    private boolean haveToken;
    private Client client;

    private Communication communication;

    private int connectToPort;
    private String connectToAddress;
    private boolean isOn;

    private BufferedInputStream buffIn;


    public ClientCommunicationManager(String address, int port,
                                      Communication communication) throws IOException {
        this.connectToPort = port;
        this.connectToAddress = address;
        this.communication = communication;
        this.isOn = false;
        startConnection();
    }

    public Communication getCommunication() {
        return communication;
    }

    public boolean isOn() {
        return isOn;
    }

    /**
     * starts the connection with the server
     */
    public void startConnection(){
        running = true;
        start();
    }

    /**
     * disconnect from the server
     */
    public void disconnectConnection(){
        running = false;
        interrupt();
    }

    private void establishConnection() throws IOException {
        Socket socket = null;
        boolean connectionRefused = true;

        while(connectionRefused){
            connectionRefused = false;
            try{
                socket = new Socket(this.connectToAddress, this.connectToPort);
            }catch (ConnectException e) {
                connectionRefused = true;
            }
        }
        System.out.println("Communication started");

        dos = new DataOutputStream(socket.getOutputStream());
        oos = new ObjectOutputStream(socket.getOutputStream());

        InputStream in = socket.getInputStream();
        buffIn = new BufferedInputStream(in);
        ois = new ObjectInputStream(buffIn);
        dis = new DataInputStream(socket.getInputStream());
    }
    @Override
    public void run () {
        try {
            this.establishConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isOn = true;
        Packet output;
        while(running){
            try {
                if(buffIn.available() > 0){
                    System.out.println("there is data to read!");
                    Object packet = ois.readObject();
                    if(packet instanceof Packet ){
                        output =
                                communication.analyzeIncomingPacket((Packet) packet);

                        if (output != null){
                            send(output);
                        }
                    }
                }
                else if(communication.wantToSend()){
                    send(communication.sendData());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(Packet output) throws IOException {
        System.out.println("sending packets to " + output.getDestination().getName() );
        oos.writeObject(output);
    }
}
