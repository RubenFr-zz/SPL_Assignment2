package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a Unit Test for the {@link Squad} class.
 * @author Ruben Fratty
 */
public class SquadTest {

    private Squad squad;
    private Map<String, Agent> agents;

    /**
     * Set up for a test
     */
    @BeforeEach
    public void setUp(){
        this.squad = Squad.getInstance();
        this.agents = squad.getAgents();
    }

    /**
     * Test method for {@link Squad#getAgents(List<String>)}
     *
     */
    @Test
    public void testGetAgents(){
        List<Agent> agents = new LinkedList<>();
        Agent A = new Agent();

        List<String> serials = new LinkedList<>();
        serials.add("005");
        serials.add("006");
        serials.add("007");
        boolean test = false;
        try {
            test = squad.getAgents(serials);
        }catch (Exception e){
            fail("Unexpected exception: ", e);
        }
        assertFalse(test);
        agents.add(null);
    }
}
