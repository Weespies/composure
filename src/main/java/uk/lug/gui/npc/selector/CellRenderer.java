/**
 * 
 */
package uk.lug.gui.npc.selector;

import java.awt.Component;

import javax.swing.JComponent;

/**
 * Provides a generified way of specifying types of renderers for types of of component.
 * @author Luggy
 *
 */
public interface CellRenderer<TComponent extends JComponent, TValue extends Object> {

	/**
	 * Renderer a component for a given value on a given component.
	 * @param component component where the rendering is to go.
	 * @param value value that is being rendered.
	 * @param index0 first index within the component (if required).
	 * @param index1 second index within the component (if required).
	 * @param isSelected true if the cell being rendered is currently selected.
	 * @param cellHasFocus true if the cell being rendered is currently focused.
	 * @return the final rendered component.
	 */
	public Component getRenderedCellComponentCellRendererComponent(TComponent component, TValue value,
			int index0,int index1, boolean isSelected, boolean cellHasFocus) ;
}
