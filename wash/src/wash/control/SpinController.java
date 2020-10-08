package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {
    private WashingIO io;
    private int SPIN_IDLE = 1; // barrel not rotating
    private int SPIN_LEFT = 2; // barrel rotating slowly, left
    private int SPIN_RIGHT = 3; // barrel rotating slowly, right
    private int SPIN_FAST = 4; // barrel rotating fast
    private boolean spinOn = false;
    private WashingMessage program;

    public SpinController(WashingIO io) {
        this.io = io;
    }

    private void sendAck() {
        program.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
    }

    @Override
    public void run() {
        try {

            // ... TODO ...

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);
                if (m != null) {
                    program = m;
                }
                // if m is null, it means a minute passed and no message was received
                if (program != null) {
                    switch (program.getCommand()) {
                        case WashingMessage.SPIN_SLOW:
                            if (spinOn == false) {
                                sendAck();
                                spinOn = true;
                            }
                            while (true) {
                                io.setSpinMode(SPIN_LEFT);
                                program = receiveWithTimeout(60000 / Settings.SPEEDUP);
                                if (program != null && program.getCommand() != WashingMessage.SPIN_SLOW) {
                                    break;
                                }
                                io.setSpinMode(SPIN_RIGHT);
                                program = receiveWithTimeout(60000 / Settings.SPEEDUP);
                                if (program != null && program.getCommand() != WashingMessage.SPIN_SLOW) {
                                    break;
                                }
                            }
                            break;
                        case WashingMessage.SPIN_FAST:
                            if (spinOn == false) {
                                spinOn = true;
                                io.setSpinMode(SPIN_FAST);
                                sendAck();
                            }
                            break;
                        case WashingMessage.SPIN_OFF:
                            if (spinOn) {
                                io.setSpinMode(SPIN_IDLE);
                                spinOn = false;
                                sendAck();
                            }
                            break;
                        default:
                            System.out.println("Invalid command try again");
                    }

                }
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
