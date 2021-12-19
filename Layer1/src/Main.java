import ClientCommunication.ClientManager;
import ClientCommunication.CommunicationClients;
import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import Network.ClientSide.Client;
import Network.ServerSide.Server;
import ResourceManagement.ResourceManager;
import ServerCommunications.CommunicationServers;
import ServerCommunications.CoreCommunication;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ConnectionConfig auxConnectionConfig = new ConnectionConfig();
        final String[] clientNames ={"NodeB1","NodeB2"};
        final String[] clientsToConnectTo ={"NodeC1","NodeC2"};

        System.out.println("welcome!");

        if(args.length < 1){
            System.out.println("you need to select the node name");
        }
        else{
            boolean correct = false;
            String input = args[0];
            for (String name : clientNames){
                if (input.equals(name)) {
                    correct = true;
                    break;
                }
            }
            if (correct){
                Config config = auxConnectionConfig.getConfigOf(input);


                System.out.println("I'm process pid : " + config.getPid());

                HashMap<String, CommunicationServers> auxCommunicationManagers = new HashMap<>();

                for (String name :
                        clientsToConnectTo) {
                    auxCommunicationManagers.put(name, new CommunicationServers());
                }
                auxCommunicationManagers.put(config.getName(),
                        new CommunicationServers());

                CoreCommunication server = new CoreCommunication(
                        new Server(config.getPort(),
                                    auxCommunicationManagers.get(config.getName())),
                        new Client(),
                        clientNames,
                        auxConnectionConfig,
                        config,
                        auxCommunicationManagers
                );
                ResourceManager.get().setCallbackServer(server);
                ResourceManager.get().setServerConfig(config);

                ClientManager client = new ClientManager(
                        new Server(
                                config.getPublicPort(),
                                new CommunicationClients()
                                ),
                        config.getName(),
                        auxConnectionConfig,
                        config
                );

                server.startServerCommunication(config, clientsToConnectTo);
                client.startServerCommunication(config);

            }
            else{
                System.out.println("ERROR! Client name not found.");
            }

            while(CoreCommunication.running){
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException ignored){

                }
            }
        }
    }
}
