package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 10;
	private ActorThread<WashingMessage> program;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}


	@Override
	public void run() {

		try {
			WashingMessage prev=null;
			while (true) {

				WashingMessage m = receiveWithTimeout(dt * 1000/ Settings.SPEEDUP);
				if (m != null) {
					// The thread that sent the message
					//program = m.getSender();
					prev=m;
				}

				if (prev != null) {
					switch (prev.getCommand()) {
					case WashingMessage.TEMP_IDLE: {
						io.heat(false);
						break;
					}

					case WashingMessage.TEMP_SET: {
						double tempWeWant = prev.getValue();
						double lowerMargin = 0.6;
						double upperMargin = 0.3;
						if (io.getTemperature() + upperMargin < tempWeWant-2) {
							io.heat(true);
							

						} else if(io.getTemperature()+lowerMargin >tempWeWant ){
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
