package train.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Todo: One train needs to move when the other one stops
public class Monitor {
	Set<Segment> busySegments = new HashSet<Segment>();
	
	public synchronized void addAsBusy(List<Segment> s) {
		for(int i=0; i<3; i++) {
			busySegments.add(s.get(i));
		}
	}
	
	
	
	public synchronized void moveHead(Route route, List<Segment> train) throws InterruptedException {
		Segment head = route.next();
		while(busySegments.contains(head)) {
			wait();
		}
		busySegments.add(head);
		train.add(0, head);
		head.enter();
		
	}

	
	public synchronized void moveTail(Route route, List<Segment> train) throws InterruptedException{
		Segment tail = train.remove(train.size() - 1);
		busySegments.remove(tail);
		tail.exit();
		notifyAll();
	}
	
	public synchronized void move(Route route, List<Segment> train) throws InterruptedException {
		moveHead(route, train);
		moveTail(route, train);
	}
	
	

	
	
	

}
