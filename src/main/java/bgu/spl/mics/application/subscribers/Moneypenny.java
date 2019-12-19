package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
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

    private Squad squad;
    private int currTick;

    public Moneypenny(String name, CountDownLatch latch) {
        super(name, latch);
        this.squad = Squad.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback -> {
            currTick = callback.getTick();
            System.out.println(currTick);
        });

		subscribeBroadcast(TerminationBroadcast.class, callback -> {
			this.terminate();
		});

        subscribeEvent(AgentsAvailableEvent.class, callback -> {

            List<String> agents = callback.getSerialNumbers();
            try {
                boolean acquired = squad.getAgents(agents);

                if (acquired && currTick <= (callback.getExpired() - callback.getDuration())) {
                    long timeIn = System.currentTimeMillis();
                    System.out.println("Tik in: " + currTick);
                    squad.sendAgents(agents, callback.getDuration());
                    System.out.println("Tick out: " + currTick);
                    System.out.println("Time taken (in Milli): " + (System.currentTimeMillis() - timeIn));
                }
                else {
                    acquired = false;
                }
                HashMap<String, Object> toReturn = getResult(agents, acquired);
                complete(callback, toReturn);
            } catch (InterruptedException e) {
                e.printStackTrace();
                complete(callback, null);
            }
        });
    }

    private HashMap<String, Object> getResult(List<String> agents, boolean acquired) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Done", acquired);
        result.put("MonneyPenny", getName());
        result.put("AgentsName", squad.getAgentsNames(agents));
        return result;
    }
}
