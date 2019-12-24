package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

    private int projectTime;
    private int speed;
    private Timer timer;
    private AtomicInteger currTick;
    private CountDownLatch latch;
    private TimerTask timerTask;
    private long time;


    public TimeService(int projectTime, int speed, CountDownLatch latch) {
        super("Time Service");
        this.projectTime = projectTime;
        this.speed = speed;
        this.currTick = new AtomicInteger(0);
        this.latch = latch;


    }

    @Override
    protected void initialize() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis() - time);
                if (currTick.get() == projectTime) {
                    getSimplePublisher().sendBroadcast(new TerminationBroadcast());
                    timer.cancel();
                    timerTask.cancel();
                } else {
                    getSimplePublisher().sendBroadcast(new TickBroadcast(currTick.incrementAndGet()));
                }
            }
        };
        time = System.currentTimeMillis();
        timer.schedule(timerTask, 0, speed);
    }

    @Override
    public void run() {
        initialize();
    }

}