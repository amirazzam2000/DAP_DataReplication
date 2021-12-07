import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import Network.ClientSide.Client;
import Network.ServerSide.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ConnectionConfig auxConnectionConfig = new ConnectionConfig();
        final String[] clientNames ={"NodeA1","NodeA2","NodeA3"};

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
                        clientNames) {
                    auxCommunicationManagers.put(name, new CommunicationServers());
                }

                CoreCommunication lamportMutex = new CoreCommunication(
                        new Server(config.getPort(),
                                auxCommunicationManagers.get(config.getName())),
                        new Client(),
                        clientNames,
                        config.getName(),
                        auxConnectionConfig,
                        config,
                        auxCommunicationManagers
                );

                lamportMutex.startServerCommunication(config, clientNames);

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
