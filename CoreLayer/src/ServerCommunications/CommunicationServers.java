package ServerCommunications;

import Network.Packets.Command;
import Network.Packets.Packet;
import Network.Packets.Protocols;
import Network.ServerSide.Communication;
import ResourceManagement.IResourcesManagement;
import ResourceManagement.ResourceManager;

import java.util.LinkedList;

public class CommunicationServers implements Communication {
    private boolean send;
    private Packet packet;
    private LinkedList<Packet> queuedPackets;


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
        this.queuedPackets.add(inputPacket);
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
