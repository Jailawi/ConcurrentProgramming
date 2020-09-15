package train.model;

import java.util.List;

import train.view.TrainView;

public class TrainCreator extends Thread {
	List<Segment> train;
	Monitor m;
	TrainView view;
	
	public TrainCreator(List<Segment> train, Monitor m, TrainView view) {
		this.train= train;
		this.m=m;
		this.view=view;
	}
	
	public void createTrain(List<Segment> train, Route route) {

		for (int i = 0; i < 3; i++) {
			train.add(route.next());
		}

		for (int i = 0; i < 3; i++) {
			train.get(i).enter();
		}
	}
	
	public void run() {
		try {
			Route route = view.loadRoute();
			createTrain(train, route);
			while (true) {
				m.move(route, train);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
