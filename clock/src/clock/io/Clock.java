package clock.io;

import java.util.concurrent.Semaphore;

public class Clock implements Runnable {
	private int h, m, s;
	private ClockOutput out;
	private ClockInput in;
	private Time time = new Time(h, m, s);
	private Semaphore mutex = new Semaphore(1);

	public Clock(ClockOutput out, ClockInput in) {
		this.out = out;
		this.in = in;
	}

	public void run() {
		while (true) {
			timeTicking();

		}
	}

	public void setTime(int h, int m, int s) throws InterruptedException {
		time.setTime(h, m, s);
	}

	public Time getTime() {
		return time;
	}

	/*
	 * 
	 * private void updateClockWithUserInput() throws InterruptedException {
	 * in.getSemaphore().acquire(); UserInput userInput = in.getUserInput();
	 * time.getTimeFromUser(userInput); }
	 * 
	 */

	private void timeTicking() {
		long t0 = System.currentTimeMillis();
		try {

			int sec = 0;
			while (true) {
				long now = System.currentTimeMillis();
				time.getCurrentTime().increaseTime();
				out.displayTime(time.getHour(), time.getMin(), time.getSec());
				Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
				sec++;
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}

	}

}
