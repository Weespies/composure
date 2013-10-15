/**
 * 
 */
package uk.lug.serenity.npc.gui.controls;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.StringUtils;

import uk.lug.gui.CachedImageLoader;

/**
 * @author Luggy
 */
public class JButtonRow extends JPanel {
	private List<JLabel> buttonLabels;

	private Icon SELECTED_ICON = CachedImageLoader.getCachedIcon("images/button_blue.png");

	private Icon UNSELECTED_ICON = CachedImageLoader.getCachedIcon("images/button_off.png");

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	protected int value = 0;

	private int minimum = 1;

	private int maximum = 10;

	/**
	 * @param buttonCount
	 */
	public JButtonRow(int minimum, int maximum) {
		super();
		this.maximum = maximum;
		this.minimum = minimum;
		build();
	}

	/**
	 * Build the user interface
	 */
	private void build() {

		buttonLabels = new ArrayList<JLabel>(getButtonCount());
		setLayout(new GridBagLayout());
		float weightx = (1.0f / getButtonCount());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, weightx, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
				0, 0);

		for (int x = 0; x < getButtonCount(); x++) {
			JLabel label = new JLabel(UNSELECTED_ICON);
			label.setName(getButtonComponentNameForValue(x));
			gbc.gridx = x;
			add(label, gbc);
			buttonLabels.add(label);
		}

