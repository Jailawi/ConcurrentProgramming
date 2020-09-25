package lift;

public class LiftThread implements Runnable {
	private Monitor m;

	public LiftThread(Monitor m) {
		this.m = m;
	}

	@Override
	public void run() {

		while (true) {

			try {
				if (m.getPassengers() >= 1) {
					m.moveUp();
					m.moveDown();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
