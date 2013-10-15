/**
 * 
 */
package uk.lug.serenity.npc.random.archetype;

import java.util.HashMap;
import java.util.Random;

import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.random.archetype.skills.GeneralSkillBias;

/**
 * Copyright 10 Jul 2007
 * @author Paul Loveridge
 * <p>Profile object.  Characters can be generated randomly 
 * by the generate but the Archetype selected can be used to 
 * bias the randomisation.</p>
 * <p>The Archetype encompasses the functionallity of the old StatProfiles, includes
 * the skills profile list, and includes the new equipment packs.</P>
 * 
 */
public class Archetype2 {
	public static final int DEFAULT_WEIGHTING = 10;
	private static final int INITIAL_SKILL_BIAS_LIST_SIZE = 5;
	private static final int INITIAL_FORBIDDEN_TRAIT_LIST_SIZE = 5;
	private HashMap<MainStat,Weighting> statBiases;
	private HashMap<GeneralSkill, GeneralSkillBias> generalSkillBiases;
	private String name;
	private String author;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Construct an empty Archetype.
	 */
	public Archetype2() {
		super();
		initStats();
		initSkills();
	}

	/**
	 * Initialise pure random stat biases.
	 */
	private void initStats() {
		statBiases = new HashMap<MainStat, Weighting>();
		for ( MainStat stat : MainStat.values() ) {
			statBiases.put( stat, new Weighting(DEFAULT_WEIGHTING));
		}
	}

	/**
	 * Initialise the pure random skills map.
	 */
	private void initSkills() {
		generalSkillBiases = new HashMap<GeneralSkill, GeneralSkillBias>();
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			generalSkillBiases.put( skill, new GeneralSkillBias(skill));
		}
	}

	/**
	 * @return Main stat biases
	 */
	public HashMap<MainStat, Weighting> getStatBiases() {
		return statBiases;
	}

	/**
	 * Sets the main stat biases
	 * @param newStatBiases
	 */
	public void setStatBiases(HashMap<MainStat, Weighting> newStatBiases) {
		for ( MainStat stat : MainStat.values() ) {
			if ( !newStatBiases.containsKey(stat) ) {
				throw new IllegalArgumentException("Main stat "+(stat.getName())+" missing from map.");
			}
		}
		this.statBiases = newStatBiases;
	}

	/**
	 * @return general skills bias
	 */
	public HashMap<GeneralSkill, GeneralSkillBias> getGeneralSkillBiases() {
		return generalSkillBiases;
	}

	/**
	 * @param newSkillBiases general skills bias
	 */
	public void setGeneralSkillBiases(
			HashMap<GeneralSkill, GeneralSkillBias> newSkillBiases) {
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			if ( !newSkillBiases.containsKey(skill) ) {
				throw new IllegalArgumentException("General skill "+ (skill.getName())+" missing from map.");
			}
		}
		this.generalSkillBiases = newSkillBiases;
	}
	
	public int getTotalStatWeighting() {
		int ret=0;
		for ( MainStat stat : MainStat.values() ) {
			ret = ret + ( statBiases.get(stat).getValue() ) ;
		}
		return ret;
	}
	
	public int getTotalGeneralSkillWeighting() {
		int ret=0;
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			ret = ret + ( generalSkillBiases.get(skill).getWeighting().getValue() ) ;
		}
		return ret;
	}
	
	public MainStat getRandomStat(Random r ) {
		int total = getTotalStatWeighting();
		int n = r.nextInt( total );
		
		for ( MainStat stat : MainStat.values() ) {
			Weighting weighting = statBiases.get(stat);
			if ( n<weighting.getValue() ) {
				return stat;
			}
			n=n-weighting.getValue();
		}
		return null;
	}
	
	public GeneralSkill getRandomGeneralSkill(Random r ) {
		int total = getTotalGeneralSkillWeighting();
		int n = r.nextInt( total );
		
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			Weighting weighting = generalSkillBiases.get(skill).getWeighting();
			if ( n<weighting.getValue() ) {
				return skill;
			}
			n=n-weighting.getValue();
		}
		return null;
	}
}
