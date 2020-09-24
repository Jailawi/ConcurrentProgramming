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
			try {
				Thread.sleep(3000);
				Passenger pass = liftView.createPassenger();
				int fromFloor = pass.getStartFloor();
				int toFloor = pass.getDestinationFloor();
				// monitor.countPassengers(fromFloor);
				pass.begin();
				monitor.putPassengeInLift(fromFloor, toFloor, pass);
				pass.enterLift();
				monitor.exitPassengerFromLift(fromFloor, toFloor, pass);
				pass.exitLift();
				pass.end();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