		// Setup GUI responses
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				final Point p = e.getPoint();
				new Thread(new ResponseRunner(p)).start();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				final Point p = e.getPoint();
				new Thread(new ResponseRunner(p)).start();
			}
		};

		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	/**
	 * Responds to a mouse press at a given point.
	 * 
	 * @param pressPoint
	 */
	private void respondToMousePress(Point pressPoint) {
		int v = getValueForPoint(pressPoint);
		if (getComponentAt(pressPoint) == null) {
			getOtherComponent(pressPoint);
			return;
		}
		value = v;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLabelIcons();
			}
		});

		fireAllChangeListeners();

	}

	/**
	 * FInd the component the mouse is over. This is a blatent hack and wouldn't
	 * be at all necessary if swing would just tell me the mouse button state.
	 * 
	 * @param pressPoint
	 *            the point relative to this component of the mouse cursor.
	 * @return true if a component was found that was a JButtonRow .
	 */
	private boolean getOtherComponent(Point pressPoint) {
		Container parent = this.getParent();
		Point p = new Point(pressPoint);
		p.x = p.x + getX();
		p.y = p.y + getY();
		Component other = parent.getComponentAt(p);
		if (other != null && (other instanceof JButtonRow)) {
			p.x = p.x - other.getX();
			p.y = p.y - other.getY();
			((JButtonRow) other).respondToMousePress(p);
			return true;
		}
		return false;

	}

	/**
	 * Fire all listeners to tell them of a change.
	 */
	private void fireAllChangeListeners() {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : listeners) {
			listener.stateChanged(event);
		}
	}

	/**
	 * Sets the selected/unselected icon of each jlabel depending on the
	 * currently selected value.
	 */
	protected void setLabelIcons() {
		if (value == 0 && minimum == 0) {
			for (int x = 0; x < buttonLabels.size(); x++) {
				JLabel buttonLabel = buttonLabels.get(x);
				buttonLabel.setIcon(UNSELECTED_ICON);
				buttonLabel.repaint();
			}
		} else {
			for (int x = 0; x < buttonLabels.size(); x++) {
				JLabel buttonLabel = buttonLabels.get(x);
				buttonLabel.setName(getButtonComponentNameForValue(x));

				int v = minimum + x;
				buttonLabel.setIcon(v <= value ? SELECTED_ICON : UNSELECTED_ICON);
				buttonLabel.repaint();
			}
		}
	}

	/**
	 * For a given point in this component return the appropriate selection
	 * value.
	 * 
	 * @param p
	 * @return
	 */
	private int getValueForPoint(Point p) {
		Component comp = getComponentAt(p);
		if (comp == null) {
			return minimum;
		}
		int div = (getWidth() / getButtonCount());
		int ret = minimum + (p.x / div);
		return ret;
	}

	/**
	 * Runnable just for responding to mouse events
	 */
	class ResponseRunner implements Runnable {
		private Point responsePoint;

		/**
		 * @param responsePoint
		 */
		private ResponseRunner(Point responsePoint) {
			super();
			this.responsePoint = responsePoint;
		}

		public void run() {
			respondToMousePress(responsePoint);
		}
	}

	/**
	 * Set the icon that represents each value when it is in the selected state.
	 * 
	 * @param selected_icon
	 */
	public void setSelectedIcon(Icon selected_icon) {
		SELECTED_ICON = selected_icon;
	}

	/**
	 * Set the icon that represents each value when it is in the unselected
	 * state.
	 * 
	 * @param selected_icon
	 */
	public void setUnselectedIcon(Icon unselected_icon) {
		UNSELECTED_ICON = unselected_icon;
	}

	/**
	 * Add a listener to be notified when the selected value changes.
	 * 
	 * @param changeListener
	 */
	public void addChangeListener(ChangeListener changeListener) {
		listeners.add(changeListener);
	}

	/**
	 * Remove a listener so that it no longer gets notified when the value
	 * changes.
	 * 
	 * @param changeListener
	 */
	public void removeChangeListener(ChangeListener changeListener) {
		listeners.remove(changeListener);
	}

	/**
	 * @return currently selected value (between 0 and buttoncount-1 ).
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Change the selected value. This triggers both firing of the change
	 * listeners and a GUI repaint for labels who's icons have changed.
	 * 
	 * @param newValue
	 */
	public void setValue(int newValue) {
		if (newValue < minimum) {
			throw new IllegalArgumentException("New value cannot be less than 0.");
		}
		if (newValue > maximum) {
			throw new IllegalArgumentException("New value exceeds selectable maximum of _" + (getButtonCount() - 1) + ".");
		}
		this.value = newValue;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLabelIcons();
			}
		});

		fireAllChangeListeners();
	}

	/**
	 * @return the number of selectable buttons and this the selectable maximum
	 *         value +1.
	 */
	public int getButtonCount() {
		if (minimum == 0) {
			return (maximum - minimum);
		} else {
			return (maximum - minimum) + 1;
		}
	}

	/**
	 * Redraw the user interface. Usually done because the number of buttons has
	 * changed.
	 */
	private void redrawButtons() {
		for (JLabel label : buttonLabels) {
			remove(label);
		}
		buttonLabels.clear();

		float weightx = (1.0f / getButtonCount());
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, weightx, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
				0, 0);

		for (int x = 0; x < getButtonCount(); x++) {
			JLabel label = new JLabel(UNSELECTED_ICON);
			label.setIcon(x < value ? SELECTED_ICON : UNSELECTED_ICON);
			gbc.gridx = x;
			add(label, gbc);
			buttonLabels.add(label);
		}

		revalidate();
		repaint();
	}

	/**
	 * Get the name of button at value v.
	 * 
	 * @param value
	 */
	private String getButtonComponentNameForValue(int value) {
		StringBuilder sb = new StringBuilder(128);
		if (!StringUtils.isEmpty(this.getName())) {
			sb.append(this.getName());
		} else {
			sb.append("ButtonRow");
		}
		sb.append("_button");
		sb.append(value);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		super.setName(name);
		for (int i = 0; i < buttonLabels.size(); i++) {
			buttonLabels.get(i).setName(getButtonComponentNameForValue(i + 1));
		}
	}

	/**
	 * @return the icon used for selected buttons
	 */
	Icon getSelectedIcon() {
		return SELECTED_ICON;
	}

	/**
	 * @return the icon used for unselected buttons.
	 */
	Icon getUnselectedIcon() {
		return UNSELECTED_ICON;
	}

}
