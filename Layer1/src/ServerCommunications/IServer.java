package ServerCommunications;

public interface IServer {
    void broadcastMsg(int protocol, Object data);
}
