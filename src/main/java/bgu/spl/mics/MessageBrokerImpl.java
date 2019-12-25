package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminationBroadcast;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

    /**
     * @param SubscribersQueue Subscribers with their message queue
     * @param EventSubscriber holds the events with its subscribed subscribers
     * @param BroadcastSubscriber holds the broadcasts with its subscribed subscribers
     * @param toBeSolved holds the events with its unsolved futures (futures to be solved)
     */
    private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> SubscribersQueue;
    private ConcurrentHashMap<Class<? extends Message>, LinkedList<Subscriber>> EventSubscribers;
    private ConcurrentHashMap<Class<? extends Message>, LinkedList<Subscriber>> BroadcastSubscribers;
    private ConcurrentHashMap<Event<?>, Future<?>> toBeSolved;

    private final Object lock1;
    private final Object lock2;

    public MessageBrokerImpl() {
        this.SubscribersQueue = new ConcurrentHashMap<>();
        this.EventSubscribers = new ConcurrentHashMap<>();
        this.BroadcastSubscribers = new ConcurrentHashMap<>();
        this.toBeSolved = new ConcurrentHashMap<>();
        lock1 = new Object();
        lock2 = new Object();
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
        synchronized (lock1) {
            if (EventSubscribers.containsKey(type) && !EventSubscribers.get(type).contains(m)) {
                EventSubscribers.get(type).addLast(m);
            } else {
                LinkedList<Subscriber> subType = new LinkedList<>();
                subType.addLast(m);
                EventSubscribers.put(type, subType);
            }
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
        synchronized (lock1) {
            if (!(BroadcastSubscribers.containsKey(type) && !BroadcastSubscribers.get(type).contains(m))) {
                LinkedList<Subscriber> subType = new LinkedList<>();
                BroadcastSubscribers.put(type, subType);
            }
            synchronized (BroadcastSubscribers.get(type)) {
                BroadcastSubscribers.get(type).addLast(m);
            }
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
    @SuppressWarnings("unchecked")
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

        if (subscribers == null) {
            System.out.println("NO SUBSCRIBERS FOR:" + b.getClass().getName());
            return;
        }
        try {
            synchronized (BroadcastSubscribers.get(b.getClass())) {
                for (int i = 0; i < subscribers.size(); i++) {
                    SubscribersQueue.get(subscribers.get(i)).put(b);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("UNEXPECTED ERROR: Mission subscriber queue");
        }
    }

    /**
     * A Publisher calls this method in order to add the
     * event e to the message queue of one of the Subscribers which subscribed to
     * receive events of type e.getClass().
     *
     * @param e NOT NULL
     * @return Future object to be solved
     * <p>
     * Round-robin : take the first subscriber {@link LinkedList#pollFirst()}
     * and put it last
     */
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        synchronized (lock1) {
            Subscriber nextSubRoundRobin = getNextSub(e);
            if (nextSubRoundRobin != null && SubscribersQueue.containsKey(nextSubRoundRobin)) {
                Future<T> future = new Future<>();
                toBeSolved.put(e, future);

                try {
                    SubscribersQueue.get(nextSubRoundRobin).put(e);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return future;
            } else return null;
        }
    }

    private <T> Subscriber getNextSub(Event<T> e) {
        Subscriber nextSubRoundRobin;
        synchronized (EventSubscribers.get(e.getClass())) {
            nextSubRoundRobin = EventSubscribers.get(e.getClass()).pollFirst();
            EventSubscribers.get(e.getClass()).addLast(nextSubRoundRobin);
        }
        return nextSubRoundRobin;
    }

    /**
     * a Subscriber calls this method in order to register itself. This method
     * should create a queue for the Subscriber in the MessageBroker.
     *
     * @param m NOT NULL
     */
    @Override
    public void register(Subscriber m) {
        SubscribersQueue.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    /**
     * A Subscriber calls this method in order to unregister itself. Should
     * remove the message queue allocated to the Subscriber and clean all the
     * references related to this MessageBroker.
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void unregister(Subscriber m) {
        synchronized (lock2) {
            /* Remove the register from all its registrations */
            if (m != null) {
                delete(m, EventSubscribers);
                delete(m, BroadcastSubscribers);
            }

            /* If someone unregister it means the time to finish the program has come
             * Then we resolve to null every non resolved futures */
            for (Event events : toBeSolved.keySet()) {
                complete(events, null);
            }

            if (SubscribersQueue.get(m) != null) {
                for (Message message : SubscribersQueue.get(m)) {
                    if (message instanceof Event)
                        complete((Event) message, null);
                }
                SubscribersQueue.remove(m);
            }

            System.out.println(m.getClass().getName() + m.getName() + " UNREGISTERED");
        }
    }

    /**
     * Safely unregister the subscriber for every Event/Broadcast it registered
     *
     * @param m          - subscriber to unregister
     * @param SubHashMap - map from which we want to unregister the subscriber {@param m}
     */
    private void delete(Subscriber m, ConcurrentHashMap<Class<? extends Message>, LinkedList<Subscriber>> SubHashMap) {
        for (Class<? extends Message> mess : SubHashMap.keySet()) {
            LinkedList<Subscriber> list = SubHashMap.get(mess);
            if (!list.isEmpty())
                synchronized (list) {
                    list.remove(m);
                }
            else SubHashMap.remove(mess);
        }
    }

    /**
     * A Subscriber calls this method in order to take a
     * message from its allocated queue. This method is blocking (waits until there is an
     * available message and returns it).
     */
    @Override
    public Message awaitMessage(Subscriber m) throws InterruptedException {
        if (!SubscribersQueue.containsKey(m))
            throw new IllegalArgumentException("no such subscriber");
        /**
         * {@code take()}: Retrieves and removes the head of this queue,
         * waiting if necessary until an element becomes available.
         * @return the head of the queue
         */
        return SubscribersQueue.get(m).poll(2, TimeUnit.SECONDS);
    }
}
