import GlobalResources.Client.ClientConfig;
import GlobalResources.Config;
import Network.ClientSide.ClientCommunicationManager;
import Network.Packets.Command;
import Network.Packets.Packet;
import Network.Packets.Protocols;
import Network.ServerSide.Server;
import Network.WebSocket.WebSocketClientEndpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client {
    private ArrayList<Config>[] layers;
    private String[] transactions;
    private Network.ClientSide.Client networkClient;
    private CommunicationManager communicationManager;
    private Config destination;
    private ClientConfig myConfig;
    private int[] layersNodeCounts = new int[3];
    private WebSocketClientEndpoint webSocket;

    public Client(ArrayList<Config>[] layers, String[] transactions,
                  ClientConfig myConfig) {
        this.layers = layers;
        this.transactions = transactions;
        networkClient = new Network.ClientSide.Client();
        communicationManager = new CommunicationManager();
        this.myConfig = myConfig;

        layersNodeCounts[0] = 3;
        layersNodeCounts[1] = 2;
        layersNodeCounts[2] = 2;

        System.out.println("My Port is : " + myConfig.getPort());
        try {
            webSocket = new WebSocketClientEndpoint(new URI("ws://localhost:8080/socket"));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void stopClient(){
        this.networkClient.closeCommunications();
    }

    public void processTransactions(){

        if(transactions.length > 0) {

            for (String value : transactions ) {
                switch (value.charAt(0)){

                    case 'b':
                        if(value.length()>1) {
                            int layer = Integer.parseInt(value.charAt(1) + "");

                            System.out.println("layer " + layer);
                            destination =
                                    layers[layer].get(rand.nextInt(layersNodeCounts[layer]));

                        }
                        else{
                            System.out.println("no layer specified!");
                             destination = layers[0].get(rand.nextInt
                             (layersNodeCounts[0]));


                        }
                        try {
                            networkClient.addCommunication(
                                    destination.getAddress(),
                                    destination.getPublicPort(),
                                    communicationManager);
                            System.out.println("added Connection!");
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            }catch (InterruptedException ignored){}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 'r':
                        int read = Integer.parseInt(value.substring(2,
                                value.length()-1));

                        communicationManager.resetAckFlag();
                        communicationManager.setPacket(
                                new Packet(
                                        destination,
                                        myConfig,
                                        Protocols.CLIENT_READ,
                                        new Command(
                                                Command.Operation.READ,
                                                read,
                                                -1
                                        )
                                )
                        );
                        communicationManager.setSend();
                        System.out.println("read : " + read);

                        int nodeId =
                                destination.getName().charAt(destination.getName().length() - 1) - '0' - 1;
                        sendInfoToWebSocket(read, destination.getLayer(), nodeId);

                        while(!communicationManager.isAckFlag()){
                            try {
                                TimeUnit.SECONDS.sleep(3);
                                System.out.println("waiting for ACK");
                            }catch (InterruptedException ignored){}
                        }
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        }catch (InterruptedException ignored){}

                        sendInfoToWebSocket(read,
                                communicationManager.getValue() , destination.getLayer(), nodeId);

                        try {
                            TimeUnit.SECONDS.sleep(3);
                        }catch (InterruptedException ignored){}



                        break;

                    case 'w':
                        String[] aux = value.substring(2,
                                value.length()-1).split("/");
                        if(aux.length == 2){
                            int pos = Integer.parseInt(aux[0]);
                            int writeValue = Integer.parseInt(aux[1]);

                            communicationManager.resetAckFlag();
                            communicationManager.setPacket(
                                    new Packet(
                                            destination,
                                            myConfig,
                                            Protocols.CLIENT_WRITE,
                                            new Command(
                                                    Command.Operation.WRITE,
                                                    pos,
                                                    writeValue
                                            )
                                    )
                            );
                            communicationManager.setSend();

                            System.out.println("write: " + writeValue + " at " + pos );

                            while(!communicationManager.isAckFlag()){
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                    System.out.println("waiting for ACK");
                                }catch (InterruptedException ignored){}
                            }



                        }

                        break;

                    case 'c':
                        System.out.println("endTransaction");
                        networkClient.closeCommunications();
                        break;

                    default:
                        System.out.println("Unexpected value: " + value.charAt(0));
                }
            }
        }
    }

    private void sendInfoToWebSocket(int position, int layer, int nodeId){
        StringBuilder json = new StringBuilder();

        json.append("{\"Layer\": \"").append(4).append("\",");

        json.append("\"Operating Layer\": \"").append(layer).append("\",");


        json.append("\"NodeId\": \"").append(nodeId).append("\",");

        json.append("\"Operation\": \"").append("Request").append("\",");

        json.append("\"Position\": \"").append(position).append("\"");

        json.append("}");
        webSocket.sendMessage(json.toString());
    }

    private void sendInfoToWebSocket(int position, int value, int layer, int nodeId){
        StringBuilder json = new StringBuilder();

        json.append("{\"Layer\": \"").append(4).append("\",");

        json.append("\"Operating Layer\": \"").append(layer).append("\",");

        json.append("\"NodeId\": \"").append(nodeId).append("\",");

        json.append("\"Operation\": \"").append("Answer").append("\",");

        json.append("\"Position\": \"").append(position).append("\",");

        json.append("\"Value\": \"").append(value).append("\"");

        json.append("}");
        webSocket.sendMessage(json.toString());
    }

}
