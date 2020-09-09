import clock.AlarmClockEmulator;
import clock.io.AlarmHandler;
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
        Thread clockThread = new Thread(clock);
        AlarmHandler alarmHandler = new AlarmHandler(out, in, clock);
        Thread alarmThread = new Thread(alarmHandler);

        clockThread.start();
        alarmThread.start();

        while (true) {
            in.getSemaphore().acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();

            if (choice == 1) {
                clock.setTime(h, m, s);
            } else if (choice == 2) {
                alarmHandler.isAlarmOn(true);
                alarmHandler.setAlarm(h, m, s);
                // alarmHandler.soundTheAlarm(clock.getTime().getCurrentTime());

            } else if (choice == 3) {
                alarmHandler.isAlarmOn(false);
            }

            System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }

    }
}
