package clock.io;

import java.util.concurrent.Semaphore;

public class Clock implements Runnable {
	private int h, m, s;
	private ClockOutput out;
	private Time time = new Time(h, m, s);
	private Semaphore sem = new Semaphore(1); 

	public Clock(ClockOutput out) {
		this.out = out;
	}

	public void run() {
		while (true) {
		
				timeTicking();
				
				}
	}

	public void setTime(int h, int m, int s) throws InterruptedException {
		sem.acquire();
		time.setTime(h, m, s);
		sem.release();
	}

	public Time getTime() throws InterruptedException {
		sem.acquire();
		var a = time;
		sem.release();
		return a;

	}

	private void timeTicking() {

		try {
			long t0 = System.currentTimeMillis();
			int sec = 0;
			while (true) {
				long now = System.currentTimeMillis();
				timeTick();
				Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
				sec++;
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}

	}
	
	
	public void timeTick() throws InterruptedException {
		sem.acquire();
		time.getCurrentTime().increaseTime();
		out.displayTime(time.getHour(), time.getMin(), time.getSec());
		sem.release();
	}

}
