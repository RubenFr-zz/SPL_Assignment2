package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;


/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	private Diary diary;
	private int currTick;

	public M(String name) {
		super(name);
		this.diary = Diary.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, callback -> {
			currTick = callback.getTick();
		});
		subscribeEvent(MissionReceivedEvent.class, callback -> {
			MissionInfo mission = callback.getMission();
			Future<Boolean> future1 = getSimplePublisher().sendEvent(new AgentsAvailableEvent(getName(), mission.getSerialAgentsNumbers()));

			try{
				assert future1 != null;
				if (future1.get()){
					Future<Boolean> future2 = getSimplePublisher().sendEvent(new GadgetAvailableEvent(getName(), mission.getGadget()));
					assert future2 != null;
					if (future2.get()){

					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
	}

}
