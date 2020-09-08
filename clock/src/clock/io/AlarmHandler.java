package clock.io;

public class AlarmHandler {
    private int h, m, s;
    Time alarmTime = new Time(h, m, s);
    private ClockOutput out;
    private ClockInput in;
    private boolean on = false;
    private Clock clock;

    public AlarmHandler(ClockOutput out, ClockInput in, Clock clock) {
        this.out = out;
        this.in = in;
        this.clock = clock;
    }

    public void setAlarm(int h, int m, int s) {
        this.h = h;
        this.m = m;
        this.s = s;
        // out.displayTime(h, m, s);
    }

    // anledningen till att vi skriver så är för att lägga till mutex sen
    public void isAlarmOn(boolean indicator) {
        out.setAlarmIndicator(indicator);
        on = true;
    }

    public boolean isOn() {
        return on;
    }

    // it should beep every 20 sec
    public void alarmBeep() {
        long t0 = System.currentTimeMillis();
        int sec = 0;
        try {
            for (var i = 0; i <= 20; i++) {
                long now = System.currentTimeMillis();
                out.alarm();
                Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
                sec++;
            }

        } catch (InterruptedException e) {
            throw new Error(e);
        }

    }

    public void checkAlarm() {
        // System.out.print("working outside if statement");
        if (alarmTime.equals(clock.getTime().getCurrentTime())) {
            System.out.print("working inside if statement");
            alarmBeep();

        }
    }

}