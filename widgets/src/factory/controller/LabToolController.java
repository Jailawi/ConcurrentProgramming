package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.simulation.Painter;
import factory.simulation.Press;
import factory.swingview.Factory;

/**
 * Implementation of the ToolController interface,
 * to be used for the Widget Factory lab.
 * 
 * @see ToolController
 */
public class LabToolController implements ToolController {
    private final DigitalSignal conveyor, press, paint;
    private final long pressingMillis, paintingMillis;
    private boolean conveyorIsOn;
    private boolean isBusy;
    
    
    public LabToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis, long paintingMillis) {
        this.conveyor = conveyor;
        this.press = press;
        this.paint = paint;
        this.pressingMillis = pressingMillis;
        this.paintingMillis = paintingMillis;
        this.conveyorIsOn=true;
    }

    @Override
    public void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method.
        //
        // Note that this method can be called concurrently with onPaintSensorHigh
        // (that is, in a separate thread).
        //
        if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
        	turnOff();
            press.on();
            Thread.sleep(pressingMillis);
            press.off();
            Thread.sleep(pressingMillis);   // press needs this time to retract
            turnOn();

            isBusy=false;
            notifyAll();

        }
    }

    @Override
    public void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
      
        if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
        	turnOff();
        	paint.on();
            Thread.sleep(paintingMillis);
            paint.off();
            turnOn();
            isBusy=false;
            notifyAll();

        }
    }
    
    
    private synchronized void turnOn() throws InterruptedException {
    	while(isBusy) {
    		wait();
    	}
    	conveyorIsOn=true;
    	conveyor.on();
    	
    }
    
    private synchronized void turnOff() throws InterruptedException {
    	while(conveyorIsOn==false) { 
    		isBusy=true;
    	}
    	
    	conveyorIsOn=false; 
    	conveyor.off();
    
    
    }
      
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {
        Factory factory = new Factory();
        ToolController toolController = new LabToolController(factory.getConveyor(),
                                                              factory.getPress(),
                                                              factory.getPaint(),
                                                              Press.PRESSING_MILLIS,
                                                              Painter.PAINTING_MILLIS);
        factory.startSimulation(toolController);
    }
}
