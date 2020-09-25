package lift;

import java.util.HashSet;

public class Monitor {

	private int[] waitEntry; // number of passengers waiting to enter the lift at the various floors
	private int[] waitExit; // number of passengers (in lift) waiting to leave at the various floors
	private int load; // number of passengers currently in the lift
	private LiftView view;
	private int currentFloor = 0;
	private boolean stop1, stop2 = false;
	private boolean doorOpen;
	private int passengers;
	private boolean walking;
	private HashSet<Passenger> walkers = new HashSet<>();

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}

	public synchronized int getPassengers() {
		return passengers;
	}

	public synchronized void setWalking(boolean a) {
		walking = a;
		notifyAll();
	}

	public synchronized void addWalkers(Passenger a) {
		walkers.add(a);
	}

	public synchronized void removeWalkers(Passenger a) {
		walkers.remove(a);
	}

	private synchronized void reportPassengerEnteredLift(int fromFloor) {
		waitEntry[fromFloor] -= countWillEnter(fromFloor);
		notifyAll();
	}

	private synchronized void reportPassengerExitedLift(int toFloor) {
		load -= waitExit[toFloor];
		waitExit[toFloor] = 0;
		notifyAll();
	}

	public synchronized void putPassengeInLift(int fromFloor, int toFloor) throws InterruptedException {
		waitEntry[fromFloor] += 1;
		passengers++;
		view.showDebugInfo(waitEntry, waitExit);
		while (fromFloor != currentFloor || load >= 4 || !doorOpen) {
			wait();
		}
		waitExit[toFloor] += 1;
		load++;
		view.showDebugInfo(waitEntry, waitExit);
	}

	public synchronized void exitPassengerFromLift(int fromFloor, int toFloor) throws InterruptedException {
		while (currentFloor != toFloor) {
			wait();
		}

		passengers--;
	}

	private synchronized int countWillEnter(int currentFloor) {
		int emptySpace = 4 - load;
		int willEnter = 0;
		if (waitEntry[currentFloor] > emptySpace) { // om det finns fler som vill gå in än vad det finns ledig plats
			willEnter = emptySpace;
		} else if (waitEntry[currentFloor] < emptySpace) { // om det finns mer ledig plats än vad som vill gå in
			willEnter = waitEntry[currentFloor];
		} else {
			willEnter = emptySpace;
		}
		return willEnter;
	}

	private synchronized boolean checkEntering(int currentFloor) {
		if (waitEntry[currentFloor] > 0) {
			setWalking(true);
			reportPassengerEnteredLift(currentFloor);
			return true;
		}
		return false;
	}

	private synchronized boolean checkExiting(int currentFloor) {
		if (waitExit[currentFloor] > 0) {
			setWalking(true);
			reportPassengerExitedLift(currentFloor);
			return true;
		}
		return false;
	}

	public synchronized void checkCondition() throws InterruptedException {
		stop1 = checkExiting(currentFloor);
		stop2 = checkEntering(currentFloor);
		if (stop1 || stop2) {
			//walking = true;
			while (walking) {
				wait();
			}
			while (!walkers.isEmpty()) {
				wait();
			}
		}
	}

	public void moveUp() throws InterruptedException {
		for (int i = 0; i <= 5; i++) {
			view.moveLift(currentFloor, currentFloor + 1);
			currentFloor++;

			if (waitEntry[currentFloor] >= 1 && load <= 3 || waitExit[currentFloor] >= 1) {
				view.openDoors(currentFloor);
				doorOpen = true;
				checkCondition();
			}

			if (doorOpen) {
				view.closeDoors();
				doorOpen = false;

			}

		}

	}

	public void moveDown() throws InterruptedException {
		// view.showDebugInfo(waitEntry, waitExit);
		for (int i = 5; i >= 0; i--) {
			view.moveLift(currentFloor, currentFloor - 1);
			currentFloor--;

			if (waitEntry[currentFloor] >= 1 && load <= 3 || waitExit[currentFloor] >= 1) {
				doorOpen = true;
				view.openDoors(currentFloor);
				checkCondition();
			}

			if (doorOpen) {
				doorOpen = false;

				view.closeDoors();
			}
		}

	}

}
