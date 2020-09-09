package clock.io;

public class Clock implements Runnable {
	private int h, m, s;
	private ClockOutput out;
	private Time time = new Time(h, m, s);

	public Clock(ClockOutput out) {
		this.out = out;
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
