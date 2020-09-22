package lift;

public class PassengerThread implements Runnable {
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
				int fromFloor = pass.getStartFloor();
				int toFloor = pass.getDestinationFloor();
				pass.begin();
				monitor.setPassengerTravel(fromFloor, toFloor, pass);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
