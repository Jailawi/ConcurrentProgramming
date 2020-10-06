package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private int dt = 5000;

	private ActorThread<WashingMessage> program;

    // TODO: add attributes

    public WaterController(WashingIO io) {
    	this.io =io;
    }
    

	private void sendAck() {
		program.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
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
    				
    				
    				while(program != null) {
    					switch (m.getCommand()) {
						case WashingMessage.WATER_DRAIN: {
							if (io.getWaterLevel() == 0) {
	                			io.drain(false);
	                			sendAck();
	                		}
	                		else {
	                			io.drain(true);
	                		}
	                		io.fill(false);
	                		break;
							
						}
						case WashingMessage.WATER_FILL: {
							if (io.getWaterLevel() < m.getValue()) {
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
