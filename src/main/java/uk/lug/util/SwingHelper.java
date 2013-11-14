package uk.lug.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author PaulLoveridge
 * 
 */
public class SwingHelper {
	public static final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);
	private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
	public static final String TEXT_FIELD_FOREGROUND = "TextField.foreground";
	private static UIDefaults ui = UIManager.getDefaults();

	/**
	 * Runs the given Runnable inside the Swing event dispatch thread. If the
	 * current thread is the Swing EDT then it just runs it, otherwise it calls
	 * SwingUtilities.invokeLater()
	 * 
	 * @param runnable
	 */
	public static void runInEventThread(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}

	/**
	 * Runs the given Runnable inside the Swing event dispatch thread and blocks
	 * until the runnable has completed. If the current thread is the Swing EDT
	 * then it just runs it, otherwise it calls SwingUtilities.invokeLater()
	 * 
	 * @param runnable
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static void runNowInEventThread(Runnable runnable) throws InterruptedException, InvocationTargetException {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeAndWait(runnable);
		}
	}

	/**
	 * Run the given Runnable outside of the Swing event dispatch thread. If
	 * called within the Swing EDT() then the runnable is executed within a new
	 * thread, otherwise it executes inside the current thread.
	 * 
	 * @param runnable
	 */
	public static void runOutsideEventThread(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			ThreadHandlerPool.run(runnable);
		} else {
			runnable.run();
		}
	}

	public static boolean isCapsLockOn() {
		try {
			return java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	public static void setNimbusLookAndFeel() {

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public static void boldLabel(JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.BOLD));
	}

	public static void italicLabel(JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.ITALIC));
	}

	public static void italicBoldLabel(JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.BOLD + Font.ITALIC));
	}

	/**
	 * Resize a dialog to be a percentage of the total screen size
	 * 
	 * @param owner
	 * @param dialog
	 * @param percentage
	 */
	public static void resizeAndCentreDialogOnScreen(JDialog dialog, double percentage) {
		Dimension screenSize = toolkit.getScreenSize();
		int width = (int) ((screenSize.width * percentage) / 100d);
		int height = (int) ((screenSize.height * percentage) / 100d);

		dialog.setSize(width, height);
		int x = (int) ((screenSize.width * 0.5f) - (width * 0.5f));
		int y = (int) ((screenSize.height * .05f) - (height * 0.5f));
		dialog.setLocation(x, y);
	}

	public static void centreDialogOnWindow(Window owner, JDialog dialog) {
		Point ownerPosition = owner.getLocationOnScreen();
		Dimension ownerSize = owner.getSize();
		Dimension dialogSize = dialog.getSize();
		int x = ownerPosition.x + (ownerSize.width / 2) - (dialogSize.width / 2);
		int y = ownerPosition.y + (ownerSize.height / 2) - (dialogSize.height / 2);
		dialog.setLocation(x, y);
	}

	public static void centreDialogOnWindow(Window owner, FileDialog dialog) {
		Point ownerPosition = owner.getLocationOnScreen();
		Dimension ownerSize = owner.getSize();
		Dimension dialogSize = dialog.getSize();
		int x = ownerPosition.x + (ownerSize.width / 2) - (dialogSize.width / 2);
		int y = ownerPosition.y + (ownerSize.height / 2) - (dialogSize.height / 2);
		dialog.setLocation(x, y);
	}

	public static void plainLabel(JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
	}

	public static Window getOwnerWindow(Component component) {
		if (component instanceof Window) {
			return (Window) component;
		}
		Container parent = component.getParent();
		while (!(parent instanceof Window)) {
			parent = parent.getParent();
			if (parent == null) {
				throw new IllegalArgumentException("Component has no root window.");
			}
		}
		return (Window) parent;
	}

	/**
	 * Return the top left corner coordinate for the window that will centre it
	 * on the ownerComponent;
	 * 
	 * @param ownerComponent
	 * @param aruddReportDialog
	 * @return
	 */
	public static Point centreOn(Component ownerComponent, Window window) {
		int x = ownerComponent.getLocationOnScreen().x + (ownerComponent.getWidth() / 2) - (window.getWidth() / 2);
		int y = ownerComponent.getLocationOnScreen().y + (ownerComponent.getHeight() / 2) - (window.getHeight() / 2);
		return new Point(x, y);
	}

	/**
	 * Iterate through all parents of the given component until one the class is
	 * found.
	 * 
	 * @param importReportsPanel
	 * @param class1
	 * @return
	 */
	public static Component findInParentHeirachy(Component component, Class lookingfor) {
		Component parent = component.getParent();
		while (parent != null) {
			if (lookingfor.isAssignableFrom(parent.getClass())) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	public static void errorLabel(JLabel label) {
		label.setForeground(new Color(192, 0, 0));
		italicBoldLabel(label);
	}

	public static void changeFont(JLabel label, float sizeMultipler) {
		float size = label.getFont().getSize() * sizeMultipler;
		label.setFont(label.getFont().deriveFont(size));
	}

	public static void changeFontSize(JLabel label, int newSize) {
		label.setFont(label.getFont().deriveFont((float) newSize));
	}

	public static void changeFontSize(JTextField textField, int newSize) {
		textField.setFont(textField.getFont().deriveFont((float) newSize));
	}

	public static Frame getOwnerFrame(Component component) {
		if (component instanceof Frame) {
			return (Frame) component;
		}
		Container parent = component.getParent();
		while (!(parent instanceof Frame)) {
			parent = parent.getParent();
			if (parent == null) {
				throw new IllegalArgumentException("Component has no root Frame.");
			}
		}
		return (Frame) parent;
	}

	public static void boldTextArea(JTextArea textArea) {
		textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
	}

	public static void changeFontSize(JTextArea textArea, int newSize) {
		textArea.setFont(textArea.getFont().deriveFont((float) newSize));
	}

	public static void changeFontSize(JSpinner spinner, int newSize) {
		spinner.setFont(spinner.getFont().deriveFont((float) newSize));
	}

	public static void setKeyAction_WhenInFocusedWindow(JComponent component, int keyCode, String actionIdString, Action action) {
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), actionIdString);
		component.getActionMap().put(actionIdString, action);
	}

	public static void setKeyAction_WhenFocused(JComponent component, int keyCode, String actionIdString, Action action) {
		component.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(keyCode, 0), actionIdString);
		component.getActionMap().put(actionIdString, action);
	}

	public static void setKeyAction_WhenAncestorOfFocusedComponent(JComponent component, int keyCode, String actionIdString, Action action) {
		component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyCode, 0), actionIdString);
		component.getActionMap().put(actionIdString, action);
	}

	public static void dialogFullScreen(JDialog dialog) {
		dialog.setBounds(new Rectangle(0, 0, toolkit.getScreenSize().width, toolkit.getScreenSize().height));
	}

	public static void boldTextField(JTextField textField) {
		textField.setFont(textField.getFont().deriveFont(Font.BOLD));
	}

	public static void plainTextField(JTextField textField) {
		textField.setFont(textField.getFont().deriveFont(Font.PLAIN));
	}

	public static void pause(int i) {
		long t = System.currentTimeMillis() + i;
		while (System.currentTimeMillis() < t) {
			Thread.yield();
		}
	}

	public static Color getSwingColor(String key) {
		return ui.getColor(key);
	}

}
