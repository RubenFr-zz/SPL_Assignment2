package bgu.spl.mics;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;


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
    private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> hashMapSubscriber;// Subscriber with a BlockingQueue (that has a blocking method) with all of his messages
    private ConcurrentHashMap<Class<? extends Message>, LinkedList<Subscriber>> EventSubscribers;// For Agents, Gadgets and MissionReceived events types! with their subscriber
    private ConcurrentHashMap<Class<? extends Message>, LinkedList<Subscriber>> BroadcastSubscribers;// For Tick Broadcast events with their subscriber
    private ConcurrentHashMap<Event<?>, Future<?>> toBeSolved;

    private MessageBrokerImpl() {// For the singleton
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
        private static MessageBroker instance = new MessageBrokerImpl();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MessageBroker getInstance() {
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
            if (this.EventSubscribers.containsKey(type) && !this.EventSubscribers.get(type).contains(m)) {// If we have that kind of event and that subscriber didn't subscribed more than once
                this.EventSubscribers.get(type).addLast(m);// We now add m in the end of the "chain" of the subscribers of that kind of event
            } else {// Means its the first time someone subscribed that kind of event
                LinkedList<Subscriber> newSubersList = new LinkedList<>();
                newSubersList.addLast(m);// The new subscribers list (there is only one subscriber because it a new list)
                this.EventSubscribers.put(type, newSubersList);
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
            if (this.BroadcastSubscribers.containsKey(type) && !this.BroadcastSubscribers.get(type).contains(m)) {// If we have that kind of broadcast and that subscriber didn't subscribed more than once
                this.BroadcastSubscribers.get(type).addLast(m);// We now add m in the end of the "chain" of the subscribers of that kind of broadcast
            } else {// Means its the first time someone subscribed that kind of event
                LinkedList<Subscriber> newSubersList = new LinkedList<>();
                newSubersList.addLast(m);// The new subscribers list (there is only one subscriber because it a new list)
                this.BroadcastSubscribers.put(type, newSubersList);
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
        Future<T> future = (Future<T>)this.toBeSolved.get(e);// Casting
        if(future!=null)
            future.resolve(result);
        this.toBeSolved.remove(e);// Doesn't excised
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
        if(this.BroadcastSubscribers.containsKey(b.getClass()) && this.BroadcastSubscribers.get(b.getClass())!=null) {// Means that there is a subscriber with that type of broadcast
            LinkedList<Subscriber> subers = this.BroadcastSubscribers.get(b.getClass());// The list of the subscribers of that broadcast
            if(subers == null)// b doesn't broadcasting to any subscriber
                System.out.println("There are no subscribers to: " + b.getClass().getName());
            else {
                for (Subscriber sub : subers) {
                    try {// Try to put the broadcast in sub (one of the subers - need to send them that broadcast)
                        this.hashMapSubscriber.get(sub).put(b);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        Subscriber firstSub = this.EventSubscribers.get(e.getClass()).pollFirst();// We got the first subscriber in the "chain" of the subscribers (from his kind)
        if (firstSub != null && this.hashMapSubscriber.containsKey(firstSub)) {// If we have that subscriber in the hash of the subscribers
            Future<T> future = new Future<>();
            this.toBeSolved.put(e, future);// Set a future object for that event
            this.EventSubscribers.get(e.getClass()).addLast(firstSub);// Round-Robin: we move that subscriber to the end of the "chain"
            try {
                this.hashMapSubscriber.get(firstSub).put(e);// Tries to put the event e in the blocking queue of firstSub
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return future;// We return future we put in the event e
        }
        else
            return null;
    }

    /**
     * a Subscriber calls this method in order to register itself. This method
     * should create a queue for the Subscriber in the MessageBroker.
     *
     * @param m NOT NULL
     */
    @Override
    public void register(Subscriber m) {
        BlockingQueue<Message> block = new LinkedBlockingDeque<>();
        if(!this.hashMapSubscriber.containsValue(m))// Means that m isn't in the hash of the subscribers
            this.hashMapSubscriber.put(m, block);
        else
            throw new IllegalArgumentException("This subscriber is already exists!");
    }

    /**
     * A Subscriber calls this method in order to unregister itself. Should
     * remove the message queue allocated to the Subscriber and clean all the
     * references related to this MessageBroker.
     */
    @Override
    public void unregister(Subscriber m) {
        if (!this.hashMapSubscriber.contains(m))// m isn't exists
            throw new IllegalArgumentException("This subscriber isn't exists!");
        else {
            // We need to delete all the events ad broadcasts subscribed by that subscriber
            // In order to do so, we will check if is available (by synchronized(m))
            for (Class<? extends Message> key : this.EventSubscribers.keySet()) {// In order to bring every subscriber that reported an event
                LinkedList<Subscriber> checkedSubscriber = this.EventSubscribers.get(key);// We know it would be one subscriber at a time
                if (checkedSubscriber.contains(m)) {// if checkedSubscriber == m
                    synchronized (this.EventSubscribers.get(key)) {// Now no one can access m (thread safe)
                        this.EventSubscribers.remove(checkedSubscriber);// Remove him
                    }
                }
            }
            for (Class<? extends Message> key : this.BroadcastSubscribers.keySet()) {// In order to bring every subscriber that reported a broadcast
                LinkedList<Subscriber> checkedSubscriber = this.BroadcastSubscribers.get(key);// We know it would be one subscriber at a time
                if (checkedSubscriber.contains(m)) {// if checkedSubscriber == m
                    synchronized (this.BroadcastSubscribers.get(key)) {// Now no one can access m (thread safe)
                        this.BroadcastSubscribers.remove(checkedSubscriber);// Remove him
                    }
                }
            }
            this.hashMapSubscriber.remove(m);// Final remove - from the "chain" of the subscribers
        }
    }

    /**
     * A Subscriber calls this method in order to take a
     * message from its allocated queue. This method is blocking (waits until there is an
     * available message and returns it).
     */
    @Override
    public Message awaitMessage(Subscriber m) throws InterruptedException {
        if (!this.hashMapSubscriber.containsKey(m))// Means that m isn't in the hash of the subscribers
            throw new IllegalArgumentException("no such subscriber");
        /**
         * {@code take()}: Retrieves and removes the head of this queue,
         * waiting if necessary until an element becomes available.
         * @return the head of the queue
         */
        while(this.hashMapSubscriber.get(m)==null) wait();// Wait until there is a message in the queue
        return this.hashMapSubscriber.get(m).take();
    }
}
