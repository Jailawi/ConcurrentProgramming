package train.simulation;

import java.util.LinkedList;
import java.util.List;

import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

	public static void main(String[] args) {
		List<Segment> train = new LinkedList<>();

		TrainView view = new TrainView();

		Route route = view.loadRoute();

		for (int i = 0; i < 3; i++) {
			train.add(route.next());
		}

		for (int i = 0; i < 3; i++) {
			train.get(i).enter();
		}

		while (true) {
			Segment head = route.next();
			train.add(0, head);
			head.enter();
			Segment tail = train.remove(train.size() - 1);
			tail.exit();
		}

	}

}
