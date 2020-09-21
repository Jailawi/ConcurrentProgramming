package lift;

public class PassengerThread extends Thread {
	private Monitor monitor;
	private LiftView liftView;

	public PassengerThread(Monitor monitor, LiftView liftView) {
		this.monitor = monitor;
		this.liftView = liftView;
	}

	@Override
	public void run() {
		while (true) {
			// wait(4500);
			try {
				Passenger pass = liftView.createPassenger();
				monitor.addPassengerInLift(pass);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
