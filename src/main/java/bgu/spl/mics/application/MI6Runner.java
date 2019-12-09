package main.java.bgu.spl.mics.application;

import main.java.bgu.spl.mics.application.passiveObjects.Agent;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {

        Agent agent = Agent.getInstance();
        agent.setName("James Bond");
        agent.setSerialNumber("007");
        System.out.println(agent);
    }
}