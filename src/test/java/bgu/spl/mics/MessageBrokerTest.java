package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageBrokerTest {

    private MessageBroker messageBroker;
    @BeforeEach
    public void setUp(){
        this.messageBroker = MessageBrokerImpl.getInstance();
    }

    @Test
    void testSubscribeEvent() {
    }

    @Test
    void testSubscribeBroadcast() {
    }

    @Test
    void testComplete() {
    }

    @Test
    void testSendBroadcast() {
    }

    @Test
    void testSendEvent() {
    }

    @Test
    void testRegister() {
    }

    @Test
    void testUnregister() {
    }

    @Test
    void testAwaitMessage() {
    }
}
