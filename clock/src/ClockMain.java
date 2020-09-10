import clock.AlarmClockEmulator;
import clock.io.AlarmHandler;
import clock.io.Clock;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

    private static int SET_TIME = 1; // user set new clock time
    private static int SET_ALARM = 2; // user set new alarm time
    private static int TOGGLE_ALARM = 3; // user pressed both buttons simultaneously
    private static boolean toggleAlarm;

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput in = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        Clock clock = new Clock(out);
        Thread clockThread = new Thread(clock);
        AlarmHandler alarmHandler = new AlarmHandler(out, clock);
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

            if (choice == SET_TIME) {
                clock.setTime(h, m, s);
            } else if (choice == SET_ALARM) {
                toggleAlarm = true;
                alarmHandler.isAlarmOn(toggleAlarm);
                alarmHandler.setAlarm(h, m, s);

            } else if (choice == TOGGLE_ALARM) {
                toggleAlarm = !toggleAlarm;
                alarmHandler.isAlarmOn(toggleAlarm);
            }

            System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }

    }
}
