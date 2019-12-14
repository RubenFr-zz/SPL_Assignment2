package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.Squad;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private Squad squad;

	public Moneypenny(String name) {
		super(name);
		// TODO Implement this
		this.squad = Squad.getInstance();
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
