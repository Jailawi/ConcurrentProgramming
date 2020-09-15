package train.simulation;

import java.util.LinkedList;
import java.util.List;

import train.model.Monitor;
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

	public static void main(String[] args) throws InterruptedException {
		TrainSimulation ts = new TrainSimulation();
		Monitor m = new Monitor();

		TrainView view = new TrainView();

		List<Segment> train1 = new LinkedList<>();
		List<Segment> train2 = new LinkedList<>();
		List<Segment> train3 = new LinkedList<>();

		// Route route3 = view.loadRoute();

		// ts.createTrain(train2, route2);
		// ts.createTrain(train3, route3);

		Thread trainThread1 = new Thread(() -> {
			try {
				Route route1 = view.loadRoute();
				ts.createTrain(train1, route1);
				while (true) {
					m.move(route1, train1);
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		Thread trainThread2 = new Thread(() -> {
			try {
				Route route2 = view.loadRoute();
				ts.createTrain(train2, route2);
				while (true) {
					m.move(route2, train2);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		Thread trainThread3 = new Thread(() -> {
			try {
				Route route3 = view.loadRoute();
				ts.createTrain(train3, route3);
				while (true) {
					m.move(route3, train3);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		while (true) {
			trainThread1.start();
			trainThread2.start();
			trainThread3.start();
		}

	}

}
