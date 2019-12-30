package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MpFlag;

import java.util.HashMap;
import java.util.List;

public class AgentsAvailableEvent implements Event<HashMap<String, Object>> {

    private String sendID;
    private List<String> serialNumbers;
    private int expired;

    public AgentsAvailableEvent(String sendID, List<String> serialNumbers, int expired) {
        this.sendID = sendID;
        this.serialNumbers = serialNumbers;
        this.expired = expired;
    }

    public List<String> getSerialNumbers() {
        return this.serialNumbers;
    }

    public int getExpired() {
        return expired;
    }


    public String getSendID() {
        return sendID;
    }
}
