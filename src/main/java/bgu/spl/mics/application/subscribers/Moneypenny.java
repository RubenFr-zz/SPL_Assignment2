package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.SendAgentsEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.HashMap;
import java.util.LinkedList;
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
    private int currTick;

    public Moneypenny(String name, CountDownLatch latch) {
        super(name, latch);
        this.squad = Squad.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback -> {
            currTick = callback.getTick();
        });

        subscribeBroadcast(TerminationBroadcast.class, callback -> {
            HashMap<String, Agent> agents = (squad.getAgents());
            List<String> serials = new LinkedList<>();
            for (Agent agent : agents.values())
                serials.add(agent.getSerialNumber());
            squad.releaseAgents(serials);

            terminate();

        });

        subscribeEvent(AgentsAvailableEvent.class, callback -> {

            System.out.println("Agents requested");
            List<String> agents = callback.getSerialNumbers();
            boolean acquired = false;

            if (currTick <= callback.getExpired()) {
                try {
                    acquired = squad.getAgents(agents);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            complete(callback, getResult(agents, acquired));

        });

        subscribeEvent(SendAgentsEvent.class, callback -> {
            System.out.println("Sending Agents");

            if (currTick <= (callback.getExpired() - callback.getDuration())) {
                long timeIn = System.currentTimeMillis();
                squad.sendAgents(callback.getSerialNumbers(), callback.getDuration());
                System.out.println("Time taken (in Milli): " + (System.currentTimeMillis() - timeIn));
                complete(callback, true);
            } else complete(callback, false);
        });
    }

    private HashMap<String, Object> getResult(List<String> agents, boolean acquired) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Acquired", acquired);
        if (acquired) {
            result.put("MonneyPenny", getName());
            result.put("AgentsName", squad.getAgentsNames(agents));
        }
        return result;
    }
}
