package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
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
    private CountDownLatch latch;

    public Moneypenny(String name, CountDownLatch startSignal) {
        super(name);
        this.squad = Squad.getInstance();
        this.latch = startSignal;
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

        if (Integer.parseInt(getName()) % 2 == 0) {
            subscribeEvent(AgentsAvailableEvent.class, callback -> {

                System.out.println("MP" + getName() + ": M" + callback.getSendID() + ", Agents requested !");
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
        } else {
            subscribeEvent(SendAgentsEvent.class, callback -> {
                System.out.println("MP" + getName() + ": M" + callback.getSendID() + ", Sending Agents for: " + (callback.getDuration() * 100) + " Milli");

                if (currTick <= (callback.getExpired() - callback.getDuration())) {
                    squad.sendAgents(callback.getSerialNumbers(), callback.getDuration());
                    complete(callback, true);
                } else complete(callback, false);
            });

            subscribeEvent(ReleaseAgentsEvent.class, callback -> {
                squad.releaseAgents(callback.getSerialNumbers());
                complete(callback, true);
            });
        }
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
