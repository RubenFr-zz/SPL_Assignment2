package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.MpFlag;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.HashMap;
import java.util.LinkedList;
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
    private final HashMap<MissionInfo, LinkedList<Future>> futureHashMap;
    private CountDownLatch latch;

    public M(String name, CountDownLatch startSignal) {
        super(name);
        this.diary = Diary.getInstance();
        this.futureHashMap = new HashMap<>();
        this.latch = startSignal;

    }

    /**
     * As said on the forum we chose to first request the GadgetAvailable and then the AgentsAvailable
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback -> currTick = callback.getTick());

        subscribeBroadcast(TerminationBroadcast.class, callback -> {
            synchronized (futureHashMap) {
                for (LinkedList<Future> list : futureHashMap.values())
                    for (Future fut : list)
                        if (fut != null && !fut.isDone())
                            fut.resolve(null);
                this.terminate();
            }
        });

        subscribeEvent(MissionReceivedEvent.class, callback -> {

            MissionInfo mission = callback.getMission();
            System.out.println("Mission Received: " + mission.getMissionName() + ", by: M" + getName());
            int timeExpired = mission.getTimeExpired();
            futureHashMap.putIfAbsent(mission, new LinkedList<Future>());

            diary.increment();

            if (currTick <= timeExpired) {
                Future<HashMap<String, Object>> fut1 = askAgents(mission);
                futureHashMap.get(mission).add(fut1);

                if (fut1 != null && fut1.get() != null && (boolean) fut1.get().get("Acquired") && currTick <= timeExpired) {
                    System.out.println("M" + getName() + ", agents acquired !");
                    Future<Boolean> fut2 = askGadget(mission);
                    futureHashMap.get(mission).add(fut2);

                    if (fut2 != null && fut2.get() != null && fut2.get() && currTick <= timeExpired) {
                        System.out.println("M" + getName() + ", gadget acquired !");
                        Future<Boolean> fut3 = sendAgents(mission);
                        futureHashMap.get(mission).add(fut3);

                        if (fut3 != null && fut3.get() != null && fut3.get() && currTick <= timeExpired) {
                            System.out.println("M" + getName() + ", Mission succeeded !");
                            diary.addReport(fillReport(mission, fut1.get()));
                        }

                    } else {
                        System.out.println("M" + getName() + ", didn't found the gadget, mission aborted !");
                        Future<Boolean> fut4 = releaseAgents(mission);
                        futureHashMap.get(mission).add(fut4);
                    }
                } else {
                    System.out.println("M" + getName() + ", didn't acquired the agents, mission aborted !");
//                    Future<Boolean> fut4 = releaseAgents(mission);
//                    futureHashMap.get(mission).add(fut4);
                }
            }
        });

        latch.countDown();
    }

    private Future<HashMap<String, Object>> askAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new AgentsAvailableEvent(getName(), mission.getSerialAgentsNumbers(), mission.getTimeExpired(), MpFlag.GETTING_AGENTS)
        );
    }

    private Future<Boolean> askGadget(MissionInfo mission) {
        Future<Boolean> future = getSimplePublisher().sendEvent(new GadgetAvailableEvent(getName(), mission.getGadget()));
        QTime = currTick;
        return future;
    }

    private Future<Boolean> sendAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new SendAgentsEvent(getName(), mission.getSerialAgentsNumbers(), mission.getDuration(), mission.getTimeExpired(), MpFlag.SENDING_AGENTS)
        );
    }

    private Future<Boolean> releaseAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new ReleaseAgentsEvent(mission.getSerialAgentsNumbers())
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
        report.setMoneypenny(Integer.parseInt((String) fut.get("MoneyPenny")));
        report.setQTime(this.QTime);

        return report;
    }
}
