package bgu.spl.mics.application.passiveObjects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.application.passiveObjects.Agent;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

    private final HashMap<String, Agent> agents;
    private final List<String> serialNumbers;
    private boolean terminated;

    private final Object lock = new Object();

    private Squad() {
        this.agents = new HashMap<>();
        this.serialNumbers = new LinkedList<>();
        this.terminated = false;
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
            this.serialNumbers.add(agent.getSerialNumber());
        }
    }

    /**
     * Releases agents.
     */
    public void releaseAgents(List<String> serials) {
        synchronized (lock) {
            for (String serial : serials) {
                if (agents.containsKey(serial))
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
            if (agents.get(serial).isAvailable()) {
                releaseAgents(serials);
                return;
            }

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
                if (agents.containsKey(serial)) {
                    while (!terminated && !agents.get(serial).isAvailable()) {
                        lock.wait();
                    }
                    if(terminated) return false;
                    agents.get(serial).acquire();
                } else {
                    releaseAgents(serials);
                    return false;
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
        }
        return list;
    }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
        synchronized (lock){
            lock.notifyAll();
        }
    }
}
