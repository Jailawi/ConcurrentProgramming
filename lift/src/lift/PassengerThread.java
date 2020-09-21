package lift;

public class PassengerThread extends Thread{
	private Monitor monitor;
	private LiftView lf;
	
	 public PassengerThread(Monitor m,LiftView lf) {
		this.monitor=m;
		this.lf=lf;
	}

	
	@Override
	public void run() {
		while(true) {
			//wait(4500);
			 Passenger pass = lf.createPassenger();
			 monitor.addPassengerInLift(pass);
			
		}
	}
}
