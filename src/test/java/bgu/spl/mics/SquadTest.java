package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a Unit Test for the {@link Squad} class.
 * @author Ruben Fratty
 */
public class SquadTest {

    private Map<String, Agent> agents;
    private Squad squad;

    /**
     * Set up for a test
     */
    @BeforeEach
    public void setUp(){
        squad = Squad.getInstance();
        agents = createMap();
        Agent agent1 = new Agent();
        Agent agent2 = new Agent();
        Agent agent3 = new Agent();
        Agent agent4 = new Agent();
        agent1.setName("Amir yt");
        agent1.setSerialNumber("001");
        agent1.release();
        agent2.setName("Ruben");
        agent2.setSerialNumber("002");
        agent2.release();
        agent3.setName("hanan");
        agent3.setSerialNumber("000");
        agent3.release();
        agent4.setName("James Bond");
        agent4.setSerialNumber("007");
        agent4.release();
        agents.put("001",agent1);
        agents.put("002",agent2);
        agents.put("000",agent3);
        agents.put("007",agent4);
        Agent[] temp = {agent1,agent2,agent3,agent4};
        squad.load(temp);
    }

    /**
     * This creates the object under test (OUT)
     * @return a {@link Squad} instance
     */
    protected Map<String, Agent> createMap() {
        return new HashMap<>();
    }

    /**
     * Test method for {@link Squad#getAgents(List<String>)}
     *
     */
    @Test
    public void testGetAgents() throws InterruptedException {
        List<String> test1 = new LinkedList<>();
        test1.add("001");
        test1.add("002");
        test1.add("000");
        test1.add("005");
        assertFalse(squad.getAgents(test1));
        List<String> test2 = new LinkedList<>();
        test2.add("001");
        test2.add("002");
        test2.add("000");
        test2.add("007");
        assertTrue(squad.getAgents(test2));
    }

    @Test
    public void testGetInstance() {
        assertTrue(Squad.class == squad.getClass());
    }

    @Test
    public void testLoad() {
        assertTrue(squad != null);
    }

    @Test
    public void testReleaseAgents(){
        List<String> test1 = new LinkedList<>();
        test1.add("001");
        test1.add("002");
        squad.releaseAgents(test1);
        for(String serial : test1)
            assertTrue(agents.get(serial).isAvailable());
        assertFalse(!agents.get("001").isAvailable());
    }

    @Test
    public void testSendAgents(){
        List<String> test1 = new LinkedList<>();
        test1.add("001");
        test1.add("002");
        squad.sendAgents(test1,1);
        for(String serial : test1)
            assertTrue(agents.get(serial).isAvailable());
    }

    @Test
    public void testGetAgentsNames(){
        List<String> test1 = new LinkedList<>();
        test1.add("007");
        test1.add("002");
        List<String> temp = squad.getAgentsNames(test1);
        assertTrue(temp.contains("Ruben"));
        assertTrue(temp.contains("James Bond"));
    }
}
