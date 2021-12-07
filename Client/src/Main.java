import GlobalResources.Client.ClientConnectionConfig;
import GlobalResources.Config;
import GlobalResources.ConnectionConfig;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        ConnectionConfig auxConnectionConfig = new ConnectionConfig();
        ClientConnectionConfig clientConfig = new ClientConnectionConfig();

        final String[] clientNames ={"C0","C1"};

        ArrayList<Config>[] layers = (ArrayList<Config>[])new ArrayList[3];

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
                for(Config c : auxConnectionConfig.getConfigList()){
                    if (layers[c.getLayer()] == null)
                        layers[c.getLayer()] = new ArrayList<>();

                    layers[c.getLayer()].add(c);
                }

                String[] values = new String[0];
                try {
                    String file = Files.readString(Paths.get("Client/resources" +
                            "/inputC0.txt"));

                    String regex = "(?<=[\\d])(,)(?=[\\d])";
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(file);

                    file = m.replaceAll("/");

                    regex = " ";
                    p = Pattern.compile(regex);
                    m = p.matcher(file);

                    file = m.replaceAll("");


                    System.out.println(file);

                    values  = file.split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Client client = new Client(layers, values,
                        clientConfig.getConfigOf(input));

                client.processTransactions();

            }
        }


    }
}
