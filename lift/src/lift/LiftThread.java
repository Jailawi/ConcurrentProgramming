package lift;

public class LiftThread extends Thread {
	private LiftView lf;
	private Monitor m;

	public LiftThread(LiftView lf, Monitor m) {
		this.lf = lf;
		this.m = m;
	}

	@Override
	public void run() {
		while (true) {

			try {
				m.moveLift();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
	}

}
