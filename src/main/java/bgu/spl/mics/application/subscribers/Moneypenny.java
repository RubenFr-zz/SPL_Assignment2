package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MpFlag;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

    private final Squad squad;
    private final MpFlag type;
    private CountDownLatch latch;

    public Moneypenny(String name, CountDownLatch startSignal, MpFlag mpFlag) {
        super(name);
        this.squad = Squad.getInstance();
        this.latch = startSignal;
        this.type = mpFlag;
    }

    @Override
    protected void initialize() {

        subscribeBroadcast(TerminationBroadcast.class, callback -> {
            squad.setTerminated(true);
            terminate();
        });

        if (type == MpFlag.GETTING_AGENTS) {

            subscribeEvent(AgentsAvailableEvent.class, callback -> {

                List<String> agents = callback.getSerialNumbers();
                java.util.Collections.sort(agents);
                System.out.println("MP" + getName() + ": M" + callback.getSendID() + ", Agents requested ! " + agents);

                try {
                    complete(callback, getResult(agents, squad.getAgents(agents)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        } else if (type == MpFlag.SENDING_AGENTS){

            subscribeEvent(SendAgentsEvent.class, callback -> {
                System.out.println("MP" + getName() + ": M" + callback.getSendID() + ", Sending Agents " + callback.getSerialNumbers()+ " for: " + (callback.getDuration() * 100) + " Milli");
                squad.sendAgents(callback.getSerialNumbers(), callback.getDuration());
                complete(callback, true);
            });

            subscribeEvent(ReleaseAgentsEvent.class, callback -> {
                squad.releaseAgents(callback.getSerialNumbers());
                complete(callback, true);
            });

        } else if (type == MpFlag.RELEASING_AGENTS) {

            subscribeEvent(ReleaseAgentsEvent.class, callback -> {
                squad.releaseAgents(callback.getSerialNumbers());
                complete(callback, true);
            });

        } else System.out.println("ERROR: Moneypenny don't have any type...");

        latch.countDown();
    }

    private HashMap<String, Object> getResult(List<String> agents, boolean acquired) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Acquired", acquired);
        if (acquired) {
            result.put("MoneyPenny", getName());
            result.put("AgentsName", squad.getAgentsNames(agents));
        }
        return result;
    }
}
