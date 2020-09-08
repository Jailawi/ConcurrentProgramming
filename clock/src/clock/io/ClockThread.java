package clock.io;
//kan förmodligen flytta in ClockInput i denna klass för att göra det enklare

public class ClockThread extends Thread {
	private int h, m, s;
	private ClockOutput out;

	public ClockThread(int h, int m, int s, ClockOutput cp) {
		this.h = h;
		this.m = m;
		this.s = s;
		this.out = cp;
	}

	public void run() {

		timeTicking();

	}

	public void timeTicking() {
		long t0 = System.currentTimeMillis();
		try {

			int sec = 0;
			while (true) {
				long now = System.currentTimeMillis();
				increaseTime();
				out.displayTime(h, m, s);
				Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
				sec++;
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}

	}

	public void increaseTime() {
		s++;
		if (s > 59) {
			m++;
			s = 0;
			if (m > 59) {
				h++;
				m = 0;
			}
		}

	}
}
