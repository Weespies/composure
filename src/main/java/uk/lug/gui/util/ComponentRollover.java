/**
 * 
 */
package uk.lug.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Adds highlighting for a component so that its colors change on mouse enter or exit.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class ComponentRollover extends MouseAdapter {
	private Component component;
	private Color normalBackground;
	private Color normalForeground;
	private Color highlightBackground;
	private Color highlightForeground;
	
	
	
	/**
	 * @param component
	 * @param highlightBackground
	 * @param highlightForeground
	 */
	public ComponentRollover(Component component, Color highlightBackground, Color highlightForeground) {
		super();
		this.component = component;
		this.highlightBackground = highlightBackground;
		this.highlightForeground = highlightForeground;
		component.addMouseListener( this );
	}



	/**
	 * Creates a ComponentRollover taking the un-rollover colors from the component
	 * @param component component to received mouse over highlighting
	 * @param normalBackground background when not highlighted
	 * @param normalForeground foreground when not highlighted
	 * @param highlightBackground background when highlighted
	 * @param highlightForeground foreground when highlighted
	 */
	public ComponentRollover(Component component, Color normalBackground, Color normalForeground, Color highlightBackground, Color highlightForeground) {
		super();
		normalBackground = component.getBackground();
		normalForeground = component.getForeground();
		this.component = component;
		this.normalBackground = normalBackground;
		this.normalForeground = normalForeground;
		this.highlightBackground = highlightBackground;
		this.highlightForeground = highlightForeground;
		component.addMouseListener( this );
	}



	public ComponentRollover( Component target ) {
		normalBackground = target.getBackground();
		normalForeground = target.getForeground();
		highlightBackground = target.getForeground();
		highlightForeground = target.getBackground();
		component = target;
		component.addMouseListener( this );
	}



	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		e.getComponent().setBackground( highlightBackground );
		e.getComponent().setForeground( highlightForeground );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		e.getComponent().setBackground( normalBackground );
		e.getComponent().setForeground( normalForeground );
	}



	/**
	 * @return the component
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * @return the highlightBackground
	 */
	public Color getHighlightBackground() {
		return highlightBackground;
	}

	/**
	 * @param highlightBackground the highlightBackground to set
	 */
	public void setHighlightBackground(Color highlightBackground) {
		this.highlightBackground = highlightBackground;
	}

	/**
	 * @return the highlightForeground
	 */
	public Color getHighlightForeground() {
		return highlightForeground;
	}

	/**
	 * @param highlightForeground the highlightForeground to set
	 */
	public void setHighlightForeground(Color highlightForeground) {
		this.highlightForeground = highlightForeground;
	}

	/**
	 * @return the normalBackground
	 */
	public Color getNormalBackground() {
		return normalBackground;
	}

	/**
	 * @param normalBackground the normalBackground to set
	 */
	public void setNormalBackground(Color normalBackground) {
		this.normalBackground = normalBackground;
	}

	/**
	 * @return the normalForeground
	 */
	public Color getNormalForeground() {
		return normalForeground;
	}

	/**
	 * @param normalForeground the normalForeground to set
	 */
	public void setNormalForeground(Color normalForeground) {
		this.normalForeground = normalForeground;
	}
	
	/**
	 * Assign a default rollover to a component.
	 * @param c
	 */
	public static void enableRollover( Component c ) {
		ComponentRollover cr = new ComponentRollover( c );
	}

	
	/**
	 * Assign a default rollover to a component with custom colors
	 * @param c
	 */
	public static void customRollover( Component c , Color normalBG, Color normalFG, Color selectedBG, Color selectedFG ) {
		ComponentRollover cr = new ComponentRollover( c , normalBG, normalFG, selectedBG, selectedFG);
	}

	
	/**
	 * Assign a default rollover to a component.
	 * @param c
	 */
	public static void customHighlight( Component c, Color selectedBG, Color selectedFG  ) {
		ComponentRollover cr = new ComponentRollover( c ,selectedBG, selectedFG);
	}

}
