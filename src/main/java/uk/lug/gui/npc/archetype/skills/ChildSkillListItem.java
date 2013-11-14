/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.serenity.npc.random.archetype.skills.ChildSkillGroup;
import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkill;

/**
 * @author Luggy
 * Representings child skills in the bias control.
 * Can wrap either a WeightedChildSkill or a ChildSkillGroup but not both.
 */
public class ChildSkillListItem {
	private WeightedChildSkill childSkill=null;
	private ChildSkillGroup skillGroup=null;
	
	
	/**
	 * Construct a weighting list item for a single child skill.
	 * @param childSkill
	 */
	public ChildSkillListItem(WeightedChildSkill childSkill) {
		super();
		this.childSkill = childSkill;
	}		
	
	/**
	 * Construct a weighting list item for a group of skills
	 * @param skillGroup
	 */
	public ChildSkillListItem(ChildSkillGroup skillGroup) {
		super();
		this.skillGroup = skillGroup;
	}


	/**
	 * @return the WeightedChildSkill for this listitem, which will be null if this item
	 * is a listitem for a group.
	 */
	public WeightedChildSkill getChildSkill() {
		return childSkill;
	}
	
	/**
	 * @return the ChildSkillGroup this listitem is wrapping.  If this listitem is wrapping
	 * an individual child skill weighting then this will return null.
	 */
	public ChildSkillGroup getSkillGroup() {
		return skillGroup;
	}
	
	/**
	 * Set the WeightedChildSkill for this list item.  Calls to getSkillGroup() after this
	 * will return null.
	 * @param childSkill
	 */
	public void setChildSkill(WeightedChildSkill childSkill) {
		this.childSkill = childSkill;
		skillGroup=null;
	}
	
	/**
	 * Set the ChildSkillGroup this item is to wrap.   Calls to getChildSkill() after
	 * this will return null;
	 * @param skillGroup
	 */
	public void setSkillGroup(ChildSkillGroup skillGroup) {
		this.skillGroup = skillGroup;
		childSkill=null;
	}
	
	/**
	 * @return true if this listitem is wrapping a ChildSkillGroup, false if it
	 * is wrapping a WeightedChildSkill .
	 */
	public boolean isSkillGroup() {
		return skillGroup!=null;
	}
	
	/**
	 * @return text appropriate for labelling this listitem in a GUI.
	 */
	public String getLabelText() {
		return ( isSkillGroup() ? skillGroup.getDescription() : childSkill.getSkillName() );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getLabelText();
	}
	
	/**
	 * @return Weighting for the group or child skill as appropriate
	 */
	public Weighting getWeighting() {
		return ( isSkillGroup() ? skillGroup.getWeighting() : childSkill.getWeighting() );
	}
}
