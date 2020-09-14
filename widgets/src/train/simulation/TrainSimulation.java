package train.simulation;

import java.util.LinkedList;
import java.util.List;

import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

	public void createTrain(List<Segment> train, Route route) {

		for (int i = 0; i < 3; i++) {
			train.add(route.next());
		}

		for (int i = 0; i < 3; i++) {
			train.get(i).enter();
		}
	}

	public void moveTrain(Route route, List<Segment> train) {
		Segment head1 = route.next();
		train.add(0, head1);
		head1.enter();
		Segment tail = train.remove(train.size() - 1);
		tail.exit();
	}

	public static void main(String[] args) {
		TrainSimulation ts = new TrainSimulation();
		
		TrainView view = new TrainView();

		List<Segment> train1 = new LinkedList<>();
		List<Segment> train2 = new LinkedList<>();
		List<Segment> train3 = new LinkedList<>();

		Route route1 = view.loadRoute();
		Route route2 = view.loadRoute();
		Route route3 = view.loadRoute();

		ts.createTrain(train1, route1);
		ts.createTrain(train2, route2);
		ts.createTrain(train3, route3);

		while (true) {
			ts.moveTrain(route1, train1);
			ts.moveTrain(route2, train2);
			ts.moveTrain(route3, train3);

		}

	}

}
