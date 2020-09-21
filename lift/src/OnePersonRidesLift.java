


import lift.LiftThread;
import lift.LiftView;
import lift.Monitor;
import lift.Passenger;
import lift.PassengerThread;

public class OnePersonRidesLift {

    public static void main(String[] args) {
        LiftView  view = new LiftView();
    	Monitor monitor = new Monitor(view);


        
    	PassengerThread pt= new PassengerThread(monitor,view);
    	pt.start();
    	
    	LiftThread lt = new LiftThread(view,monitor);
    	//lt.start();
    	
    		lt.start();
    	
    	
    	
    	/*
        Passenger pass = view.createPassenger();
        int  fromFloor = pass.getStartFloor();
        int    toFloor = pass.getDestinationFloor();

        pass.begin();                        // walk in (from left)
        if (fromFloor != 0) {
            view.moveLift(0, fromFloor);
        }
        view.openDoors(fromFloor);
        pass.enterLift();                    // step inside

        view.closeDoors();
        view.moveLift(fromFloor, toFloor);   // ride lift
        view.openDoors(toFloor);

        pass.exitLift();                     // leave lift
        pass.end();                          // walk out (to the right)
    
    
    */
    }
    	
}
