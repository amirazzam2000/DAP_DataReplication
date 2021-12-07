package GlobalResources.Client;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ClientConnectionConfig {

    private HashMap<String, ClientConfig> configurations;
    private ClientConfig[] list;

    public ClientConnectionConfig() {
        configurations = new HashMap<>();
        String  configJson = null;
        try {
            configJson = Files.readString(Paths.get("Shared/src" +
                    "/GlobalResources/Client/ClientConfig.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        list = gson.fromJson(configJson, ClientConfig[].class );

        if(list != null){
            for (ClientConfig config: list ) {
                configurations.put(config.getName(), config);
            }
        }
    }


    public ClientConfig getConfigOf(String serverName){
        return configurations.get(serverName);
    }

    public ClientConfig[] getConfigList(){
        return list;
    }

    public static void main(String[] args){
        ClientConnectionConfig cc = new ClientConnectionConfig();
        System.out.println();
    }
}

