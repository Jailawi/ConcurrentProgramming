package train.simulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import train.model.Monitor;
import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

	public static class Train implements Runnable {
		private LinkedList<Segment> train = new LinkedList<>();
		private Route route;
		private Monitor m;

		public Train(Route route, Monitor m) {
			this.route = route;
			this.m = m;
		}

		public void createTrain() {

			for (int i = 0; i <= 2; i++) {
				var s = route.next();
				train.add(s);
				s.enter();
			}

		}

		@Override
		public void run() {
			this.createTrain();
			while (true) {
				try {
					m.move(route, train);
					;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) throws InterruptedException {
		TrainSimulation ts = new TrainSimulation();

		TrainView view = new TrainView();
		Monitor m = new Monitor();
		Route route;
		Train train;
		Thread TrainThread;

		for (var i = 0; i <= 19; i++) {
			route = view.loadRoute();
			train = new Train(route, m);
			TrainThread = new Thread(train, "Train " + (i + 1));
			TrainThread.start();
		}

	}

}
