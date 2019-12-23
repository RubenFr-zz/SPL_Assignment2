package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Publisher {

	private List<MissionInfo> missions;

	public Intelligence(String name) {
		super(name);
		this.missions = new LinkedList<>();
	}

	public void loadMission(MissionInfo[] missions){
		this.missions.addAll(Arrays.asList(missions));
	}

	@Override
	protected void initialize() {

	}

	@Override
	public void run() {
		initialize();
	}

}
