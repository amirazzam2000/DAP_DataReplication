package ClientCommunication;

import Network.ClientSide.Client;
import Network.Packets.Command;
import Network.Packets.Packet;
import Network.Packets.Protocols;
import Network.ServerSide.Communication;

import java.io.IOException;
import java.util.LinkedList;

public class CommunicationClients implements Communication {
    private boolean send;
    private Packet packet;
    private final ResourceManager resourceManager;
    private Client sender;

    public CommunicationClients() {
        this.send = false;
        this.packet = null;
        this.resourceManager = ResourceManager.get();
        sender = new Client();

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
        System.out.println("incoming message to the client interface from " + inputPacket.getSource().getName());
        switch (inputPacket.getProtocolID())
        {
            case Protocols.CLIENT_READ:
                if (inputPacket.getData() instanceof Command){
                    Command c = (Command) inputPacket.getData();

                    if(c.getOperation() == Command.Operation.READ){

                        return new Packet(
                                inputPacket.getSource(),
                                inputPacket.getDestination(),
                                Protocols.ACK,
                                resourceManager.readValue(c.getPosition()));

                    }
                }
                break;

            case Protocols.CLIENT_WRITE:
                if (inputPacket.getData() instanceof Command){
                    Command c = (Command) inputPacket.getData();

                    if(c.getOperation() == Command.Operation.WRITE){
                        resourceManager.updateValue(c.getPosition(),c.getValue());

                        return new Packet(
                                inputPacket.getSource(),
                                inputPacket.getDestination(),
                                Protocols.ACK,
                                resourceManager.readValue(c.getPosition()));
                    }
                }

                break;
        }

        return null;
    }

    @Override
    public boolean wantToSend(){return isSend();}

    @Override
    public Packet sendData() {
        this.send = false;
        return this.packet;
    }

}
