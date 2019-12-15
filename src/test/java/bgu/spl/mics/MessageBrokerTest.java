package bgu.spl.mics;

import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.subscribers.M;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageBrokerTest {

    private MessageBroker messageBroker;
    private AgentsAvailableEvent agentEvent;
    private GadgetAvailableEvent gadgetEvent;
    private MissionReceivedEvent receivedEvent;
    private TickBroadcast tickEvent;
    @BeforeEach
    public void setUp(){
    /*
        create new events from several types and check them
     */
    messageBroker = new MessageBrokerImpl();
    agentEvent = new AgentsAvailableEvent("111111");
    gadgetEvent = new GadgetAvailableEvent("22222");
    receivedEvent = new MissionReceivedEvent("333333");
    tickEvent = new TickBroadcast("444444", 3);
    }

    @Test
    public void testSubscribeEvent() {
        Subscriber M1 = new M("diasy");
        messageBroker.subscribeEvent(receivedEvent.getClass(),M1);

    }
    @Test
    public void testSubscribeBroadcast() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testComplete() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSendBroadcast() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSendEvent() {
        // TODO Auto-generated method stub
    }

    @Test
    public void testRegister() {
        // TODO Auto-generated method stub
    }

    @Test
    public void testUnregister() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testAwaitMessage() throws InterruptedException {
        // TODO Auto-generated method stub
    }

}
