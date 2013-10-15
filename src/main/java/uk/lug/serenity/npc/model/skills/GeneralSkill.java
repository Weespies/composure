/**
 * 
 */
package uk.lug.serenity.npc.model.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import uk.lug.util.RandomFactory;

/**
 * @author Luggy
 *
 */
public enum GeneralSkill {
	ANIMAL_HANDLING(0,"Animal Handling","ANIMHAND"),
	ARTISTRY(1,"Artistry","ARTIST"),
	ATHLETICS(2,"Athletics","ATHLET"),
	COVERT(3,"Covert","COVRT"),
	CRAFT(4,"Craft","CRAFT"),
	DISCIPLINE(5,"Discipline","DISCIP"),
	GUNS(6,"Guns","GUNS"),
	HEAVY_WEAPON(7,"Heavy Weapons","HVYWEAP"),
	INFLUENCE(8,"Influence","INFLU"),
	KNOWLEDGE(9,"Knowledge","KNOW"),
	LINGUIST(10,"Linguist","LING"),
	MECHANICAL_ENGINEERING(11,"Mechanical Engineering","MECHENG"),
	MEDICAL(12,"Medical Expertise","MED"),
	MELEE_WEAPONS(13,"Melee Weapon Combat","MELEE"),
	PERCEPTION(14,"Perception","PERC"),
	PERFORMANCE(15,"Performance","PERF"),
	PILOT(16,"Pilot","PILOT"),
	PLANETARY_VEHICLE(17,"Planetary Vehicle","PLANVEH"),
	RANGED_WEAPONS(18,"Ranged Weapons","RANGWEAP"),
	SCIENTIFIC_EXPERT(19,"Scientific Expertise","SCIEXP"),
	SURVIVAL(20,"Survival","SURV"),
	TECHNICAL_ENGINEERING(21,"Technical Engineering","TECHENG"),
	UNARMED(22,"Unarmed Combat","UNARMED");
	
	
	private int index;
	private String name;
	private String code;
	
	/**
	 * @param index
	 * @param name
	 * @param code
	 */
	private GeneralSkill(int index, String name, String code) {
		this.index = index;
		this.name = name;
		this.code = code;
	}

	/**
	 * @return the index number of this General Skill.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return name of this skill.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the code for this skill.
	 */
	public String getCode() {
		return code;
	}
	
	public static GeneralSkill getForIndex(int i) {
		for ( GeneralSkill gskill : values() ) {
			if ( gskill.getIndex()==i ) {
				return gskill;
			}
		}
		throw new IllegalArgumentException("Unknown index for general skill "+(i)+" . ");
	}
	
	public static GeneralSkill getForCode(String code) {
		for ( GeneralSkill gskill : values() ) {
			if ( gskill.getCode().equalsIgnoreCase(code) ) {
				return gskill;
			}
		}
		throw new IllegalArgumentException("Unknown code for general skill \""+(code)+"\" . ");
	}
	
	public static GeneralSkill getRandom() {
		Random r = RandomFactory.getRandom();
		return values()[ r.nextInt( values().length ) ];
	}
	
	public static int length() {
		return values().length;
	}

	/**
	 * @param name2
	 * @return
	 */
	public static GeneralSkill getForName(String name) {
		for ( GeneralSkill gskill : values() ) {
			if ( StringUtils.equalsIgnoreCase( gskill.getName(),name ) ) {
				return gskill;
			}
		}
		throw new IllegalArgumentException("Unknown code for general skill \""+(name)+"\" . ");
	}
	
	public static List<String> getNames() {
		ArrayList<String>ret = new ArrayList<String>(values().length);
		for (GeneralSkill skill : values() ) {
			ret.add( skill.getName() );
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
