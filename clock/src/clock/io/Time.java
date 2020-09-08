package clock.io;

import java.util.concurrent.Semaphore;

public class Time {
	private int h, m, s;
	private Semaphore mutex = new Semaphore(1);

	public Time(int h, int m, int s) {
		this.h = h;
		this.s = s;
		this.m = m;
	}

	public Time getCurrentTime() {
		return this;
	}

	public int getHour() {
		return h;
	}

	public int getMin() {
		return m;
	}

	public int getSec() {
		return s;
	}

	/*
	 * public void getTimeFromUser(UserInput userInput) throws InterruptedException
	 * { mutex.acquire(); h= userInput.getHours(); m= userInput.getMinutes(); s=
	 * userInput.getSeconds(); mutex.release(); }
	 * 
	 */

	public void setTime(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		this.h = h;
		this.m = m;
		this.s = s;
		mutex.release();
	}

	public boolean equals(Time clockTime) {
		try {
			mutex.acquire();
			boolean b = h == clockTime.getHour() && m == clockTime.getMin() && s == clockTime.getSec();
			mutex.release();
			return b;
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	public void increaseTime() throws InterruptedException {
		mutex.acquire();
		s++;
		if (s > 59) {
			m++;
			s = 0;
			if (m > 59) {
				h++;
				m = 0;
			}
		}
		mutex.release();
	}

}
