package Network.Packets;

import GlobalResources.Config;

import java.io.Serializable;

public class Packet implements Serializable {

    private Config destination;
    private Config source;

    private int protocolID;
    private Object data;

    public Packet(Config destination, Config source, int protocolID,
                  Object data) {
        this.destination = destination;
        this.source = source;
        this.protocolID = protocolID;
        this.data = data;
    }

    public Packet copy(){
        return new Packet(this.destination, this.source, this.protocolID,
                this.data);
    }

    public Config getDestination() {
        return destination;
    }

    public void setDestination(Config destination) {
        this.destination = destination;
    }

    public Config getSource() {
        return source;
    }

    public void setSource(Config source) {
        this.source = source;
    }

    public int getProtocolID() {
        return protocolID;
    }

    public void setProtocolID(int protocolID) {
        this.protocolID = protocolID;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
