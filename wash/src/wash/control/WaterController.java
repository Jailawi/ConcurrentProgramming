package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 5000;
	private boolean waterOn = false;
	private boolean waterDrained = false;
	private WashingMessage program;

	// TODO: add attributes

	public WaterController(WashingIO io) {
		this.io = io;
	}

	private void sendAck() {
		program.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
	}

	@Override
	public void run() {

		try {
			while (true) {

				WashingMessage m = receiveWithTimeout(dt / Settings.SPEEDUP);
				if (m != null) {
					// The thread that sent the message
					program = m;
				}

				if (program != null) {
					switch (program.getCommand()) {
						case WashingMessage.WATER_DRAIN:
							System.out.println("shako mako");
							while (io.getWaterLevel() > 0) {
								io.drain(true);
								io.fill(false);
							}
							io.drain(true);
							if (io.getWaterLevel() <= 0 && !waterDrained) {
								waterDrained = true;
								waterOn = false;
								sendAck();
							}
							break;

						case WashingMessage.WATER_FILL:
							while (io.getWaterLevel() < program.getValue()) {
								io.drain(false);
								io.fill(true);
								waterDrained = false;
							}
							io.fill(false);
							io.drain(false);
							if (io.getWaterLevel() >= program.getValue() && !waterOn) {
								waterOn = true;
								waterDrained = false;
								sendAck();
							}
							break;

						case WashingMessage.WATER_IDLE:
							if (waterOn) {
								io.fill(false);
								io.drain(false);
								sendAck();
								waterDrained = false;
								waterOn = false;
							}
							break;

						default:
							System.out.println("Invalid command try again");

					}
				}

			}
		} catch (

		InterruptedException e) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error();

		}

	}
}
