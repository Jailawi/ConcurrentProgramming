package lift;


public class Monitor {
	//int floor; // the floor the lift is currently on
	boolean moving; // true if the lift is moving, false if standing still with doors open
	int direction; // +1 if lift is going up, -1 if going down
	int[] waitEntry; // number of passengers waiting to enter the lift at the various floors
	int[] waitExit; // number of passengers (in lift) waiting to leave at the various floors
	int load; // number of passengers currently in the lift
	LiftView view;
	int currentLevel = 0;

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}


	public synchronized int reportPassenger(int fromFloor) {
		waitEntry[fromFloor] = waitEntry[fromFloor] += 1;
		return fromFloor;
	}

	public synchronized void reportExitDestination(int toFloor) {
		waitExit[toFloor] = waitExit[toFloor] += 1;
	}
	
	public synchronized void addPassengerInLift(Passenger pass) {
		 int  fromFloor = pass.getStartFloor();
		 reportPassenger(fromFloor);
	     int  toFloor = pass.getDestinationFloor();
		 reportExitDestination(toFloor);
	     pass.begin();
	     if(currentLevel==fromFloor) {
	     pass.enterLift();
	     }
		
	}
	
	/*
	public synchronized void handleDoorOpen(int level) throws InterruptedException {
		view.openDoors(level);
		while(waitEntry[level] !=0) {
			wait();
		}
	}
	
	
	*/

	public void moveLift() throws InterruptedException {
		while (true) {

			for (int i = 0; i <= 6; i++) {
				if (currentLevel != i) {
					view.moveLift(currentLevel, i);
					view.openDoors(i);
					view.closeDoors();
						
					currentLevel++;

				}
			}

			for (int i = 6; i >= 0; i--) {
				if (currentLevel != i) {

					view.moveLift(currentLevel--, i);
					view.openDoors(i);
					view.closeDoors();
				}
			}
		}
	}

}
