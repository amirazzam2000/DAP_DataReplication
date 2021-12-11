package ServerCommunications;

import Network.Packets.Protocols;

public interface IServer {
    void broadcastMsg(int protocol, Object data);
}
