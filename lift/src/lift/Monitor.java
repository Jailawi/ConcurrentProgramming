package lift;

import java.util.concurrent.locks.ReentrantLock;

import jdk.javadoc.doclet.Reporter;

public class Monitor {
	// int floor; // the floor the lift is currently on
	boolean moving = true; // true if the lift is moving, false if standing still with doors open
	int direction; // +1 if lift is going up, -1 if going down
	int[] waitEntry; // number of passengers waiting to enter the lift at the various floors
	int[] waitExit; // number of passengers (in lift) waiting to leave at the various floors
	int load; // number of passengers currently in the lift
	LiftView view;
	int currentFloor = 0;
	boolean stopLift = false;

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}

	// public void reportPassengerWait(int fromFloor) {
	// waitEntry[fromFloor] = waitEntry[fromFloor] += 1;
	// }

	// public void reportPassengerExit(int toFloor) {
	// waitExit[toFloor] = waitExit[toFloor] += 1;
	// }

	private synchronized void reportPassengerEntered(int fromFloor) {
		// System.out.println("is walking");
		waitEntry[fromFloor] -= 1;
		notifyAll();
	}

	private synchronized void reportPassengerExited(int fromFloor) {
		// System.out.println("entered");
		waitExit[fromFloor] = waitEntry[fromFloor] -= 1;
		notifyAll();
	}

	public synchronized void setPassengerTravel(int fromFloor, int toFloor, Passenger pass)
			throws InterruptedException {
		waitEntry[fromFloor] += 1;
		while (fromFloor != currentFloor || load == 4) {
			wait();
		}
		pass.enterLift();
		load++;

	}

	public synchronized void addPassengerInLift(Passenger pass) throws InterruptedException {
		int fromFloor = pass.getStartFloor();
		waitEntry[fromFloor] += 1;
		int toFloor = pass.getDestinationFloor();
		waitExit[toFloor] += 1;
		pass.begin();

		while (currentFloor != toFloor) {
			wait();
		}
		pass.enterLift();
		// while (currentFloor != toFloor) {
		// wait();
		// }
		// pass.exitLift();
	}

	public synchronized boolean checkEntering(int currentFloor) {
		// System.out.println(waitEntry[currentFloor]);
		if (waitEntry[currentFloor] > 0) {
			this.currentFloor = currentFloor;
			reportPassengerEntered(currentFloor);
			return true;
		}
		return false;
	}

	private void checkExiting(int currentFloor) {
		// System.out.println(waitExit[currentFloor]);
		if (waitExit[currentFloor] > 0) {
			System.out.println("entered");
			reportPassengerExited(currentFloor);
		}
	}

	/*
	 * public synchronized void handleDoorOpen(int level) throws
	 * InterruptedException { view.openDoors(level); while(waitEntry[level] !=0) {
	 * wait(); } }
	 * 
	 * 
	 */

	public void moveLift() throws InterruptedException {
		while (true) {

			for (int i = 0; i <= 5; i++) {
				view.moveLift(currentFloor, currentFloor + 1);
				currentFloor++;
				view.openDoors(currentFloor);

				checkEntering(currentFloor);
				// checkExiting(currentFloor);
				view.closeDoors();

			}

			for (int i = 5; i >= 0; i--) {
				view.moveLift(currentFloor, currentFloor - 1);
				currentFloor--;
				view.openDoors(currentFloor);
				if (stopLift) {
					wait(1000);
				}
				// checkExiting(currentFloor);
				checkEntering(currentFloor);
				view.closeDoors();
			}
		}
	}

	public synchronized void waitOutside() throws InterruptedException {
		wait(1000);
	}

}
