package ServerCommunications;

import Network.Packets.Packet;
import Network.Packets.Protocols;
import Network.ServerSide.Communication;
import ResourceManagement.ResourceManager;

public class CommunicationServers implements Communication {
    private boolean send;
    private Packet packet;
    private final ResourceManager resourceManager;
    private boolean ackFlag;


    public CommunicationServers() {
        this.send = false;
        this.packet = null;
        this.resourceManager = ResourceManager.get();
    }

    public boolean isSend() {return send && packet != null;}

    public void setSend() {
        this.send = true;
    }

    public Packet getPacket() {
        return packet;
    }


    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    @Override
    public Packet analyzeIncomingPacket(Packet inputPacket) {
        System.out.println("incoming message from " + inputPacket.getSource().getName());

        switch (inputPacket.getProtocolID()){
            case Protocols.UPDATE_LAYER2:
                if(inputPacket.getData() instanceof int[]){
                    System.out.println("values received : ");
                    for(int i : (int[])inputPacket.getData()){
                        System.out.print(i + " ");
                    }
                    System.out.println();
                    ResourceManager.get().updateArray((int[])inputPacket.getData());
                }
                break;

        }
        return null;
    }

    public boolean isAckFlag() {
        return ackFlag;
    }

    public void resetAckFlag() {
        this.ackFlag = false;
    }

    @Override
    public boolean wantToSend(){return isSend();}

    @Override
    public Packet sendData() {
        this.send = false;
        return this.packet;
    }

}
