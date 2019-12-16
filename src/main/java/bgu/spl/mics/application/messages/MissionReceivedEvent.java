package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class MissionReceivedEvent implements Event<Boolean> {

    private String senderId;
    private MissionInfo mission;

    public MissionReceivedEvent(String senderId, MissionInfo mission) {
        this.senderId = senderId;
        this.mission = mission;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public MissionInfo getMission() {
        return this.mission;
    }
}
