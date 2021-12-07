package Clocks;

import java.util.HashMap;

public class DirectClock {
    public HashMap<String,Integer> clock;
    public String myId;

    public DirectClock(String id){
        myId = id;
        clock = new HashMap<>();
        clock.put(id, 1);
    }

    public DirectClock(String id, Integer initial_clocks){
        myId = id;
        clock = new HashMap<>();
        clock.put(id, initial_clocks);
    }

    public int getValue(String id){
        return clock.containsKey(id)? clock.get(id) : 0;
    }

    public void ticks(){
        clock.put(myId, clock.get(myId) + 1);
    }

    public void sendAction(){
       this.ticks();
    }

    public void receiveAction(String senderID, int sentValue) {
        clock.put(senderID, sentValue);
        clock.put(myId, clock.get(myId) > clock.get(senderID)?
                clock.get(myId) + 1: clock.get(senderID) +1);
    }
}
