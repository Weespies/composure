/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.datatransfer.DataFlavor;

/**
 * @author Luggy
 *
 */
public class WeightedChildSkillFlavor extends DataFlavor {
	public static final String READABLE_NAME = "WeightedChildSkill";
	private static WeightedChildSkillFlavor instance=null;
	
	/**
	 * 
	 */
	public WeightedChildSkillFlavor() {
		super( WeightedChildSkillFlavor.class, READABLE_NAME );
	}

	static WeightedChildSkillFlavor get() {
		if ( instance==null ) {
			instance = new WeightedChildSkillFlavor();
		}
		return instance;
	}
}
