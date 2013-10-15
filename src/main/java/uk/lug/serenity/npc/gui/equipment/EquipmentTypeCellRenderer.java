/**
 * 
 */
package uk.lug.serenity.npc.gui.equipment;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import uk.lug.serenity.npc.model.equipment.EquipmentType;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class EquipmentTypeCellRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	
	
	public EquipmentTypeCellRenderer() {
		super();
		setOpaque( true );
	}



	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		EquipmentType type = (EquipmentType)value;
		setText( type.getName() );
		setIcon( type.getIcon() );
		setBackground( isSelected ? list.getForeground() : list.getBackground() ) ;
		setForeground( isSelected ? list.getBackground() : list.getForeground() ) ;
		return this;
	}
	
}
