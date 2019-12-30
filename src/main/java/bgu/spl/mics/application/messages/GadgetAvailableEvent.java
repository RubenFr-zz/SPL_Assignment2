package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.Map;

public class GadgetAvailableEvent implements Event<Map.Entry<Boolean, Integer>> {
    private String senderId;
    private String gadget;

    public GadgetAvailableEvent(String senderId, String gadget) {
        this.senderId = senderId;
        this.gadget = gadget;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getGadget(){
        return this.gadget;
    }
}
