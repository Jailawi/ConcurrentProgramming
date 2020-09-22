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

	private synchronized void reportPassengerEntered(int fromFloor) {
		waitEntry[fromFloor] -= 1;
		notifyAll();
	}

	private synchronized void reportPassengerExited(int fromFloor) {
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
		waitExit[toFloor] += 1;
		load++;
		while (currentFloor != toFloor) {
			wait();
		}
		pass.exitLift();
		load--;

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

	public synchronized boolean checkExiting(int currentFloor) {
		// System.out.println(waitExit[currentFloor]);
		if (waitExit[currentFloor] > 0) {
			System.out.println("passenger should exit here");
			this.currentFloor = currentFloor;
			reportPassengerExited(currentFloor);
			return true;
		}
		return false;
	}

}
