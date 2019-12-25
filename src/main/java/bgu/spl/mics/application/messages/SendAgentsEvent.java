package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MpFlag;

import java.util.List;

public class SendAgentsEvent implements Event<Boolean> {

    private String sendID;
    private List<String> serialNumbers;
    private int duration;
    private int expired;
    private MpFlag flag;

    public SendAgentsEvent(String sendID, List<String> serialNumbers, int duration, int expired, MpFlag flag) {
        this.sendID = sendID;
        this.serialNumbers = serialNumbers;
        this.duration = duration;
        this.expired = expired;
        this.flag = flag;
    }

    public String getSendID() {
        return sendID;
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

    public MpFlag getFlag() {
        return flag;
    }
}
