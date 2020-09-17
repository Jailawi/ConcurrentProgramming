package train.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Monitor {
	private Set<Segment> busySegments = new HashSet<Segment>();

	public synchronized void moveHead(Segment head, List<Segment> train) throws InterruptedException {
		while (busySegments.contains(head)) {
			wait();
		}
		busySegments.add(head);
		head.enter();
		train.add(0, head);
	}

	public synchronized void moveTail(Segment tail) throws InterruptedException {
		busySegments.remove(tail);
		notifyAll();
	}

	public void move(Route route, List<Segment> train) throws InterruptedException {
		Segment head = route.next();
		moveHead(head, train);
		Segment tail = train.remove(train.size() - 1);
		tail.exit();
		moveTail(tail);
	}
}
