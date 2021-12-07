import GlobalResources.Client.ClientConfig;
import GlobalResources.Config;
import Network.ClientSide.ClientCommunicationManager;
import Network.Packets.Command;
import Network.Packets.Packet;
import Network.Packets.Protocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Client {
    private ArrayList<Config>[] layers;
    private String[] transactions;
    private Network.ClientSide.Client networkClient;
    private CommunicationManager communicationManager;
    private Config destination;
    private ClientConfig myConfig;

    public Client(ArrayList<Config>[] layers, String[] transactions,
                  ClientConfig myConfig) {
        this.layers = layers;
        this.transactions = transactions;
        networkClient = new Network.ClientSide.Client();
        communicationManager = new CommunicationManager();
        this.myConfig = myConfig;
    }

    public void processTransactions(){

        if(transactions.length > 0) {

            for (String value : transactions ) {
                switch (value.charAt(0)){

                    case 'b':
                        if(value.length()>1) {
                            int layer = Integer.parseInt(value.substring(1,
                                    value.length() - 1));

                            System.out.println("layer " + layer);
                            destination =layers[layer].get(0);

                        }
                        else{
                            System.out.println("no layer specified!");
                            destination =layers[0].get(0);
                        }
                        try {
                            networkClient.addCommunication(
                                    destination.getAddress(),
                                    destination.getPublicPort(),
                                    communicationManager);
                            System.out.println("added Connection!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 'r':
                        int read = Integer.parseInt(value.substring(2,
                                value.length()-1));
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
                            System.out.println("write: " + writeValue + " at " + pos );
                        }

                        break;

                    case 'c':
                        System.out.println("end");
                        for (ClientCommunicationManager connection:
                             networkClient.getActiveCommunications()
                             ) {
                            connection.disconnectConnection();
                        }
                        break;

                    default:
                        System.out.println("Unexpected value: " + value.charAt(0));
                }
            }
        }
    }

}
