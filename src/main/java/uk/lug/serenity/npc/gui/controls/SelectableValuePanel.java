/**
 * 
 */
package uk.lug.serenity.npc.gui.controls;

import java.awt.Color;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class SelectableValuePanel extends ValuePanel {
	private static final long serialVersionUID = 1L;
	private Color selectedBackground= Color.decode("#e2e198");
	private Color selectedForeground= Color.BLUE;
	private Color unselectedBackground;
	private Color unselectedForeground;
	private boolean selected = false;
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		setColors();
	}

	/**
	 * @param text
	 * @param value
	 * @param maxValue
	 * @param minValue
	 * @param valueStep
	 */
	public SelectableValuePanel(String text, int value, int maxValue, int minValue, int valueStep) {
		super(text, value, maxValue, minValue, valueStep);
		unselectedBackground = super.getBackground();
		unselectedForeground = super.getForeground();
	}
	
	/**
	 * Apply colors as per current selection.
	 *
	 */
	private void setColors() {
		setBackground( selected ? selectedBackground : unselectedBackground );
		setForeground( selected ? selectedForeground : unselectedForeground );
		getTextLabel().setBackground( selected ? selectedBackground : unselectedBackground );
		getTextLabel().setForeground( selected ? selectedForeground : unselectedForeground );
		repaint();
	}

	/**
	 * @return the selectedBackground
	 */
	public Color getSelectedBackground() {
		return selectedBackground;
	}

	/**
	 * @param selectedBackground the selectedBackground to set
	 */
	public void setSelectedBackground(Color selectedBackground) {
		this.selectedBackground = selectedBackground;
		if ( selected ) {
			setColors();
		}
	}

	/**
	 * @return the selectedForeground
	 */
	public Color getSelectedForeground() {
		return selectedForeground;
	}

	/**
	 * @param selectedForeground the selectedForeground to set
	 */
	public void setSelectedForeground(Color selectedForeground) {
		this.selectedForeground = selectedForeground;
		if ( selected ) {
			setColors();
		}
	}

	/**
	 * @return the unselectedBackground
	 */
	public Color getUnselectedBackground() {
		return unselectedBackground;
	}

	/**
	 * @param unselectedBackground the unselectedBackground to set
	 */
	public void setUnselectedBackground(Color unselectedBackground) {
		this.unselectedBackground = unselectedBackground;
		if ( !selected ) {
			setColors();
		}
	}

	/**
	 * @return the unselectedForeground
	 */
	public Color getUnselectedForeground() {
		return unselectedForeground;
	}

	/**
	 * @param unselectedForeground the unselectedForeground to set
	 */
	public void setUnselectedForeground(Color unselectedForeground) {
		this.unselectedForeground = unselectedForeground;
		if ( !selected ) {
			setColors();
		}
	}
}