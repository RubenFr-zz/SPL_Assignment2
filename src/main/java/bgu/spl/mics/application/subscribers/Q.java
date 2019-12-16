package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;


/**
 * Q is the only Subscriber\Publisher that has access to the {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private Inventory inventory;
	private int currTick;



	private Q(String name) {
		super(name);
		this.inventory = Inventory.getInstance();
	}

	/**
	 * Static inner class (Bill Push singleton method)
	 * That way we are sure the class instance is only defined once !
	 */
	private static class QHolder {
		private static Q instance = new Q("Q");
	}

	/**
	 * @return The only instance of the class Q
	 */
	public static Q getInstance() { return QHolder.instance; }


	/**
	 * Subscribe to {@link AgentsAvailableEvent} and {@link TickBroadcast}
	 */
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, callback -> {
			currTick = callback.getTick();
		});
		subscribeEvent(GadgetAvailableEvent.class, callback -> {
			boolean found  = inventory.getItem(callback.getGadget());
			complete(callback, found);
		});
	}

}
