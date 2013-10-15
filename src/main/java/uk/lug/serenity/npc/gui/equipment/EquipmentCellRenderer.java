package uk.lug.serenity.npc.gui.equipment;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import uk.lug.serenity.npc.gui.CreditFormat;
import uk.lug.serenity.npc.model.equipment.Equipment;

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
public class EquipmentCellRenderer extends JPanel implements ListCellRenderer {

	private JLabel detailsLabel;
	private JLabel iconLabel; 
	private static final long serialVersionUID = 1L;

	public EquipmentCellRenderer() {
		detailsLabel = new JLabel();
		iconLabel = new JLabel();
		
		detailsLabel.setOpaque(true);
		detailsLabel.setHorizontalTextPosition( SwingConstants.RIGHT );
		iconLabel.setOpaque(true);
		iconLabel.setHorizontalTextPosition( SwingConstants.RIGHT );
		setLayout( new BorderLayout() );
		add( detailsLabel, BorderLayout.CENTER );
		add( iconLabel, BorderLayout.WEST );
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Equipment eq = (Equipment)value;
		detailsLabel.setText( getTitle(eq) );
		detailsLabel.setForeground( isSelected ? list.getBackground() : list.getForeground() );
		detailsLabel.setBackground( isSelected ? list.getForeground() : list.getBackground() );
		detailsLabel.setIcon ( eq.getType().getIcon() );
		iconLabel.setIcon( eq.getAvailability().getIcon() );
		return this;
	}
	
	/**
	 * @param eq Equipment
	 * @return renderer text for this component.
	 */
	private String getTitle( Equipment eq ) {
		StringBuilder sb = new StringBuilder(128);
		sb.append( eq.getName() );
		if ( eq.getCost()>0 ) {
			sb.append(" " );
			sb.append( CreditFormat.get().format( eq.getCost() ) );
		}
		return sb.toString();
	}

}
