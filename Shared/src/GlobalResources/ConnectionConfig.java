package GlobalResources;

import Network.ClientSide.ClientCommunicationManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.module.Configuration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionConfig {

    private HashMap<String, Config> configurations;
    private Config[] list;

    public ConnectionConfig() {
        configurations = new HashMap<>();
        String  configJson = null;
        try {
            configJson = Files.readString(Paths.get("Shared/src/GlobalResources/Config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        list = gson.fromJson(configJson, Config[].class );

        if(list != null){
            for (Config config: list ) {
                configurations.put(config.getName(), config);
            }
        }
    }


    public Config getConfigOf(String serverName){
        return configurations.get(serverName);
    }

    public Config[] getConfigList(){
        return list;
    }

    public static void main(String[] args){
        ConnectionConfig cc = new ConnectionConfig();
        System.out.println();
    }
}

