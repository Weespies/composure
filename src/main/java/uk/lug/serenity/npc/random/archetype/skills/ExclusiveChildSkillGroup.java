/**
 * 
 */
package uk.lug.serenity.npc.random.archetype.skills;

import java.util.Random;

import uk.lug.serenity.npc.model.skills.SkillSheet;

/**
 * @author Luggy
 * A child skill group where only one skill is ever
 * chosen out of all options.
 */
public class ExclusiveChildSkillGroup extends ChildSkillGroup {
	static final String EXCLUSIVE_GROUP = "exclusiveGroup";
	private String chosenSkillName;
	
	/**
	 * 
	 */
	public ExclusiveChildSkillGroup() {
		super();
	}



	/**
	 * @param name
	 */
	public ExclusiveChildSkillGroup(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.ChildSkillGroup#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Exclusive Group";
	}



	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.skills.ChildSkillGroup#getXMLName()
	 */
	@Override
	protected String getXMLName() {
		return EXCLUSIVE_GROUP;
	}
	
	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.skills.ChildSkillGroup#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !( obj instanceof ExclusiveChildSkillGroup ) ) {
			return false;
		}
		return super.equals(obj);
	}



	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.skills.ChildSkillGroup#getRandomMember(lug.serenity.npc.model.skills.SkillSheet, java.util.Random)
	 */
	@Override
	public String getRandomMember(SkillSheet sheet, Random random) {
		for ( WeightedChildSkill wcs: getChildSkills() ) {
			if ( sheet.getPointsIn(wcs.getSkillName())>6 ){
				return wcs.getSkillName();
			}
		}
		int n = random.nextInt( getChildSkills().size() );
		for ( WeightedChildSkill wcs : getChildSkills() ) {
			n -= wcs.getWeighting().getValue();
			if ( n<0 ) {
				return wcs.getSkillName();
			}
		}
		return null;
	}
}
