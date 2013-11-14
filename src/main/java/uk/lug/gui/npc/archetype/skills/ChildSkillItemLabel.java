/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import uk.lug.gui.util.CachedImageLoader;

/**
 * @author Luggy
 *
 */
public class ChildSkillItemLabel extends JLabel implements ListCellRenderer {
	private static final Icon INDIVIDUAL_SKILL_ICON = CachedImageLoader.getCachedIcon("images/text.png");
	private static final Icon GROUPED_SKILLS_ICON = CachedImageLoader.getCachedIcon("images/package.png");

	/**
	 * @param item
	 */
	public ChildSkillItemLabel() {
		super();
		setOpaque(true);
	}
	

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		ChildSkillListItem item = (ChildSkillListItem)value;
		setBackground( isSelected ? list.getSelectionBackground() : list.getBackground() );
		setForeground( isSelected ? list.getSelectionForeground() : list.getSelectionForeground() );
		setIcon( item.isSkillGroup() ? GROUPED_SKILLS_ICON : INDIVIDUAL_SKILL_ICON );
		setText( item.getLabelText()  );
		return this;
	}

}


