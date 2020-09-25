package lift;

import java.util.HashSet;

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
				Passenger pass = liftView.createPassenger();
				int fromFloor = pass.getStartFloor();
				int toFloor = pass.getDestinationFloor();
				pass.begin();
				monitor.putPassengeInLift(fromFloor, toFloor);
				monitor.addWalkers(pass);
				pass.enterLift();
				monitor.setWalking(false);
				monitor.removeWalkers(pass);
				monitor.exitPassengerFromLift(fromFloor, toFloor);
				monitor.addWalkers(pass);
				pass.exitLift();
				monitor.setWalking(false);
				monitor.removeWalkers(pass);
				pass.end();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
