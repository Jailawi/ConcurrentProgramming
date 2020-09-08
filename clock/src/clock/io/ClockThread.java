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
			
		
		while(true) {
			long now = System.currentTimeMillis();
			increaseTime();
			out.displayTime(h, m, s);
			Thread.sleep((t0 + ((s + 1) * 1000)) - now);
		}
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		
	}
	
	
	public void increaseTime() {
		s++;
		if(s==59) {
			m++;
			if(m==59) {
				h++;
			}
		}
		
	}
		
		
		
		/*
		try {

			long t0 = System.currentTimeMillis();
			int sec=0;
			while(sec<59){
				long now = System.currentTimeMillis();
				out.displayTime(h, m, s += 1);
				Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
				sec++;
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
		
		*/

	}


