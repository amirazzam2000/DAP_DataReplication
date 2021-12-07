package Network.ClientSide;

import Network.ServerSide.Communication;

import java.io.IOException;
import java.util.ArrayList;

public class Client {

    private ArrayList<ClientCommunicationManager> communicationServers;


    public Client() {
        this.communicationServers = new ArrayList<>();
    }

    public void addCommunication(String address, int port,
                                 Communication communication) throws IOException {

        this.communicationServers.add(new ClientCommunicationManager(address,
                port, communication));

    }

    public ArrayList<ClientCommunicationManager> getActiveCommunications(){
        return this.communicationServers;
    }


    public void closeCommunications() {
        for(ClientCommunicationManager cs: communicationServers){
            cs.disconnectConnection();
        }
    }
}
