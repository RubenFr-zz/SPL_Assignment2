package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.*;
import java.util.concurrent.CountDownLatch;


/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {

	private HashMap<Integer, LinkedList<MissionInfo>> missions;
	private HashMap<MissionReceivedEvent,  Future<?>> futures;
	int currTick;
	CountDownLatch latch;

	public Intelligence(String name, CountDownLatch startSignal) {
		super(name);
		missions = new HashMap<>();
		futures = new HashMap<>();
		this.latch = startSignal;
	}

	public void loadMission(MissionInfo[] missions){
		for ( MissionInfo mission : missions){
			int time = mission.getTimeIssued();
			if (! this.missions.containsKey(time))
			{
				LinkedList<MissionInfo> list = new LinkedList<>();
				list.addLast(mission);
				this.missions.put(time, list);
			}
			else { this.missions.get(time).addLast(mission); }
		}
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, callback -> {
			currTick = callback.getTick();

			if ( missions.containsKey(currTick)){
				for ( MissionInfo mission : missions.get(currTick)) {
					MissionReceivedEvent event = new MissionReceivedEvent(getName(), mission);
					Future<Boolean> future = this.getSimplePublisher().sendEvent(event);
					futures.putIfAbsent(event, future);
				}
			}
		});

		subscribeBroadcast(TerminationBroadcast.class, callback -> {
			this.terminate();
		});

		latch.countDown();
	}


	@Override
	public String toString() {
		return "Publisher " + getName() + ": \nMissions: " + missions.toString() + "\n";
	}
}
