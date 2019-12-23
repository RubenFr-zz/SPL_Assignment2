package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GadgetAvailableEvent implements Event<Boolean> {
    private String senderId;

    public GadgetAvailableEvent(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return this.senderId;
    }
}
