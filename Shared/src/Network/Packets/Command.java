package Network.Packets;

import java.io.Serializable;

public class Command implements Serializable {
    public enum Operation {READ, WRITE};
    private Operation op;
    private int position;
    private int value;

    public Command(Operation op, int position, int value) {
        this.op = op;
        this.position = position;
        this.value = value;
    }


    public Operation getOperation() {
        return op;
    }

    public void setOperation(Operation op) {
        this.op = op;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getValue() {
        if (op == Operation.READ)
            return -1;
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
