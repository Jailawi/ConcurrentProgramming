import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;

	private final JProgressBar mainProgressBar;
	private ExecutorService pool;

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();

		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
		pool = Executors.newFixedThreadPool(2);
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {

		/*
		 * Most Swing operations (such as creating view elements) must be performed in
		 * the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		CodeBreaker codeBreaker = new CodeBreaker();
		new Sniffer(codeBreaker).start();

	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {
		SwingUtilities.invokeLater(() -> {
			WorklistItem workItem = new WorklistItem(n, message);
			ProgressItem progressItem = new ProgressItem(n, message);
			JButton removeButton = new JButton("Remove");
			JButton cancelButton = new JButton("cancel");

			workList.add(workItem);

			Runnable decryptTask = () -> {
				try {
					Tracker tracker = new Tracker(progressItem, mainProgressBar);
					String plaintext = Factorizer.crack(message, n, tracker);
					SwingUtilities.invokeLater(() -> {
						progressItem.getTextArea().setText(plaintext);
						progressItem.add(removeButton);
						progressItem.remove(cancelButton);
					});

				} catch (InterruptedException e1) {
					progressItem.getProgressBar().setValue(1000000);
				}

			};

			SwingUtilities.invokeLater(() -> {
				JButton breakButton = new JButton("Break");
				workItem.add(breakButton);

				breakButton.addActionListener(e -> {
					workList.remove(workItem);
					progressList.add(progressItem);
					mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);

					removeButton.addActionListener(c -> {
						progressList.remove(progressItem);
						mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
						mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
					});

					Future future = pool.submit(decryptTask);

					cancelButton.addActionListener(a -> {
						SwingUtilities.invokeLater(() -> {
							int soFar = progressItem.getProgressBar().getValue();
							future.cancel(true);
							progressItem.remove(cancelButton);
							progressItem.add(removeButton);
							progressItem.getTextArea().setText("[CANCELLED]");
							mainProgressBar.setMaximum(mainProgressBar.getMaximum()
									- (progressItem.getProgressBar().getMaximum() - soFar));

							progressItem.getProgressBar().setValue(1000000);
						});

					});

					progressItem.add(cancelButton);

				});

			});

			// System.out.println("message intercepted (N=" + n + ")...");
		});

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
