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

	public Time getCurrentTime() throws InterruptedException {
		mutex.acquire();
		Time t = this;
		mutex.release();
		return t;
	}

	public int getHour() throws InterruptedException {
		mutex.acquire();
		int a = h;
		mutex.release();
		return a;
	}

	public int getMin() throws InterruptedException {
		mutex.acquire();
		int a = m;
		mutex.release();
		return a;
	}

	public int getSec() throws InterruptedException {
		mutex.acquire();
		int a = s;
		mutex.release();
		return a;
	}

	public void setTime(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		this.h = h;
		this.m = m;
		this.s = s;
		mutex.release();
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
