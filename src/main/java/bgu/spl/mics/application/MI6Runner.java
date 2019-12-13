package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) throws InterruptedException {

        Agent agent = Agent.getInstance();
        agent.setName("James Bond");
        agent.setSerialNumber("007");
        System.out.println(agent);

        Agent tmp = agent;
        Thread t1 = new Thread(tmp::acquire);
        Thread t2 = new Thread(agent::release);
//        t1.start();
//        t2.join();
        tmp.acquire();
        t2.start();
        while (!agent.isAvailable()) System.out.println("waiting");
        System.out.println(tmp);

    }
}
