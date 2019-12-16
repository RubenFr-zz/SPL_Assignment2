package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.LinkedList;
import java.util.List;

public class AgentsAvailableEvent implements Event<Boolean> {
    private String senderId;
    private List<String> serialNumbers;

    public AgentsAvailableEvent(String senderId, List<String> serialNumbers) {
        this.senderId = senderId;
        this.serialNumbers = serialNumbers;
    }

    public String getSenderId() {
        return senderId;
    }

    public List<String> getSerialNumbers() {
        return this.serialNumbers;
    }
}
