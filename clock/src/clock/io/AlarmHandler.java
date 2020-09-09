package clock.io;

import java.util.concurrent.Semaphore;

public class AlarmHandler implements Runnable {
    private int h, m, s;
    Time alarmTime = new Time(h, m, s);
    private ClockOutput out;
    private boolean on = false;
    private Clock clock;
    private Semaphore sem = new Semaphore(1);

    public AlarmHandler(ClockOutput out, Clock clock) {
        this.out = out;
        this.clock = clock;
    }

    public void setAlarm(int h, int m, int s) {
        this.h = h;
        this.m = m;
        this.s = s;
    }

    // anledningen till att vi skriver så är för att lägga till mutex sen
    public void isAlarmOn(boolean indicator) {
        out.setAlarmIndicator(indicator);
        on = indicator;
    }

    // it should beep for 20 sec
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

    public void checkAlarm() throws InterruptedException {
        sem.acquire();
        boolean timeEqualsAlarm = h == clock.getTime().getHour() && m == clock.getTime().getMin()
                && s == clock.getTime().getSec();
        if (timeEqualsAlarm) {
            alarmBeep();
        }
        sem.release();
    }

    @Override
    public void run() {
        while (true) {
            try {
                checkAlarm();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}