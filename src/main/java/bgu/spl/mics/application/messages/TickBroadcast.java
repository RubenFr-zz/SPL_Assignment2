package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int currTick;

    public TickBroadcast(int currTick) {
        this.currTick=currTick;
    }

    public final int getTick() {
        return currTick;
    }
}
