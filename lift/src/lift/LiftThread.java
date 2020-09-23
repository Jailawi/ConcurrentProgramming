package lift;

public class LiftThread implements Runnable {
	private LiftView lift;
	private Monitor m;
	private int currentFloor = 0;
	private int next = 1;
	private int nextFloor = currentFloor + next;


	public LiftThread(LiftView lift, Monitor m) {
		this.lift = lift;
		this.m = m;
	}

	

	@Override
	public void run() {
	
		while (true) {

			m.isThereAPassenger();
		
		}
	}
	
	
}
