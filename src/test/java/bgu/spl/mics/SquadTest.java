package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is a Unit Test for the {@link Squad} class.
 * @author Ruben Fratty
 */
public class SquadTest {

    private Map<String, Agent> agents;

    /**
     * Set up for a test
     */
    @BeforeEach
    public void setUp(){
        agents = createMap();
    }

    /**
     * This creates the object under test (OUT)
     * @return a {@link Squad} instance
     */
    protected Map<String, Agent> createMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Test method for {@link Squad#getAgents(List<String>)}
     *
     */
    @Test
    public void testGetAgents(){
        assertTrue(true);
    }
}



//    /**
//     * Retrieves the single instance of this class.
//     */
//    public static Squad getInstance() {
//        //TODO: Implement this
//        return null;
//    }
//
//    /**
//     * Initializes the squad. This method adds all the agents to the squad.
//     * <p>
//     * @param inventory 	Data structure containing all data necessary for initialization
//     * 						of the squad.
//     */
//    public void load (Agent[] inventory) {
//        // TODO Implement this
//    }
//
//    /**
//     * Releases agents.
//     */
//    public void releaseAgents(List<String> serials){
//        // TODO Implement this
//    }
//
//    /**
//     * simulates executing a mission by calling sleep.
//     * @param time   milliseconds to sleep
//     */
//    public void sendAgents(List<String> serials, int time){
//        // TODO Implement this
//    }
//
//    /**
//     * acquires an agent, i.e. holds the agent until the caller is done with it
//     * @param serials   the serial numbers of the agents
//     * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
//     */
//    public boolean getAgents(List<String> serials){
//        // TODO Implement this
//        return false;
//    }
//
//    /**
//     * gets the agents names
//     * @param serials the serial numbers of the agents
//     * @return a list of the names of the agents with the specified serials.
//     */
//    public List<String> getAgentsNames(List<String> serials){
//        // TODO Implement this
//        return null;
//    }
//
//}
