package uk.lug.gui.pending;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import uk.lug.util.SwingHelper;
import uk.lug.util.ThreadHandlerPool;

/**
 * Pending panel shows a "Please wait" UI while so remote or long duration
 * action occurs, then replaces that with the final UI.
 * 
 */
public abstract class PendingPanel extends JPanel {
	private PleaseWaitPanel waitPanel;
	private boolean pendingStarted = false;
	private boolean pendingCompleted = false;
	private String errorMessageText;

	public PendingPanel(String pleaseWaitText) {
		waitPanel = new PleaseWaitPanel();
		setLayout(new BorderLayout());
		waitPanel.setText(pleaseWaitText);
		add(waitPanel, BorderLayout.CENTER);
		waitPanel.start();
		ThreadHandlerPool.run(new Runnable() {

			public void run() {
				startBackgroundRun();

			}
		});
		Dimension d = new Dimension(350, 200);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
	}

	public void setPendingTile(String str) {
		waitPanel.setText(str);
	}

	protected void startBackgroundRun() {
		pendingStarted = true;
		final boolean pendingOk = performPending();
		waitPanel.stop();
		SwingHelper.runInEventThread(new Runnable() {
			
			public void run() {
				replacePendingUI(pendingOk);
			}
		});
		pendingCompleted = true;
	}
	
	public void startPending(final String message) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				waitPanel.setText(message);
				waitPanel.start();
				waitPanel.setVisible(true);
			}
		
		});
	}

	public void stopPending() {
		SwingHelper.runInEventThread(new Runnable() {
			
			public void run() {
				waitPanel.stop();
				waitPanel.setVisible(false);	
			}
		});
	}

	
	private void replacePendingUI(boolean pendingOpearationOK) {
		removeAll();
		if (pendingOpearationOK) {
			SwingHelper.runInEventThread(new Runnable() {

				public void run() {
					buildPostPendingUI();
				}
			});
		} else {
			buildErrorUI();
		}
		revalidate();
	}

	/**
	 * Called after performPending() has completed unsuccessfully and all
	 * components have been cleared. This method should build an error display
	 * UI. Unless overridden this just displays an error message in a label.
	 */
	protected void buildErrorUI() {
		setLayout(new BorderLayout());
		JPanel centrePanel = new JPanel(new GridLayout(2, 1));

		JLabel errorLabel = new JLabel("An error occurred performing that operation.");
		errorLabel.setForeground(Color.RED);
		SwingHelper.boldLabel(errorLabel);
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centrePanel.add(errorLabel);

		JLabel errorMessageLabel = new JLabel(errorMessageText == null ? "" : errorMessageText);
		errorMessageLabel.setForeground(Color.RED);
		errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		SwingHelper.boldLabel(errorMessageLabel);
		centrePanel.add(errorMessageLabel);

		add(centrePanel, BorderLayout.CENTER);
	}

	/**
	 * Perform the operation that is pending. This method should not return
	 * until all the require actions for the main UI to display have been
	 * completed.
	 * 
	 * @return true if the operation has succeeded or false if an error is to be
	 *         displayed.
	 */
	protected abstract boolean performPending();

	/**
	 * Called after performPending() has completed successfully and all
	 * components have been cleared. This method should build the post pending
	 * interface and is always called from inside the Swing event dispatch
	 * thread.
	 */
	protected abstract void buildPostPendingUI();

	public boolean isPendingStarted() {
		return pendingStarted;
	}

	public boolean isPendingCompleted() {
		return pendingCompleted;
	}

	public void setErrorMessage(String message) {
		this.errorMessageText = message;
	}

}
