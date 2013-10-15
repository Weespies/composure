/**
 * 
 */
package uk.lug.serenity.npc.model.event;

import uk.lug.serenity.npc.model.skills.SkillData;

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
public class SkillChangeEvent {
	public static final int POINTS_CHANGED= 1;
	public static final int CHILD_ADDED=2;
	public static final int CHILD_POINTS_CHANGED=3;
	
	private int type =0;
	private SkillData skillData ;

	/**
	 * @return Returns the skillData.
	 */
	public SkillData getSkillData() {
		return skillData;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 * @param skillData
	 */
	public SkillChangeEvent(int type, SkillData skillData) {
		super();
		// TODO Auto-generated constructor stub
		this.type = type;
		this.skillData = skillData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj==null || !(obj instanceof SkillChangeEvent) ) {
			return false;
		}
		SkillChangeEvent event = (SkillChangeEvent)obj;
		if ( event.getType()!=getType() ) {
			return false;
		}
		if ( !event.getSkillData().equals( getSkillData() ) ) {
			return false;
		}
		return true;
	}
	
}
