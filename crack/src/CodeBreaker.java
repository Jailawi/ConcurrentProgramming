import java.awt.LayoutManager;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;

	private final JProgressBar mainProgressBar;

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();

		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {

		/*
		 * Most Swing operations (such as creating view elements) must be performed in
		 * the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		SwingUtilities.invokeLater(() -> {
			CodeBreaker codeBreaker = new CodeBreaker();
			new Sniffer(codeBreaker).start();

		});
	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {
		WorklistItem workItem = new WorklistItem(n, message);
		ProgressItem progressItem = new ProgressItem(n, message);

		workList.add(workItem);

		Runnable decryptTask = () -> {
			try {
				
				Tracker tracker = new Tracker(progressItem, mainProgressBar);
				String plaintext = Factorizer.crack(message, n, tracker);
				progressItem.getTextArea().setText(plaintext);
				SwingUtilities.invokeLater(()->{
			
				
				});
				

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		};
		
		

		ExecutorService pool = Executors.newFixedThreadPool(2);

		SwingUtilities.invokeLater(()->{
				JButton breakButton = new JButton("Break");
		workItem.add(breakButton);
		breakButton.addActionListener(e -> {
			workList.remove(workItem);
			progressList.add(progressItem);
			mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
			
			JButton removeButton = new JButton("Remove");
			progressItem.add(removeButton);
			
			removeButton.addActionListener(c -> {
				progressList.remove(progressItem);
				mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
				mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
			});
		
			
			Future future = pool.submit(decryptTask);
		   
			
			
			JButton cancelButton = new JButton("cancel");
			progressItem.add(cancelButton);
			cancelButton.addActionListener(a -> {
				if(future.cancel(true)){
					progressItem.getTextArea().setText("[CANCELLED]");
					progressItem.getProgressBar().setValue(1000000);
					mainProgressBar.setValue(mainProgressBar.getValue() + 1000000);

					progressItem.remove(cancelButton);
				}
			});
			
			
		
			
		});
			
			
		});
		
		
		

		// System.out.println("message intercepted (N=" + n + ")...");
	}
}

class Tracker implements ProgressTracker {
	private int totalProgress = 0;
	private ProgressItem progressItem;
	private JProgressBar mainProgressBar;

	public Tracker(ProgressItem progressItem, JProgressBar mainProgressBar) {
		this.progressItem = progressItem;
		this.mainProgressBar = mainProgressBar;
	}

	@Override
	public void onProgress(int ppmDelta) {
		totalProgress += ppmDelta;
		SwingUtilities.invokeLater(() -> mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta));
		SwingUtilities.invokeLater(() -> progressItem.getProgressBar().setValue(totalProgress));
	}



}
