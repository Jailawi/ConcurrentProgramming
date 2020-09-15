package train.simulation;

import java.util.LinkedList;
import java.util.List;

import train.model.Monitor;
import train.model.Route;
import train.model.Segment;
import train.model.TrainCreator;
import train.view.TrainView;

public class TrainSimulation {
	
	
	public static void main(String[] args) throws InterruptedException {
		TrainView view = new TrainView();
		Monitor monitor =new Monitor();



		List<Segment> train1 = new LinkedList<>();
		List<Segment> train2 = new LinkedList<>();
		List<Segment> train3 = new LinkedList<>();
		List<Segment> train4 = new LinkedList<>();
		List<Segment> train5 = new LinkedList<>();
		List<Segment> train6 = new LinkedList<>();
		List<Segment> train7 = new LinkedList<>();
		List<Segment> train8 = new LinkedList<>();
		List<Segment> train9 = new LinkedList<>();
		List<Segment> train10 = new LinkedList<>();
		List<Segment> train11 = new LinkedList<>();
		List<Segment> train12 = new LinkedList<>();
		List<Segment> train13 = new LinkedList<>();
		List<Segment> train14 = new LinkedList<>();
		List<Segment> train15 = new LinkedList<>();
		List<Segment> train16 = new LinkedList<>();
		List<Segment> train17 = new LinkedList<>();
		List<Segment> train18 = new LinkedList<>();
		List<Segment> train19 = new LinkedList<>();
		List<Segment> train20 = new LinkedList<>();

		
		TrainCreator tc1= new TrainCreator(train1,monitor, view);
		TrainCreator tc2= new TrainCreator(train2,monitor, view);
		TrainCreator tc3= new TrainCreator(train3,monitor, view);
		TrainCreator tc4= new TrainCreator(train4,monitor, view);
		TrainCreator tc5= new TrainCreator(train5,monitor, view);
		TrainCreator tc6= new TrainCreator(train6,monitor, view);
		TrainCreator tc7= new TrainCreator(train7,monitor, view);
		TrainCreator tc8= new TrainCreator(train8,monitor, view);
		TrainCreator tc9= new TrainCreator(train9,monitor, view);
		TrainCreator tc10= new TrainCreator(train10,monitor, view);
		TrainCreator tc11= new TrainCreator(train11,monitor, view);
		TrainCreator tc12= new TrainCreator(train12,monitor, view);
		TrainCreator tc13= new TrainCreator(train13,monitor, view);
		TrainCreator tc14= new TrainCreator(train14,monitor, view);
		TrainCreator tc15= new TrainCreator(train15,monitor, view);
		TrainCreator tc16= new TrainCreator(train16,monitor, view);
		TrainCreator tc17= new TrainCreator(train17,monitor, view);
		TrainCreator tc18= new TrainCreator(train18,monitor, view);
		TrainCreator tc19= new TrainCreator(train19,monitor, view);
		TrainCreator tc20= new TrainCreator(train20,monitor, view);
		


		



			tc1.start();
			tc2.start();
			tc3.start();
			tc4.start();
			tc5.start();
			tc6.start();
			tc7.start();
			tc8.start();
			tc9.start();
			tc10.start();
			tc11.start();
			tc12.start();
			tc13.start();
			tc14.start();
			tc15.start();
			tc16.start();
			tc17.start();
			tc18.start();
			tc19.start();
			tc20.start();
			
	}

}
