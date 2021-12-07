import Network.Packets.Packet;
import Network.ServerSide.Communication;

import java.util.LinkedList;

public class CommunicationClients implements Communication {
    private boolean send;
    private Packet packet;
    private LinkedList<Packet> queuedPackets;


    public CommunicationClients() {
        this.send = false;
        this.packet = null;
        this.queuedPackets = new LinkedList<>();
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

    @Override
    public boolean wantToSend(){return isSend();}

    @Override
    public Packet sendData() {
        this.send = false;
        return this.packet;
    }

    public boolean isMessagesAvailable(){
        return !this.queuedPackets.isEmpty();
    }

    public Packet[] getMessageFromQueue(){
        Packet[] P =this.queuedPackets.toArray(Packet[]::new);
        this.queuedPackets = new LinkedList<>();
        return P;
    }
}
