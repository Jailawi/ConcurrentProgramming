package lift;



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

	public Monitor(LiftView view) {
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
	}
	
	public  void isThereAPassenger() {
		System.out.println("passengers: " + passengers);
		while(passengers >= 1) {
			moveUp();
			moveDown();
		}
	}
	
	public synchronized int getNbrOfExiting(int floor) {
		return waitExit[floor];
	}
	
	public synchronized int getNbrOfEntering(int floor) {
		return waitEntry[floor];
	}

	private synchronized void reportPassengerEnteredLift(int fromFloor) {

		waitEntry[fromFloor] -= 1;
		notifyAll();
	}

	private synchronized void reportPassengerExitedLift(int fromFloor) {
		waitExit[fromFloor] = waitExit[fromFloor] -= 1;
		notifyAll();
	}

	public synchronized void setPassengerTravel(int fromFloor, int toFloor, Passenger pass)
			throws InterruptedException {

		waitEntry[fromFloor] += 1;
		passengers++;


		while (fromFloor != currentFloor || load == 4 || !doorOpen) {
			wait();
		}
		waitExit[toFloor] += 1;

		pass.enterLift();
		load++;
		while (currentFloor != toFloor) {
			wait();
		}
		
		pass.exitLift();
		passengers--;
		load--;

	}
	

	public synchronized boolean checkEntering(int currentFloor) {
		// System.out.println(waitEntry[currentFloor]);
		view.showDebugInfo(waitEntry, waitExit);
		if (waitEntry[currentFloor] > 0) {
			reportPassengerEnteredLift(currentFloor);

			this.currentFloor = currentFloor;
			return true;
		}
		return false;
	}

	public synchronized boolean checkExiting(int currentFloor) {

		 System.out.println(waitExit[currentFloor]);
		if (waitExit[currentFloor] > 0) {
			System.out.println("passenger should exit here");
			this.currentFloor = currentFloor;
			reportPassengerExitedLift(currentFloor);
			return true;
		}
		return false;
	}
	
	private synchronized void waitOutside(int x) throws InterruptedException {
		wait(1250 * x);
	}
	
	
	public void moveUp() {
		for (int i = 0; i <= 5; i++) {
			view.moveLift(currentFloor, currentFloor + 1);
			currentFloor++;
			if(waitEntry[currentFloor] >=1 && load <=3 || waitExit[currentFloor] >=1) {
			view.openDoors(currentFloor);
			doorOpen=true;
			}
			stop1 = checkExiting(currentFloor);
			stop2 = checkEntering(currentFloor);
			if (stop1&&stop2) {
				try {
					//System.out.println("waitexit: " +waitExit[currentFloor]);
					if(doorOpen) {
					waitOutside(Math.abs(( waitEntry[currentFloor]-load + waitExit[currentFloor]))+1);
					
					stop1 = false;
					stop2=false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(stop2) {
				try {
					if(doorOpen) {
					waitOutside(Math.abs(( waitEntry[currentFloor]-load))+1);
					stop1=false;
					stop2 = false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(stop1) {
				try {
					if(doorOpen) {
					waitOutside(waitExit[currentFloor]+1);
					stop2 = false;
					stop1= false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(doorOpen) {
				doorOpen=false;
				view.closeDoors();
				}
		}
		
	}
	
	
	public void moveDown() {
	for (int i = 5; i >= 0; i--) {
		// System.out.println("this should be false: " + stop);
		view.moveLift(currentFloor, currentFloor - 1);
		currentFloor--;
		//System.out.println(currentFloor);
		System.out.println("isDoorOpen: " + doorOpen);
		if(waitEntry[currentFloor] >=1 && load <=3 || waitExit[currentFloor] >=1) {
			view.openDoors(currentFloor);
			doorOpen=true;
			}
		// checkExiting(currentFloor);
		stop1 = checkExiting(currentFloor);
		stop2 = checkEntering(currentFloor);
		if (stop1&&stop2) {
			try {
				//System.out.println("waitexit: " +waitExit[currentFloor]);
				if(doorOpen) {
				waitOutside(Math.abs(( waitEntry[currentFloor]-load + waitExit[currentFloor]))+1);
				
				stop1 = false;
				stop2=false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(stop2) {
			try {
				if(doorOpen) {
				waitOutside(Math.abs(( waitEntry[currentFloor]-load))+1);
				stop1=false;
				stop2 = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(stop1) {
			try {
				if(doorOpen) {
				waitOutside(waitExit[currentFloor]+1);
				stop2 = false;
				stop1= false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(doorOpen) {
		doorOpen=false;
		view.closeDoors();
		}
	}
	
}


}
