package Network.ServerSide;

import Network.Packets.Packet;

public interface Communication{

    Packet analyzeIncomingPacket(Packet inputPacket);
    boolean wantToSend();
    Packet sendData();

}
