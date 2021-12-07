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

        ArrayList<Config>[] layers = (ArrayList<Config>[])new ArrayList[3];

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
        int layer = -1;


        if(values.length > 0) {

            for (String value : values ) {
                switch (value.charAt(0)){

                    case 'b':
                        if(value.length()>1) {
                            layer = Integer.parseInt(value.substring(1,
                                    value.length() - 1));
                            System.out.println("layer " + layer);
                        }
                        else
                            System.out.println("no layer specified!");
                        break;
                    case 'r':
                        int read = Integer.parseInt(value.substring(2,
                                value.length()-1));

                        System.out.println("read : " + read);
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
                        break;

                    default:
                        System.out.println("Unexpected value: " + value.charAt(0));
                }
           }
        }
    }
}
