package lift;

public class LiftThread implements Runnable {
	private LiftView lift;
	private Monitor m;
	private int currentFloor = 0;
	private int next = 1;
	private int nextFloor = currentFloor + next;
	private boolean stop = false;

	public LiftThread(LiftView lift, Monitor m) {
		this.lift = lift;
		this.m = m;
	}

	private synchronized void waitOutside() throws InterruptedException {
		wait(1000);
	}

	@Override
	public void run() {
		while (true) {

			for (int i = 0; i <= 5; i++) {
				// System.out.println("this should be false: " + stop);
				lift.moveLift(currentFloor, currentFloor + 1);
				currentFloor++;
				lift.openDoors(currentFloor);

				stop = m.checkEntering(currentFloor);
				// System.out.println("this should be true if passenger is waiting: " + stop);

				if (stop) {
					try {
						waitOutside();
						stop = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				lift.closeDoors();

			}

			for (int i = 5; i >= 0; i--) {
				// System.out.println("this should be false: " + stop);
				lift.moveLift(currentFloor, currentFloor - 1);
				currentFloor--;
				System.out.println(currentFloor);
				lift.openDoors(currentFloor);
				// checkExiting(currentFloor);
				stop = m.checkEntering(currentFloor);
				if (stop) {
					try {
						waitOutside();
						stop = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lift.closeDoors();
			}
		}
	}

}
