package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 10;
	private WashingMessage program;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	private void sendAck() {
		program.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
	}

	@Override
	public void run() {

		try {
			while (true) {

				WashingMessage m = receiveWithTimeout(dt * 1000 / Settings.SPEEDUP);
				if (m != null) {
					program = m;
				}

				if (program != null) {
					switch (program.getCommand()) {
						case WashingMessage.TEMP_IDLE: {
							io.heat(false);
							sendAck();
							break;
						}

						case WashingMessage.TEMP_SET: {
							double tempWeWant = program.getValue();
							double currentTemp = io.getTemperature();
							double lowerMargin = dt * 0.000238 * (currentTemp - 20) - 0.085;
							double upperMargin = 0.478;

							if (currentTemp + lowerMargin < tempWeWant - 2 && currentTemp + upperMargin > tempWeWant) {
								sendAck();
							}

							if (currentTemp + lowerMargin < tempWeWant - 2) {
								io.heat(true);

							} else if (currentTemp + upperMargin > tempWeWant) {
								io.heat(false);

							}
							break;
						}

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
