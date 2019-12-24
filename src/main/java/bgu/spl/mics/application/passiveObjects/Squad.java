package bgu.spl.mics.application.passiveObjects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

    private final HashMap<String, Agent> agents;
    private final Object lock = new Object();

    private Squad() {
        this.agents = new HashMap<>();
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
            this.agents.putIfAbsent(agent.getSerialNumber(), agent);
        }
    }

    /**
     * Releases agents.
     */
    public void releaseAgents(List<String> serials) {
        synchronized (lock) {
            for (String serial : serials) {
                agents.get(serial).release();
            }
            lock.notifyAll();
        }
    }

    /**
     * simulates executing a mission by calling sleep.
     *
     * @param time milliseconds to sleep
     */
    public void sendAgents(List<String> serials, int time) {

        for (String serial : serials)
            if (agents.get(serial).isAvailable())
                throw new AgentNotAcquiredException();

        try {
            TimeUnit.MILLISECONDS.sleep(time * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        releaseAgents(serials);
    }

    /**
     * acquires an agent, i.e. holds the agent until the caller is done with it
     *
     * @param serials the serial numbers of the agents
     * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
     */
    public boolean getAgents(List<String> serials) throws InterruptedException {
        synchronized (lock) {
            for (String serial : serials) {
                Agent agent = agents.get(serial);
                if (agent == null) return false;
                else {
                    while (!agent.isAvailable()) {
                        lock.wait();
                    }
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
        List<String> list = new LinkedList<>();
        for (String serial : serials) {
            if (agents.get(serial) != null)
                list.add(agents.get(serial).getName());
            else System.out.println(new NullPointerException(serial + " isn't an agent").toString());
        }
        return list;
    }

    public HashMap<String, Agent> getAgents() {
        return agents;
    }
}
