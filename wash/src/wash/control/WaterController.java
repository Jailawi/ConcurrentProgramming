package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 5000;

	private WashingMessage program;

    // TODO: add attributes

    public WaterController(WashingIO io) {
    	this.io =io;
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
    				
    				
    				if(program != null) {
    					switch (program.getCommand()) {
						case WashingMessage.WATER_DRAIN: {
							while (io.getWaterLevel() > 0) {
	                			io.drain(true);
		                		io.fill(false);

	                		}
	                		io.drain(false);
	                		sendAck();
	                		break;

	                		}
							
						
						case WashingMessage.WATER_FILL: {
							if (io.getWaterLevel() < program.getValue()) {
								io.drain(false);
	                			io.fill(true);
	                		}
	                		else {
	                			sendAck();
	                			io.fill(false);
	                		}
	                		io.drain(false);
	                		break;
							
						}
						
						case WashingMessage.WATER_IDLE: {
							io.fill(false);
	                		io.drain(false);
	                		sendAck();
	                		break;
							
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
