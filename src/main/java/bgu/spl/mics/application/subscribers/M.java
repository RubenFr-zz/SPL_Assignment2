package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
    private HashMap<MissionInfo, Future<HashMap<String, Object>>> futureMap;
    private int currTick;
    private int QTime;

    public M(String name, CountDownLatch latch) {
        super(name, latch);
        this.diary = Diary.getInstance();
        this.futureMap = new HashMap<>();

    }

    /**
     * As said on the forum we chose to first request the GadgetAvailable and then the AgentsAvailable
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback -> {
            currTick = callback.getTick();
        });

        subscribeBroadcast(TerminationBroadcast.class, callback -> {
        	this.terminate();
        });

        subscribeEvent(MissionReceivedEvent.class, callback -> {

            diary.increment();
            MissionInfo mission = callback.getMission();
            int timeExpired = mission.getTimeExpired();
            Future<Boolean> fut1 = askGadget(mission);

            try {
                if (fut1.get() && currTick <= timeExpired) {
                    Future<HashMap<String, Object>> fut2 = askAgents(mission);

                    //TODO : Le currTick ne peut pas s'updater puisque tant que ce call back n'est pas finit rien
                    //TODO : d'autre n'est fait dans cette classe !!!
                    if ((boolean) fut2.get().get("Done") && currTick <= timeExpired) {
                        diary.addReport(fillReport(mission));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private Future<HashMap<String, Object>> askAgents(MissionInfo mission) {
        Future<HashMap<String, Object>> fut2 = getSimplePublisher().sendEvent(new AgentsAvailableEvent(getName(),
                mission.getSerialAgentsNumbers(), mission.getDuration(), mission.getTimeExpired()));
        futureMap.putIfAbsent(mission, fut2);
        return fut2;
    }

    private Future<Boolean> askGadget(MissionInfo mission) {
        Future<Boolean> fut1 = getSimplePublisher().sendEvent(new GadgetAvailableEvent(getName(), mission.getGadget()));
        QTime = currTick;
        return fut1;
    }

    @SuppressWarnings("unchecked")
    private Report fillReport(MissionInfo mission) {
        Report report = new Report();
        HashMap<String, Object> fut = null;
        try {
            fut = futureMap.get(mission).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert fut != null;
        report.setAgentsSerialNumbersNumber(mission.getSerialAgentsNumbers());
        report.setGadgetName(mission.getGadget());
        report.setM(Integer.parseInt(this.getName()));
        report.setMissionName(mission.getMissionName());
        report.setTimeCreated(currTick);
        report.setTimeIssued(mission.getTimeIssued()); //TODO CHANGE NOT TRUE
        report.setAgentsNames((List<String>) fut.get("AgentsName"));
		report.setMoneypenny(Integer.parseInt((String) fut.get("MonneyPenny")));
        report.setQTime(this.QTime);

        return report;
    }
}
