package test;

import main.application.passiveObjects.Agent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SquadTest {

    private Map<String, Agent> agents;

    @BeforeEach
    public void setUp(){
        this.agents = new ConcurrentHashMap<>();
    }

    /**
     * Test of the {@link main.application.passiveObjects.Squad#load(Agent[])} method
     */
    @Test
    public void testLoad(Agent[] inventory){

    }
}
