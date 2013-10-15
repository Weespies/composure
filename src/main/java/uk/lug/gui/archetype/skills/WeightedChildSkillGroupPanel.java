/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.apache.commons.lang.StringUtils;

import uk.lug.gui.ColorUtils;
import uk.lug.serenity.npc.random.archetype.skills.ChildSkillGroup;
import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkillGroup;

/**
 * @author Luggy
 *
 */
public class WeightedChildSkillGroupPanel extends ChildSkillGroupPanel {
	private static final Color TOP_TITLE = new Color( 0x75b357 );
	private static final Color BOTTOM_TITLE = ColorUtils.darken( TOP_TITLE, 0.5f);
	
	
	/**
	 * @param group
	 */
	public WeightedChildSkillGroupPanel(ChildSkillGroup group) {
		super(group);
	}

	/* (non-Javadoc)
	 * @see lug.gui.archetype.skills.ChildSkillGroupPanel#getGroupTitle()
	 */
	@Override
	protected String getGroupTitle() {
		if ( StringUtils.isEmpty(dataModel.getName()) ) {
			return "Weighted";
		}
		return dataModel.getName()+" - Weighted";
	}

	/* (non-Javadoc)
	 * @see lug.gui.archetype.skills.ChildSkillGroupPanel#titleTopColor()
	 */
	@Override
	protected Color titleTopColor() {
		return TOP_TITLE;
	}
	
	/* (non-Javadoc)
	 * @see lug.gui.archetype.skills.ChildSkillGroupPanel#titleBottomColor()
	 */
	@Override
	protected Color titleBottomColor() {
		return BOTTOM_TITLE;
	}
}
