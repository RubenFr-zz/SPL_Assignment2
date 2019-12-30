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
import java.util.Map;
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

            diary.increment();
            MissionInfo mission = callback.getMission();
            futureHashMap.putIfAbsent(mission, new LinkedList<>());

            if (currTick <= callback.getMission().getTimeExpired()) {
                Future<HashMap<String, Object>> fut1 = askAgents(mission);
                futureHashMap.get(mission).add(fut1);

                if (fut1 != null && fut1.get() != null && (boolean) fut1.get().get("Acquired") && currTick <= callback.getMission().getTimeExpired()) {
                    Future<Map.Entry<Boolean, Integer>> fut2 = askGadget(mission);
                    futureHashMap.get(mission).add(fut2);

                    if (fut2 != null && fut2.get() != null && fut2.get().getKey() && currTick <= callback.getMission().getTimeExpired()) {
                        QTime = fut2.get().getValue();
                        Future<Boolean> fut3 = sendAgents(mission);
                        futureHashMap.get(mission).add(fut3);

                        if (fut3 != null && fut3.get() != null && fut3.get())
                            diary.addReport(fillReport(mission, fut1.get()));

                    } else {
                        Future<Boolean> fut4 = releaseAgents(mission);
                        futureHashMap.get(mission).add(fut4);
                    }
                }
            }
        });

        latch.countDown();
    }

    private Future<HashMap<String, Object>> askAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new AgentsAvailableEvent(getName(), mission.getSerialAgentsNumbers(), mission.getTimeExpired())
        );
    }

    private Future<Map.Entry<Boolean, Integer>> askGadget(MissionInfo mission) {
        Future<Map.Entry<Boolean, Integer>> future = getSimplePublisher().sendEvent(new GadgetAvailableEvent(getName(), mission.getGadget()));
        return future;
    }

    private Future<Boolean> sendAgents(MissionInfo mission) {
        return getSimplePublisher().sendEvent(
                new SendAgentsEvent(getName(), mission.getSerialAgentsNumbers(), mission.getDuration(), mission.getTimeExpired())
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
        report.setTimeCreated(QTime + mission.getDuration());
        report.setTimeIssued(mission.getTimeIssued());
        report.setAgentsNames((List<String>) fut.get("AgentsName"));
        report.setMoneypenny(Integer.parseInt((String) fut.get("MoneyPenny")));
        report.setQTime(this.QTime);

        return report;
    }
}
