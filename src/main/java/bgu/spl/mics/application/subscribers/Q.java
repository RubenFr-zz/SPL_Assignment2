package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.Inventory;


/**
 * Q is the only Subscriber\Publisher that has access to the {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private Inventory inventory;

	private Q() {
		super("Q");
		this.inventory = Inventory.getInstance();
	}

	/**
	 * Static inner class (Bill Push singleton method)
	 * That way we are sure the class instance is only defined once !
	 */
	private static class QHolder {
		private static Q instance = new Q();
	}

	/**
	 * @return The only instance of the class Q
	 */
	public static Q getInstance() { return QHolder.instance; }


	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
