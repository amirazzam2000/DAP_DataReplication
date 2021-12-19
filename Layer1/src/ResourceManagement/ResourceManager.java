package ResourceManagement;

import Network.Packets.Command;
import Network.Packets.Protocols;
import ServerCommunications.IServer;

public class ResourceManager implements IResourcesManagement {
    private final int ARRAY_SIZE = 10;
    int[] array = new int[ARRAY_SIZE];
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
        printArray();
    }
}
