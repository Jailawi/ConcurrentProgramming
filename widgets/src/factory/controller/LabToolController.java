package factory.controller;

import java.util.concurrent.Semaphore;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.simulation.Painter;
import factory.simulation.Press;
import factory.swingview.Factory;

/**
 * Implementation of the ToolController interface, to be used for the Widget
 * Factory lab.
 * 
 * @see ToolController
 */
public class LabToolController implements ToolController {
    private final DigitalSignal conveyor, press, paint;
    private final long pressingMillis, paintingMillis;
    private boolean conveyorIsOn;
    private boolean isBusy;
    private Semaphore sem = new Semaphore(2);

    public LabToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis,
            long paintingMillis) {
        this.conveyor = conveyor;
        this.press = press;
        this.paint = paint;
        this.pressingMillis = pressingMillis;
        this.paintingMillis = paintingMillis;
        this.conveyorIsOn = true;
    }

    @Override
    public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        //
        // Note that this method can be called concurrently with onPaintSensorHigh
        // (that is, in a separate thread).
        //
        if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
            turnOff();
            press.on();
            // wait(pressingMillis);
            waitOutside(pressingMillis);
            press.off();
            // wait(pressingMillis);
            waitOutside(pressingMillis);
            turnOn();

        }
    }

    @Override
    public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {

        if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
            turnOff();
            paint.on();
            waitOutside(paintingMillis);
            // wait(paintingMillis);
            paint.off();
            turnOn();

        }
    }

    private void waitOutside(long millis) throws InterruptedException {
        long timeToWakeUp = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < timeToWakeUp) {
            long dt = timeToWakeUp - System.currentTimeMillis();
            wait(dt);
        }
    }

    private synchronized void turnOn() throws InterruptedException {
        sem.release();
        // Om båda har släppt kan vi gå vidare
        if (sem.availablePermits() == 2) {
            if (conveyorIsOn == false) {
                conveyorIsOn = true;
                conveyor.on();
            }
        }

    }

    private synchronized void turnOff() throws InterruptedException {
        sem.acquire();

        if (conveyorIsOn) {
            conveyorIsOn = false;
            conveyor.off();

        }
    }

    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        Factory factory = new Factory();
        ToolController toolController = new LabToolController(factory.getConveyor(), factory.getPress(),
                factory.getPaint(), Press.PRESSING_MILLIS, Painter.PAINTING_MILLIS);
        factory.startSimulation(toolController);
    }
}
