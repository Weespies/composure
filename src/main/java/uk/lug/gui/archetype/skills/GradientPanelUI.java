/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/**
 * @author Luggy
 *
 */
public class GradientPanelUI extends PanelUI {
	private Color topColor;
	private Color bottomColor;
	private GradientPaint gradient;
	
	/**
	 * @return the topColor
	 */
	public Color getTopColor() {
		return topColor;
	}

	/**
	 * @param topColor the topColor to set
	 */
	public void setTopColor(Color topColor) {
		this.topColor = topColor;
	}

	/**
	 * @return the bottomColor
	 */
	public Color getBottomColor() {
		return bottomColor;
	}

	/**
	 * @param bottomColor the bottomColor to set
	 */
	public void setBottomColor(Color bottomColor) {
		this.bottomColor = bottomColor;
	}

	/**
	 * @param topColor
	 * @param bottomColor
	 */
	public GradientPanelUI(Color topColor, Color bottomColor) {
		super();
		this.topColor = topColor;
		this.bottomColor = bottomColor;
		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics, javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		gradient = new GradientPaint( new Point(0,0), topColor, new Point(0,c.getHeight()), bottomColor );
		Graphics2D gtemp = (Graphics2D)g.create();
		gtemp.setPaint( gradient );
		gtemp.fillRect(0,0, c.getWidth(), c.getHeight() );
		gtemp.dispose();
	}
	
}
