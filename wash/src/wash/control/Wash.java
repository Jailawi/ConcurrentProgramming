package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {
    private static ActorThread<WashingMessage> program;

    public static void main(String[] args) throws InterruptedException {

        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);
        WashingIO io = sim.startSimulation();
        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();

        while (true) {
            int n = io.awaitButton();

            if (n == 0) {
                if (program != null) {
                    program.interrupt();
                    program = null;
                }
            }
            if (n == 1) {
                program = new WashingProgram1(io, temp, water, spin);
                program.start();
            }
            if (n == 2) {
                program = new WashingProgram2(io, temp, water, spin);
                program.start();
            }

            if (n == 3) {
                program = new WashingProgram3(io, temp, water, spin);
                program.start();

            }

        }
    }
};
