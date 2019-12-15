package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
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

	private MissionInfo missionReceivedEvent;
	private Diary diary;

	public M(String name) {
		super(name);
		this.diary = Diary.getInstance();
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
