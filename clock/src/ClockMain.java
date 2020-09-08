import clock.AlarmClockEmulator;
import clock.io.Clock;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput in = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        Clock clock = new Clock(out, in);
        Thread thread = new Thread(clock);
        thread.start();

        while (true) {
            in.getSemaphore().acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();

            if (choice == 1) {
                clock.setTime(h, m, s);
            }

            // System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }

    }
}
