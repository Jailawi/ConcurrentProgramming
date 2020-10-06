package clock.io;

import java.util.concurrent.Semaphore;

public class AlarmHandler implements Runnable {
    private int h, m, s;
    private ClockOutput out;
    private boolean on = false;
    private Clock clock;
    private Semaphore sem = new Semaphore(1);

    public AlarmHandler(ClockOutput out, Clock clock) {
        this.out = out;
        this.clock = clock;
    }

    public void setAlarm(int h, int m, int s) throws InterruptedException {
        sem.acquire();
        this.h = h;
        this.m = m;
        this.s = s;
        sem.release();
    }

    public void isAlarmOn(boolean indicator) throws InterruptedException {
        out.setAlarmIndicator(indicator);
        on = indicator;
    }

    public boolean isOn() throws InterruptedException {
        return on;
    }

    public void alarmBeep() {

        try {
            long t0 = System.currentTimeMillis();
            int sec = 0;
            for (var i = 0; i <= 20; i++) {
                long now = System.currentTimeMillis();
                if (isOn()) {
                    out.alarm();
                } else {
                    break;
                }
                Thread.sleep((t0 + ((sec + 1) * 1000)) - now);
                sec++;
            }
        } catch (InterruptedException e) {
            throw new Error(e);
        }

    }

    public void checkAlarm() throws InterruptedException {
        sem.acquire();
        boolean currentTimeEqualsAlarm = h == clock.getTime().getHour() && m == clock.getTime().getMin()
                && s == clock.getTime().getSec();
        if (currentTimeEqualsAlarm) {
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