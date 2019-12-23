package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class SendAgentsEvent implements Event<Boolean> {

    private List<String> serialNumbers;
    private int duration;
    private int expired;

    public SendAgentsEvent(List<String> serialNumbers, int duration, int expired) {
        this.serialNumbers = serialNumbers;
        this.duration = duration;
        this.expired = expired;
    }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    public int getDuration() {
        return duration;
    }

    public int getExpired(){
        return expired;
    }
}
