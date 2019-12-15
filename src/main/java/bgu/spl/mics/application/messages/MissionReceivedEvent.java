package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class MissionReceivedEvent implements Event<Boolean> {
    private String senderId;

    public MissionReceivedEvent(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }
}
