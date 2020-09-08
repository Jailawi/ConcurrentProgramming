import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import clock.io.ClockThread;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        
      
   

        
        
  
        while (true) {
        	in.getSemaphore().acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
           
            ClockThread thread= new ClockThread(out,in);
            thread.start();
            
            if(choice == 1) {
            thread.time.getCurrentTime().getTimeFromUser(userInput);
            }

          //  System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }
    }
}
