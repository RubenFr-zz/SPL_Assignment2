package bgu.spl.mics.application.passiveObjects;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;

    /**
     * Static inner class (Bill Push singleton method)
     * That way we are sure the class instance is only defined once !
     */
    private static class SquadHolder {
        private static Squad instance = new Squad();
    }

    /**
     * Retrieves the single instance of this class.
     * Can't be public - need to be checked
     */
    private static Squad getInstance() {
        return SquadHolder.instance;
    }

    /**
     * Initializes the squad. This method adds all the agents to the squad.
     * <p>
     *
     * @param agents Data structure containing all data necessary for initialization
     *               of the squad.
     */
    public void load(Agent[] agents) {
        for (Agent agent : agents) {
            this.agents.putIfAbsent(agent.getSerialNumber(), agent);// To ensure that we don't add agents with the same serial number
        }
    }

    /**
     * Releases agents.
     */
    public void releaseAgents(List<String> serials) {
        for (String serial : serials) {
            this.agents.get(serial).release();
        }
    }

    /**
     * simulates executing a mission by calling sleep.
     *
     * @param time milliseconds to sleep
     */
    public void sendAgents(List<String> serials, int time) {
        LinkedList<Agent> missionAgents = new LinkedList<>();
        for(String serial : serials){
            missionAgents.addLast(this.agents.get(serial));
        }
        // Now we build a list with the gents for that specific mission
        try {
            TimeUnit.MILLISECONDS.sleep(time * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Simulates mission in progress
        // Now we will release those agents after the mission was finished
        this.releaseAgents(serials);
    }

    /**
     * acquires an agent, i.e. holds the agent until the caller is done with it
     *
     * @param serials the serial numbers of the agents
     * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
     */
    public boolean getAgents(List<String> serials) throws InterruptedException {
        for (String serial : serials) {
            Agent agent = this.agents.get(serial);
            if (agent == null) return false;// Means that we didn't find an agent with that serial number
            else {
                synchronized (this) {// In order to prevent event that we take the same agent at the same time to several missions - NEED TO BE CHECKED!!!!!
                    while (!agent.isAvailable()) wait();// We will wait until the agent will be available to acquire
                    agent.acquire();
                }
            }
        }
        return true;
    }

    /**
     * gets the agents names
     *
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials) {
        LinkedList<String> agentsNames = new LinkedList<>();
        for(String serial : serials){
            if (this.agents.get(serial) != null)
                agentsNames.addLast(this.agents.get(serial).getName());
            else// Means that we don't have an agent with that serial number - ERROR
                System.out.println(new NullPointerException("serial isn't an agent").toString());
        }
        return agentsNames;
    }

}
