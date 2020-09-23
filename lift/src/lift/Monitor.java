package lift;

import java.util.Arrays;

public class Monitor {
	boolean moving = true; // true if the lift is moving, false if standing still with doors open
	int direction; // +1 if lift is going up, -1 if going down
	int[] waitEntry; // number of passengers waiting to enter the lift at the various floors
	int[] waitExit; // number of passengers (in lift) waiting to leave at the various floors
	int load; // number of passengers currently in the lift
	LiftView view;
	int currentFloor = 0;
	boolean stopLift = false;
	private boolean stop, stop1, stop2 = false;
	private boolean doorOpen;
	int passengers;
	int numberOfConcurrentlyWalking;

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}

	public synchronized int getPassengers() {
		return passengers;
	}

	private synchronized void reportPassengerEnteredLift(int fromFloor) {
		waitEntry[fromFloor] -= 1;

		// System.out.println("Wants to enter " + Arrays.toString(waitEntry));
		notifyAll();
	}

	private synchronized void reportPassengerExitedLift(int fromFloor) {
		waitExit[fromFloor] = 0;
		// System.out.println("Wants to exit " + Arrays.toString(waitExit));

		notifyAll();
	}

	public synchronized void putPassengeInLift(int fromFloor, int toFloor, Passenger pass) throws InterruptedException {
		waitEntry[fromFloor] += 1;
		passengers++;
		while (fromFloor != currentFloor || load == 4 || !doorOpen) {
			wait();
		}
		waitExit[toFloor] += 1;

		load++;
		view.showDebugInfo(waitEntry, waitExit);
	}

	public synchronized void exitPassengerFromLift(int fromFloor, int toFloor, Passenger pass)
			throws InterruptedException {
		while (currentFloor != toFloor) {
			wait();
		}
		passengers--;
		load--;

	}

	public synchronized boolean checkEntering(int currentFloor) {
		// view.showDebugInfo(waitEntry, waitExit);
		if (waitEntry[currentFloor] > 0) {
			this.currentFloor = currentFloor;
			for (var i = 1; i <= waitEntry[currentFloor]; i++) {
				reportPassengerEnteredLift(currentFloor);
			}

			return true;
		}
		return false;
	}

	public synchronized boolean checkExiting(int currentFloor) {

		// System.out.println(waitExit[currentFloor]);
		if (waitExit[currentFloor] > 0) {
			// System.out.println("passenger should exit here");
			this.currentFloor = currentFloor;
			System.out.println("Wants to exit: " + waitExit[currentFloor]);
			// for (var i = 1; i <= waitExit[currentFloor]; i++) {
			reportPassengerExitedLift(currentFloor);
			// }
			return true;
		}
		return false;
	}

	private synchronized void waitOutside(int x) throws InterruptedException {
		wait(1250 * x);
	}

	private synchronized void concurrentWalkers() {
		// numberOfConcurrentlyWalking = waitExit[currentFloor] + 4 - (load -
		// waitExit[currentFloor]);
		int emptySpace = 4 - load;
		int willEnter = 0;
		if (waitEntry[currentFloor] > emptySpace) {
			willEnter = emptySpace;
		} else if (waitEntry[currentFloor] < emptySpace) {
			willEnter = waitEntry[currentFloor];
		} else {
			willEnter = emptySpace;
		}
		numberOfConcurrentlyWalking = waitExit[currentFloor] + willEnter;
		System.out.println("CurrentWalkers " + numberOfConcurrentlyWalking);
	}

	public void checkCondition() {
		stop1 = checkExiting(currentFloor);
		stop2 = checkEntering(currentFloor);

		concurrentWalkers();

		if (stop1 && stop2) {
			try {
				if (doorOpen) {
					waitOutside((numberOfConcurrentlyWalking) + 1);

					stop1 = false;
					stop2 = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (stop2) {
			try {
				if (doorOpen) {
					waitOutside(numberOfConcurrentlyWalking + 1);
					stop1 = false;
					stop2 = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (stop1) {
			try {
				if (doorOpen) {
					waitOutside(numberOfConcurrentlyWalking + 1);
					stop2 = false;
					stop1 = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void moveUp() {
		for (int i = 0; i <= 5; i++) {
			view.moveLift(currentFloor, currentFloor + 1);
			currentFloor++;
			if (waitEntry[currentFloor] >= 1 && load <= 3 || waitExit[currentFloor] >= 1) {

				view.openDoors(currentFloor);
				doorOpen = true;
			}

			checkCondition();
			if (doorOpen) {
				doorOpen = false;
				view.closeDoors();
			}
		}

	}

	public void moveDown() {
		for (int i = 5; i >= 0; i--) {
			view.moveLift(currentFloor, currentFloor - 1);
			currentFloor--;

			if (waitEntry[currentFloor] >= 1 && load <= 3 || waitExit[currentFloor] >= 1) {
				view.openDoors(currentFloor);
				doorOpen = true;
			}

			checkCondition();
			if (doorOpen) {
				doorOpen = false;
				view.closeDoors();
			}
		}

	}

}
