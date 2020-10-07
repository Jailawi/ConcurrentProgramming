package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 10000;
	private ActorThread<WashingMessage> program;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}


	@Override
	public void run() {

		try {
			while (true) {

				WashingMessage m = receiveWithTimeout(dt / Settings.SPEEDUP);
				if (m != null) {
					// The thread that sent the message
					program = m.getSender();
				}

				while (program != null) {
					switch (m.getCommand()) {
					case WashingMessage.TEMP_IDLE: {
						io.heat(false);
						break;
					}

					case WashingMessage.TEMP_SET: {
						double tempWeWant = m.getValue();
						double lowerMargin = dt * 9.52 * 0.001;
						double upperMargin = dt * 0.0478;
						// måste kolla matten igen
						if (io.getTemperature() - upperMargin < tempWeWant) {
							io.heat(true);

						} else {
							io.heat(false);
							break;
						}
					}
					 default:
                         System.out.println("Invalid command try again");

					}
				}

			}

		} catch (InterruptedException e) {
			  // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error();

		}
	}
}
