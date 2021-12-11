package ResourceManagement;

import Network.Packets.Command;
import Network.Packets.Protocols;
import ServerCommunications.IServer;

public class ResourceManager implements IResourcesManagement{
    int[] array = new int[10];
    private IServer callbackServer;
    private static ResourceManager resourceManager;

    public ResourceManager() {}

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
}
