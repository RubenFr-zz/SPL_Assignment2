package bgu.spl.mics;

import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.subscribers.Q;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBrokerTest {

    private MessageBroker messageBroker;


    @BeforeEach
    public void setUp() {
        messageBroker = MessageBrokerImpl.getInstance();
    }

    @Test
    public void Test() {
        Inventory.getInstance().load(new String[]{"gun"});
        GadgetAvailableEvent event = new GadgetAvailableEvent("0", "gun");
        Subscriber m = Q.getInstance();
        messageBroker.register(m);
        m.initialize();
        messageBroker.subscribeEvent(event.getClass(), m);
        Future<Map.Entry<Boolean, Integer>> fut = messageBroker.sendEvent(event);
        assertFalse(fut.isDone());
        Thread t = new Thread(() -> {
            fut.get();
        });
        t.start();
        fut.resolve(new AbstractMap.SimpleEntry<>(true, 0));
        assertTrue(fut.get().getKey());
    }
}