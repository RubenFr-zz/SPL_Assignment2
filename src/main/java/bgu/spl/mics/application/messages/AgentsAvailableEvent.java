package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AgentsAvailableEvent implements Event<HashMap<String, Object>> {
    private String senderId;
    private List<String> serialNumbers;
    private int duration;
    private int expired;

    public AgentsAvailableEvent(String senderId, List<String> serialNumbers, int duration, int expired) {
        this.senderId = senderId;
        this.serialNumbers = serialNumbers;
        this.duration = duration;
        this.expired = expired;
    }

    public String getSenderId() {
        return senderId;
    }

    public List<String> getSerialNumbers() {
        return this.serialNumbers;
    }

    public int getDuration(){
        return this.duration;
    }

    public int getExpired() {
        return expired;
    }
}
