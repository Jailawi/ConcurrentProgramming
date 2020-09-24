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

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}

	public synchronized int getPassengers() {
		return passengers;
	}
	
	public synchronized void changeWalking(boolean a) {
		walking=a;
		notifyAll();
	}
	
	public void handleWalkers(int fromFloor, int toFloor, Passenger pass) throws InterruptedException {
		pass.begin();
		putPassengeInLift(fromFloor, toFloor, pass);
		//changeWalking(true);

		pass.enterLift();
		
		changeWalking(false);
		exitPassengerFromLift(fromFloor, toFloor, pass);
		pass.exitLift();
		changeWalking(false);
		
		pass.end();
		
	}
	


	private synchronized void reportPassengerEnteredLift(int fromFloor) {
		System.out.println("Waiting to Enter: " + waitEntry[fromFloor] + "\n");
		System.out.println("Entered: " + countWillEnter(fromFloor));
		waitEntry[fromFloor] -= countWillEnter(fromFloor);
		// view.showDebugInfo(waitEntry, waitExit);
		notifyAll();
	}

	private synchronized void reportPassengerExitedLift(int toFloor) {
		load -= waitExit[toFloor];
		waitExit[toFloor] = 0;

		// view.showDebugInfo(waitEntry, waitExit);
		notifyAll();
	}

	public synchronized void putPassengeInLift(int fromFloor, int toFloor, Passenger pass) throws InterruptedException {
		waitEntry[fromFloor] += 1;
		passengers++;
		while (fromFloor != currentFloor || load >= 4 || !doorOpen) {
			wait();
		}
		//changeWalking(true);
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
		if (waitEntry[currentFloor] > emptySpace) {
			willEnter = emptySpace;
		} else if (waitEntry[currentFloor] < emptySpace) {
			willEnter = waitEntry[currentFloor];
		} else {
			willEnter = emptySpace;
		}
		return willEnter;
	}

	public synchronized boolean checkEntering(int currentFloor) {
		// view.showDebugInfo(waitEntry, waitExit);
		if (waitEntry[currentFloor] > 0) {
			changeWalking(true);

			reportPassengerEnteredLift(currentFloor);
			return true;
		}
		return false;
	}

	public synchronized boolean checkExiting(int currentFloor) {

		// System.out.println(waitExit[currentFloor]);
		if (waitExit[currentFloor] > 0) {
			changeWalking(true);

			// System.out.println("passenger should exit here");
			// this.currentFloor = currentFloor;
			// System.out.println("Wants to exit: " + waitExit[currentFloor]);
			// for (var i = 1; i <= waitExit[currentFloor]; i++) {
			reportPassengerExitedLift(currentFloor);
			// }
			return true;
		}
		return false;
	}

	private synchronized void waitOutside(int x) throws InterruptedException {
		wait(2000 * x);
		// 1250
	}

	private synchronized void concurrentWalkers(int currentFloor) {
		// numberOfConcurrentlyWalking = waitExit[currentFloor] + 4 - (load -
		// waitExit[currentFloor]);
		int willEnter = countWillEnter(currentFloor);
		numberOfConcurrentlyWalking = waitExit[currentFloor] + willEnter;
		// System.out.println("CurrentWalkers " + numberOfConcurrentlyWalking);
	}

	public synchronized void checkCondition() throws InterruptedException {

		stop1 = checkExiting(currentFloor);
		stop2 = checkEntering(currentFloor);
		concurrentWalkers(currentFloor);
		System.out.println("walking: " + walking);
		if(stop1||stop2) {
			while(walking) {
				wait();
				System.out.println("im inside");

			//}
				walking=false;
			System.out.println("im done");

		}
		}
		
		/*

		if (stop1 && stop2) {
			try {
				if (doorOpen) {
					//waitOutside((numberOfConcurrentlyWalking) + 1);
					while(walking) {
						wait();
					}
					walking=false;
					stop1 = false;
					stop2 = false;
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (stop2) {
			try {
				if (doorOpen) {
				//	waitOutside(numberOfConcurrentlyWalking + 1);
					while(walking) {
						wait();
					}
					walking=false;

					stop1 = false;
					stop2 = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (stop1) {
			try {
				if (doorOpen) {
					//waitOutside(numberOfConcurrentlyWalking + 1);
					while(walking) {
						wait();
					}
						
					walking=false;

					stop2 = false;
					stop1 = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
*/
	}

	public void moveUp() throws InterruptedException {
		for (int i = 0; i <= 5; i++) {
			view.moveLift(currentFloor, currentFloor + 1);
			currentFloor++;
			
			if (waitEntry[currentFloor] >= 1 && load <= 3 || waitExit[currentFloor] >= 1) {
				view.openDoors(currentFloor);
				doorOpen=true;
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
		

			}
			checkCondition();
			if (doorOpen) {
				doorOpen = false;
				
				view.closeDoors();
			}
		}

	}

}
