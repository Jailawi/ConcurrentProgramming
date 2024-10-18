package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 10;
	private WashingMessage program;
	private boolean goalTempReached = false;
	private boolean tempOn = false;

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

				WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);
				if (m != null) {
					program = m;
				}

				if (program != null) {
					switch (program.getCommand()) {
						case WashingMessage.TEMP_IDLE:
							if (tempOn) {
								goalTempReached = false;
								io.heat(false);
								sendAck();
								tempOn = false;
							}
							break;

						case WashingMessage.TEMP_SET:
							double tempWeWant = program.getValue();
							double currentTemp = io.getTemperature();
							double lowerMargin = dt * 0.000238 * (currentTemp - 20) + 0.2;
							double upperMargin = 0.7;

							if (currentTemp > tempWeWant - 2 && !goalTempReached) {
								sendAck();
								goalTempReached = true;
								tempOn = true;
							}

							if (currentTemp - lowerMargin < tempWeWant - 2) {
								io.heat(true);
								tempOn = true;

							} else if (currentTemp + upperMargin > tempWeWant) {
								io.heat(false);

							}
							break;

					}
				}

			}

		} catch (InterruptedException e) {

			throw new Error();

		}
	}
}
