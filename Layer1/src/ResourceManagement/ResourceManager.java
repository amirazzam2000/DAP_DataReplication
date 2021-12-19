package ResourceManagement;

import GlobalResources.Config;
import Network.Packets.Command;
import Network.Packets.Protocols;
import ServerCommunications.IServer;

public class ResourceManager implements IResourcesManagement {
    private final int ARRAY_SIZE = 10;
    int[] array = new int[ARRAY_SIZE];
    private IServer callbackServer;
    private static ResourceManager resourceManager;
    private WebSocketClientEndpoint webSocket;
    private Config serverConfig;

    public ResourceManager() {
        try {
            webSocket = new WebSocketClientEndpoint(new URI("ws://localhost:8080/socket"));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Config getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(Config serverConfig) {
        this.serverConfig = serverConfig;
    }

    public static ResourceManager get(){
        if(ResourceManager.resourceManager == null){
            ResourceManager.resourceManager = new ResourceManager();
        }
        return ResourceManager.resourceManager;
    }

    public void setCallbackServer(IServer callbackServer) {
        this.callbackServer = callbackServer;
    }
    private void printArray(){
        for(int i : array){
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public int [] getArrayValues(){
        System.out.print("array values being sent: ");
        printArray();
        return array.clone();
    }

    @Override
    public int updateValue(int position, int value) {
        if (this.callbackServer == null) return -2;
        if (position > array.length - 1 ) return -1;

        array[position] = value;
        callbackServer.broadcastMsg(Protocols.EXECUTE,
                new Command(Command.Operation.WRITE, position, value));

        printArray();
        return array[position];
    }

    @Override
    public int readValue(int position) {
        printArray();
        return position > array.length - 1 ? -1 : array[position];
    }

    @Override
    public int updateValuePrivately(int position, int value) {
        if (position > array.length - 1 ) return -1;

        array[position] = value;
        printArray();
        return array[position];
    }

    @Override
    public void updateArray(int[] array) {
        this.array = new int[this.ARRAY_SIZE];
        this.array = array;
        System.out.println("values received : ");
        for(int i : array){
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.println("values saved: ");
        sendInfoToWebSocket();
        printArray();
    }

    private void sendInfoToWebSocket(){
        int nodeId =
                serverConfig.getName()
                        .charAt(serverConfig.getName().length() - 1 ) - '0'
                        - 1;

        StringBuilder json = new StringBuilder();
        json.append("{\"Layer\": \"").append(serverConfig.getLayer()).append(
                "\",");
        json.append("\"NodeId\": \"").append(nodeId).append("\",");

        json.append("\"Array\": [ ");
        for(int i=0; i<10; i++){
            json.append("{").append("\"id\":\"").append(i).append("\", " +
                    "\"value" +
                    "\": \"").append(array[i]).append("\"}");
            if ( i != 9){
                json.append(",");
            }

        }

        json.append("]}");
        webSocket.sendMessage(json.toString());
    }
}
