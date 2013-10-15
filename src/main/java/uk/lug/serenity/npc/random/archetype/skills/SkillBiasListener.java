/**
 * 
 */
package uk.lug.serenity.npc.random.archetype.skills;

import java.util.List;

/**
 * @author Luggy
 *
 */
public interface SkillBiasListener {
	/**
	 * Fired when a WeightedChildSkill is added to the general skill bias. 
	 * It is not fired when a WeightedChildSkill is ungrouped.
	 * @param skill
	 */
	public void addedChildSkill(WeightedChildSkill skill);
	
	/**
	 * Fired when a WeightedChildSkill is removed from the general skill bias.
	 * It is not fired when a WeightedChildSkill is added to a group.
	 * @param skill WeightedChildSkill to be removed
	 */
	public void removedChildSkill(WeightedChildSkill skill);
	
	/**
	 * Fired when a new ChildSkillGroup is created.
	 * @param group new ChildSkillGroup after formation , when its member count should be >0 .
	 */
	public void groupFormed(ChildSkillGroup group);
	
	/**
	 * Fired when ChildSkillGroup is disbanded.
	 * @param group the group that was disbanded (will be empty of children).
	 * @param members list of WeightedChildSkills that used to be group members but are now
	 * members of the GeneralSkillBias .
	 */
	public void groupDisbanded(ChildSkillGroup group, List<WeightedChildSkill> members);
	
	/**
	 * Fired when a WeightedChildSkill is added to an existing ChildSkillGroup.  Is not
	 * fired when a ChildSkillGroup is first created.
	 * @param group
	 * @param skill
	 */
	public void addedToGroup(ChildSkillGroup group, WeightedChildSkill skill);
	
	/**
	 * Fired when a WeightedChildSkill is removed from a ChildSkillGroup.
	 * It is not fired when the group is disbanded.
	 * @param group ChildSkillGroup without removed WeightedChildSkill in it.
	 * @param skill WeightedChild skill that has been removed.
	 */
	public void removedFromGroup(ChildSkillGroup group, WeightedChildSkill skill);
}
