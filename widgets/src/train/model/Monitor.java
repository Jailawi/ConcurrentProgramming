package train.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Måste fixa detta:
 *  If your trains seem to move erratically, ensure your synchronization doesn’t harm concurrency.
If only one train seems to move at a time, you have likely called Segment method exit() while
holding the monitor lock.
As exit() includes a delay (see item I1), this leads to other threads being locked out of the
monitor for the duration of the delay.

 */

public class Monitor {
	Set<Segment> busySegments = new HashSet<Segment>();

	public synchronized void addAsBusy(List<Segment> s) {
		for (int i = 0; i < 3; i++) {
			busySegments.add(s.get(i));
		}
	}

	public synchronized void moveHead(Route route, List<Segment> train) throws InterruptedException {
		Segment head = route.next();

		System.out.println("before in MoveHead:  " + busySegments);
		while (busySegments.contains(head)) {
			wait();
		}

		busySegments.add(head);

		System.out.println("after in Movehead:  " + busySegments);

		train.add(0, head);

		head.enter();

	}

	public synchronized Segment moveTail(Route route, List<Segment> train) throws InterruptedException {
		Segment tail = train.remove(train.size() - 1);
		System.out.println("before in MoveTail:  " + busySegments);

		System.out.println("after in MoveTail:  " + busySegments);

		busySegments.remove(tail);
		notifyAll();
		return tail;

	}

	public void exitTail(Segment tail) {
		tail.exit();
	}

	public void move(Route route, List<Segment> train) throws InterruptedException {
		moveHead(route, train);
		exitTail(moveTail(route, train));
	}

}
