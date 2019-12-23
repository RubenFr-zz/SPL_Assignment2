package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class AgentsAvailableEvent implements Event<Boolean> {
    private String senderId;

    public AgentsAvailableEvent(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return this.senderId;
    }
}
