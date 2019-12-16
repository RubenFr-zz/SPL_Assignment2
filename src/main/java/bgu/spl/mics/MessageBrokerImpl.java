package bgu.spl.mics;

import bgu.spl.mics.Future;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

    /**
     * @param hashMapSubscriber Subscribers with their message queue
     * @param EventSubscriber holds the events with its subscribed subscribers
     * @param BroadcastSubscriber holds the broadcasts with its subscribed subscribers
     * @param toBeSolved will hold the events with its unsolved futures (futures to be solved)
     */
    private ConcurrentMap<Subscriber, BlockingQueue<Message>> hashMapSubscriber;
    private ConcurrentMap<Class<? extends Message>, LinkedList<Subscriber>> EventSubscribers;
    private ConcurrentMap<Class<? extends Message>, LinkedList<Subscriber>> BroadcastSubscribers;
    private ConcurrentMap<Event<?>, Future<?>> toBeSolved;

    public MessageBrokerImpl() {
        this.hashMapSubscriber = new ConcurrentHashMap<>();
        this.EventSubscribers = new ConcurrentHashMap<>();
        this.BroadcastSubscribers = new ConcurrentHashMap<>();
        this.toBeSolved = new ConcurrentHashMap<>();
    }


    /**
     * Static inner class (Bill Push singleton method)
     * That way we are sure the class instance is only defined once !
     */
    private static class MessageBrokerHolder {
        private static MessageBrokerImpl instance = new MessageBrokerImpl();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MessageBrokerImpl getInstance() {
        return MessageBrokerHolder.instance;
    }

    /**
     * A Subscriber calls this method in order to subscribe itself for
     * some {@code type} of {@link Event} (the specific class type of the event is passed as a
     * parameter).
     *
     * @param type NOT NULL
     * @param m    NOT NULL
     */
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
            if (EventSubscribers.containsKey(type) && !EventSubscribers.get(type).contains(m)) {
                EventSubscribers.get(type).addLast(m);
            } else {
                LinkedList<Subscriber> subType = new LinkedList<>();
                subType.addLast(m);
                EventSubscribers.put(type, subType);
            }
    }

    /**
     * A Subscriber calls this method in order to subscribe itself for
     * some type of broadcast message (The specific class type of the broadcast is
     * passed as a parameter).
     *
     * @param type NOT NULL
     * @param m    NOT NULL
     */
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {

            if (BroadcastSubscribers.containsKey(type) && !BroadcastSubscribers.get(type).contains(m)) {
                BroadcastSubscribers.get(type).addLast(m);
            } else {
                LinkedList<Subscriber> subType = new LinkedList<>();
                subType.addLast(m);
                BroadcastSubscribers.put(type, subType);
            }

    }

    /**
     * A Subscriber calls this method in order to notify
     * the MessageBroker that the event was handled, and providing the result of
     * handling the request. The Future object associated with event e should be
     * resolved to the result given as a parameter.
     *
     * @param e      NOT NULL
     * @param result NOT NULL
     */
    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = (Future<T>) toBeSolved.get(e);
        if (future != null)
            future.resolve(result);
        toBeSolved.remove(e);

    }

    /**
     * A Publisher calls this method in order to add a broadcast
     * message to the queues of all Subscribers which subscribed to receive this
     * specific message type.
     *
     * @param b NOT NULL
     */
    @Override
    public void sendBroadcast(Broadcast b) {
        if (!BroadcastSubscribers.containsKey(b.getClass()) || BroadcastSubscribers.get(b.getClass()) == null)
            return;
        LinkedList<Subscriber> subscribers = BroadcastSubscribers.get(b.getClass());
        synchronized (BroadcastSubscribers.get(b.getClass())) {
            for (Subscriber sub : subscribers) {
                try {
                    hashMapSubscriber.get(sub).put(b);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * A Publisher calls this method in order to add the
     * event e to the message queue of one of the Subscribers which subscribed to
     * receive events of type e.getClass().
     *
     * @param e NOT NULL
     * @return Future object to be solved
     */
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

            /**
             * Round-robin : take the first subscriber {@link LinkedList#pollFirst()}
             * and put it last {@link LinkedList#addLast(@code firstSub)}
             */
            Subscriber firstSub = EventSubscribers.get(e.getClass()).pollFirst();
            if (firstSub != null && hashMapSubscriber.containsKey(firstSub)) {
                Future<T> future = new Future<>();
                toBeSolved.put(e, future);
                EventSubscribers.get(e.getClass()).addLast(firstSub);

                try {
                    hashMapSubscriber.get(firstSub).put(e);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return future;
            }
            else return null;

    }

    /**
     * a Subscriber calls this method in order to register itself. This method
     * should create a queue for the Subscriber in the MessageBroker.
     *
     * @param m NOT NULL
     */
    @Override
    public void register(Subscriber m) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        hashMapSubscriber.putIfAbsent(m, queue);

    }

    /**
     * A Subscriber calls this method in order to unregister itself. Should
     * remove the message queue allocated to the Subscriber and clean all the
     * references related to this MessageBroker.
     */
    @Override
    public void unregister(Subscriber m) {
        //TODO check if need to delete it at other places !
        //TODO check if need to {@code synchronized(m)}
        for (Class<? extends Message> key : EventSubscribers.keySet()) {
            LinkedList<Subscriber> list = EventSubscribers.get(key);
//            synchronized (m) {
            if (list.contains(m)) {
                synchronized (EventSubscribers.get(key)) {
                    list.remove(m);
                }
            }
//            }
        }
        for (Class<? extends Message> key : BroadcastSubscribers.keySet()) {
            LinkedList<Subscriber> list = BroadcastSubscribers.get(key);
//            synchronized (m) {
            if (list.contains(m)) {
                synchronized (BroadcastSubscribers.get(key)) {
                    list.remove(m);
                }
            }
//            }
        }
        hashMapSubscriber.remove(m);
    }

    /**
     * A Subscriber calls this method in order to take a
     * message from its allocated queue. This method is blocking (waits until there is an
     * available message and returns it).
     */
    @Override
    public Message awaitMessage(Subscriber m) throws InterruptedException {
        if (!hashMapSubscriber.containsKey(m))
            throw new IllegalArgumentException("no such subscriber");
        /**
         * {@code take()}: Retrieves and removes the head of this queue,
         * waiting if necessary until an element becomes available.
         * @return the head of the queue
         */
        return hashMapSubscriber.get(m).take();
    }
}
