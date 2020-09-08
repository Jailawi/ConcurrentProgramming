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
		try {

			long t0 = System.currentTimeMillis();
			for (int sec = 0; sec <= 59; sec++) {
				long now = System.currentTimeMillis();
				out.displayTime(h, m, s += 1);
				Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
				if (s == 59) {
					System.out.print("here");
					s = 0;
					out.displayTime(h, m += 1, s);
					if (m == 59) {
						m = 0;
						s = 0;
						out.displayTime(h += 1, m, s);

					}
				}
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}

	}

}
