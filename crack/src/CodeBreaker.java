import java.awt.LayoutManager;
import java.math.BigInteger;

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
		workList.add(workItem);

		workItem.getButton().addActionListener(e -> {

			try {
				workList.remove(workItem);
				ProgressItem progressItem = new ProgressItem(n, message);
				progressList.add(progressItem);
				ProgressTracker tracker = new Tracker();
				String plaintext = Factorizer.crack(message, n, tracker);
				System.out.println("da message for da beople: " + plaintext);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// System.out.println("message intercepted (N=" + n + ")...");
	}
}

class Tracker implements ProgressTracker {
	private int totalProgress = 0;

	@Override
	public void onProgress(int ppmDelta) {
		totalProgress += ppmDelta;
		System.out.println("progress = " + totalProgress + "/1000000");
	}
}
