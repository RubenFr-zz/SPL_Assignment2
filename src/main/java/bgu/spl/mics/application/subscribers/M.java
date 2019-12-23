package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

    private Diary diary;
    private int currTick;
    private int QTime;

    public M(String name, CountDownLatch latch) {
        super(name, latch);
        this.diary = Diary.getInstance();

    }

    /**
     * As said on the forum we chose to first request the GadgetAvailable and then the AgentsAvailable
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback -> currTick = callback.getTick());

        subscribeBroadcast(TerminationBroadcast.class, callback -> this.terminate());

        subscribeEvent(MissionReceivedEvent.class, callback -> {

            MissionInfo mission = callback.getMission();
            System.out.println("Mission Received: " + mission.getMissionName() + ", by: " + getName());
            int timeExpired = mission.getTimeExpired();

            diary.increment();

            try {
                if (currTick <= timeExpired) {
                    Future<HashMap<String, Object>> fut1 = askAgents(mission);

                    if (fut1 != null && (boolean) fut1.get().get("Acquired") && currTick <= timeExpired) {
                        Future<Boolean> fut2 = askGadget(mission);

                        if (fut2 != null && fut2.get() && currTick <= timeExpired) {
                            Future<Boolean> fut3 = sendAgents(mission);

                            if (fut3 != null && fut3.get() && currTick <= timeExpired)
                                diary.addReport(fillReport(mission, fut1.get()));
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private Future<HashMap<String, Object>> askAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new AgentsAvailableEvent(mission.getSerialAgentsNumbers(), mission.getTimeExpired())
        );
    }

    private Future<Boolean> askGadget(MissionInfo mission) {
        Future<Boolean> future = getSimplePublisher().sendEvent(new GadgetAvailableEvent(getName(), mission.getGadget()));
        QTime = currTick;
        return future;
    }

    private Future<Boolean> sendAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new SendAgentsEvent(mission.getSerialAgentsNumbers(), mission.getDuration(), mission.getTimeExpired())
        );
    }

    @SuppressWarnings(value = "unchecked")
    private Report fillReport(MissionInfo mission, HashMap<String, Object> fut) {
        Report report = new Report();

        report.setAgentsSerialNumbers(mission.getSerialAgentsNumbers());
        report.setGadgetName(mission.getGadget());
        report.setM(Integer.parseInt(this.getName()));
        report.setMissionName(mission.getMissionName());
        report.setTimeCreated(currTick);
        report.setTimeIssued(mission.getTimeIssued());
        report.setAgentsNames((List<String>) fut.get("AgentsName"));
        report.setMoneypenny(Integer.parseInt((String) fut.get("MonneyPenny")));
        report.setQTime(this.QTime);

        return report;
    }
}
