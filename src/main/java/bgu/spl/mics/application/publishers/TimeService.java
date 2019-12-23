package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;


/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private int projectTime;
	private int speed;
	private Timer timer;
	private AtomicInteger current;
	private CountDownLatch latch;
	private TimerTask timerTask;

	private TimeService(int projectTime, int speed) {// projectTime is the number of ticks before termination
		super("Time Service");
		this.projectTime = projectTime;
		this.speed = speed;
		this.current = new AtomicInteger(0);
//		this.latch = latch;


	}

	// Need to check if we wan here singleton
//	private static class TimeServiceHolder{
//		private static TimeService instance = new TimeService();
//	}
//
//	public TimeService getInstance(){
//		return TimeServiceHolder.instance;
//	}

	@Override
	protected void initialize() {
//		try{
//			/**
//			 * {@code await()}: Cause the current thread to wait until the latch has counted
//			 * down to zero
//			 * @catch InterruptedException
//			 */
//			latch.await();
//		}catch (InterruptedException e){
//			e.printStackTrace();
//		}
		this.timer = new Timer();
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				if (current.get() == projectTime){
					getSimplePublisher().sendBroadcast(new TerminationBroadcast());// Sends broadcast that terminates the program
					timer.cancel();
					timerTask.cancel();
				}else{
					getSimplePublisher().sendBroadcast(new TickBroadcast(getName(), current.incrementAndGet()));
				}
			}
		};
		timer.schedule(timerTask, this.speed);// Schedules timerTask for execution after speed.
	}

	@Override
	public void run() {
		initialize();// Start the TimeService
	}

}