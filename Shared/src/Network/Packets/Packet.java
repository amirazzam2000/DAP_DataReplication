package Network.Packets;

import GlobalResources.Config;
import GlobalResources.IConfig;

import java.io.Serializable;

public class Packet implements Serializable {

    private IConfig destination;
    private IConfig source;

    private int protocolID;
    private Object data;

    public Packet(IConfig destination, IConfig source, int protocolID,
                  Object data) {
        this.destination = destination;
        this.source = source;
        this.protocolID = protocolID;
        this.data = data;
    }

    public Packet copy(){
        return new Packet(destination, source, this.protocolID,
                this.data);
    }

    public IConfig getDestination() {
        return destination;
    }

    public void setDestination(IConfig destination) {
        this.destination = destination;
    }

    public IConfig getSource() {
        return source;
    }

    public void setSource(IConfig source) {
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
