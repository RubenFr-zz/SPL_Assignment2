package main.java.bgu.spl.mics.application.passiveObjects;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;
	private static Squad instance = null;

	/**
	 * Constructor
	 */
	private Squad(){
			this.agents = new ConcurrentHashMap<>();
	}

	/**
	 * Static inner class (Bill Push singleton method)
	 * That way we are sure the class instance is only defined once !
	 */
	private static class SquadHolder {
		private static Squad instance = new Squad();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return Squad.SquadHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] inventory) {
		// TODO Implement this
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		// TODO Implement this
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		// TODO Implement this
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		// TODO Implement this
		return false;
	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){

	    return null;
    }

}