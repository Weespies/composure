/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Luggy
 * Extended button with no border, background and margins set to 0,0,0,0.
 */
public class EmptyButton extends JButton {
	
	private void setEmpty() {
		setMargin( new Insets(0,0,0,0) );
		setOpaque(false);
		setContentAreaFilled(false);
		setFocusable(false);
		setBorderPainted(false);
	}
	
	/**
	 * 
	 */
	public EmptyButton() {
		super();
		setEmpty();
	}

	/**
	 * @param a
	 */
	public EmptyButton(Action a) {
		super(a);
		setEmpty();
	}

	/**
	 * @param icon
	 */
	public EmptyButton(Icon icon) {
		super(icon);
		setEmpty();
	}

	/**
	 * @param text
	 * @param icon
	 */
	public EmptyButton(String text, Icon icon) {
		super(text, icon);
		setEmpty();
	}

	/**
	 * @param text
	 */
	public EmptyButton(String text) {
		super(text);
		setEmpty();
	}
}
