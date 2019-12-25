package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class ReleaseAgentsEvent implements Event<Boolean> {
     private List<String> serialNumbers;

     public ReleaseAgentsEvent(List<String> serials){
         this.serialNumbers = serials;
     }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }
}
