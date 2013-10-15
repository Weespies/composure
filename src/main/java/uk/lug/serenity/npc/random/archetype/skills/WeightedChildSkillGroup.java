/**
 * 
 */
package uk.lug.serenity.npc.random.archetype.skills;

import java.util.Random;

import uk.lug.serenity.npc.model.skills.SkillSheet;

/**
 * @author Luggy
 * Child Skill Group where the individual skills can have bias
 * weightings in them.
 */
public class WeightedChildSkillGroup extends ChildSkillGroup {
	

	static final String WEIGHTING_GROUP = "weightingGroup";

	/**
	 * 
	 */
	public WeightedChildSkillGroup() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public WeightedChildSkillGroup(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.ChildSkillGroup#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Weighting Group";
	}

	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.skills.ChildSkillGroup#getXMLName()
	 */
	@Override
	protected String getXMLName() {
		return WEIGHTING_GROUP;
	}

	
	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.skills.ChildSkillGroup#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !( obj instanceof WeightedChildSkillGroup ) ) {
			return false;
		}
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see lug.serenity.npc.random.archetype.skills.ChildSkillGroup#getRandomMember(lug.serenity.npc.model.skills.SkillSheet, java.util.Random)
	 */
	@Override
	public String getRandomMember(SkillSheet sheet, Random random) {
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
