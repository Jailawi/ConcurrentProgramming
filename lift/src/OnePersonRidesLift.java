
import lift.LiftThread;
import lift.LiftView;
import lift.Monitor;
import lift.Passenger;
import lift.PassengerThread;

public class OnePersonRidesLift {
 
    public static void main(String[] args) {
        LiftView view = new LiftView();
        Monitor monitor = new Monitor(view);

        LiftThread lt = new LiftThread(monitor);
        Thread lift = new Thread(lt);
        lift.start();

        for (var i = 0; i < 20; i++) {
            PassengerThread pt = new PassengerThread(monitor, view);
            Thread pass = new Thread(pt);
            pass.start();
        }

    }

}
