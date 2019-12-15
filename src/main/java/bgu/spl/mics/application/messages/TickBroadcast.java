package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private String senderId;
    private int time;

    public TickBroadcast(String senderId, int time) {
        this.senderId = senderId;
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }
}
