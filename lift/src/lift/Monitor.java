package lift;

import java.util.concurrent.Semaphore;

public class Monitor {
	boolean moving = true; // true if the lift is moving, false if standing still with doors open
	int direction; // +1 if lift is going up, -1 if going down
	int[] waitEntry; // number of passengers waiting to enter the lift at the various floors
	int[] waitExit; // number of passengers (in lift) waiting to leave at the various floors
	int load; // number of passengers currently in the lift
	LiftView view;
	int currentFloor = 0;
	boolean stopLift = false;
	private boolean stop1, stop2 = false;
	private boolean doorOpen;
	int passengers;
	int numberOfConcurrentlyWalking;
	boolean walking;
	private int countWalkers;

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}

	public synchronized int getPassengers() {
		return passengers;
	}

	public synchronized void changeWalking(boolean a) {
		walking = a;
		notifyAll();
	}

	private synchronized void handleErrors() {
		if (waitExit[currentFloor] == 0) {
			changeWalking(false);
		}
	}

	private synchronized void incrementWalkers() {
		// countWalkers = countWillEnter(currentFloor) + waitExit[currentFloor];
		countWalkers++;
		notifyAll();
		// System.out.println("Current Walkers: " + countWalkers);
	}

	private synchronized void decrementWalkers() {
		countWalkers--;

		if (countWalkers == 0) {
			notifyAll();
		}

		System.out.println("Current Walkers: " + countWalkers);

	}

	public void handleWalkers(int fromFloor, int toFloor, Passenger pass) throws InterruptedException {
		pass.begin();
		putPassengeInLift(fromFloor, toFloor, pass);
		pass.enterLift();
		decrementWalkers();

		// handleErrors();
		exitPassengerFromLift(fromFloor, toFloor, pass);
		pass.exitLift();
		decrementWalkers();
		pass.end();

	}

	private synchronized void reportPassengerEnteredLift(int fromFloor) {
		// System.out.println("Waiting to Enter: " + waitEntry[fromFloor] + "\n");
		// System.out.println("Entered: " + countWillEnter(fromFloor));
		waitEntry[fromFloor] -= countWillEnter(fromFloor);
		// view.showDebugInfo(waitEntry, waitExit);
		notifyAll();
	}

	private synchronized void reportPassengerExitedLift(int toFloor) {
		load -= waitExit[toFloor];
		waitExit[toFloor] = 0;
		notifyAll();
	}

	public synchronized void putPassengeInLift(int fromFloor, int toFloor, Passenger pass) throws InterruptedException {
		waitEntry[fromFloor] += 1;
		passengers++;
		view.showDebugInfo(waitEntry, waitExit);
		while (fromFloor != currentFloor || load >= 4 || !doorOpen) {
			wait();
		}
		// changeWalking(true);
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

	public synchronized boolean checkEntering(int currentFloor) {
		if (waitEntry[currentFloor] > 0) {
			changeWalking(true);
			countWalkers += countWillEnter(currentFloor);

			reportPassengerEnteredLift(currentFloor);
			return true;
		}
		return false;
	}

	public synchronized boolean checkExiting(int currentFloor) {
		if (waitExit[currentFloor] > 0) {
			changeWalking(true);
			for (var i = 1; i <= waitExit[currentFloor]; i++) {
				incrementWalkers();
			}
			reportPassengerExitedLift(currentFloor);
			return true;
		}
		return false;
	}

	public synchronized void checkCondition() throws InterruptedException {

		stop1 = checkExiting(currentFloor);
		stop2 = checkEntering(currentFloor);
		// System.out.println("walking: " + walking);
		if (stop1 || stop2) {
			while (countWalkers != 0) {
				wait();
			}
			// System.out.println("Good to go");
		}

		/*
		 * 
		 * if (stop1 && stop2) { try { if (doorOpen) {
		 * //waitOutside((numberOfConcurrentlyWalking) + 1); while(walking) { wait(); }
		 * walking=false; stop1 = false; stop2 = false;
		 * 
		 * } } catch (InterruptedException e) { e.printStackTrace(); } } else if (stop2)
		 * { try { if (doorOpen) { // waitOutside(numberOfConcurrentlyWalking + 1);
		 * while(walking) { wait(); } walking=false;
		 * 
		 * stop1 = false; stop2 = false; } } catch (InterruptedException e) {
		 * e.printStackTrace(); } } else if (stop1) { try { if (doorOpen) {
		 * //waitOutside(numberOfConcurrentlyWalking + 1); while(walking) { wait(); }
		 * 
		 * walking=false;
		 * 
		 * stop2 = false; stop1 = false; } } catch (InterruptedException e) {
		 * e.printStackTrace(); } }
		 */
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
