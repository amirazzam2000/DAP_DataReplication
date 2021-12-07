package Clocks;

public class LamportClock {

    private int clock;
    
    public LamportClock(){clock = 1;}

    public int getValue(){ return clock;}

    public void tick(){
        clock += 1;
    }

    public void sendAction(){
        clock += 1;
    }

    public void receiveAction(String src, int sentValue ){
        clock = clock >= sentValue? clock + 1  : sentValue + 1;
    }
}
