/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import uk.lug.gui.CachedImageLoader;
import uk.lug.serenity.npc.random.archetype.Archetype;

/**
 * @author Luggy
 *
 */
public class ArchetypesCellRenderer extends JLabel implements ListCellRenderer {
	private static final Icon TEXT_ICON = CachedImageLoader.getCachedIcon("images/text.png");
	
	/**
	 * 
	 */
	public ArchetypesCellRenderer() {
		super();
		setOpaque(true);
		setIcon( TEXT_ICON );
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setText( ((Archetype)value).getName() );
		
		setForeground( isSelected ? list.getBackground() :list.getForeground() );
		setBackground( isSelected ? list.getForeground() : list.getBackground() ) ;
		return this;
	}

}
