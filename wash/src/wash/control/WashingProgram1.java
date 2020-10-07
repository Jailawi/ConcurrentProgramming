package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;

    public WashingProgram1(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
            ActorThread<WashingMessage> spin) {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }

    /*
     * Lock the hatch, let water into the machine, heat to 40◦C, keep the
     * temperature for 30 minutes, drain, rinse 5 times 2 minutes in cold water,
     * centrifuge for 5 minutes and unlock the hatch. While washing and rinsing the
     * barrel should spin slowly, switching between left and right direction every
     * minute. While centrifuging, the drain pump should run to evacuate excess
     * water.
     */

    @Override
    public void run() {
        try {
            System.out.println("begin washingprogram1");
            // Lock the hatch
            io.lock(true);

            System.out.println("Filling with Water");
            water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
            WashingMessage ackWater = receive();
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            System.out.println("setting SPIN_SLOW...");
            spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
            System.out.println("Heating to 40");
            temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));

            // Keep the temp for 30min
            Thread.sleep(30 * 60000 / Settings.SPEEDUP);
            WashingMessage ackTempSet = receive();
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            WashingMessage ackTempIdle = receive();
            System.out.println("Now we drain");
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            WashingMessage ackDrain = receive();

            System.out.println("Rinsing");
            for (int i = 0; i < 5; i++) {
                water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
                WashingMessage ackRinse = receive();
                water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
                spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
                Thread.sleep(2 * 60000 / Settings.SPEEDUP);
                water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
                ackDrain = receive();

            }

            spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
            WashingMessage ackSpinFast = receive();
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            ackDrain = receive();
            Thread.sleep(5 * 60000 / Settings.SPEEDUP);

            io.lock(false);

            System.out.println("washing program 1 finished");
        } catch (InterruptedException e) {

            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
