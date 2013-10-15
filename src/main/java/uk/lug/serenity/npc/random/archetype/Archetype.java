/**
 * 
 */
package uk.lug.serenity.npc.random.archetype;

import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.random.archetype.skills.GeneralSkillBias;
import uk.lug.util.JDOMUtils;

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
public class Archetype {
	private static final String ENABLED = "enabled";
	private static final String DESCRIPTION = "description";
	private static final String AUTHOR = "author";
	private static final String NAME = "name";
	private static final String ARCHETYPE = "archetype";
	private static final String STAT = "stats";
	public static final int DEFAULT_WEIGHTING = 1;
	private static final int INITIAL_SKILL_BIAS_LIST_SIZE = 5;
	private static final int INITIAL_FORBIDDEN_TRAIT_LIST_SIZE = 5;
	private boolean enabled=true;
	private String name;
	private String author;
	private String description;
	private StatBiasList statBiases;
	private HashMap<GeneralSkill, GeneralSkillBias> generalSkillBiases;
	
	/**
	 * Construct an empty Archetype.
	 */
	public Archetype() {
		super();
		initStats();
		initSkills();
	}

	/**
	 * Initialise pure random stat biases.
	 */
	private void initStats() {
		statBiases = new StatBiasList();
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
			generalSkillBiases.put( skill, new GeneralSkillBias(skill) );
			generalSkillBiases.get( skill ).getWeighting().setValue( 1 );
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
	public void setStatBiases(StatBiasList newStatBiases) {
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
	public void setGeneralSkillBiases(HashMap<GeneralSkill, GeneralSkillBias> newSkillBiases) {
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
	 * Set the data for this archetype from an xml element.
	 * @param xml
	 */
	public void setXML(Element xml) {
		enabled = StringUtils.equalsIgnoreCase( "true", xml.getAttributeValue(ENABLED));
		name = xml.getAttributeValue(NAME);
		author = xml.getAttributeValue(AUTHOR);
		description = xml.getChildText(DESCRIPTION);
		//Stats
		statBiases.setXML( xml.getChild(STAT) );
		
		//Skils
		for ( Element skillXML : JDOMUtils.getChildren(xml, GeneralSkillBias.SKILL) ) {
			String name = skillXML.getAttributeValue(GeneralSkillBias.NAME);
			GeneralSkill skill = GeneralSkill.getForCode(name);
			generalSkillBiases.get( skill ) .setXML( skillXML );
		}
	}
	
	public Element getXML() {
		Element xml = new Element(ARCHETYPE);
		xml.setAttribute(ENABLED, Boolean.toString( enabled ) );
		xml.setAttribute(NAME,name);
		xml.setAttribute(AUTHOR,author);
		Element descriptionNode = new Element(DESCRIPTION);
		descriptionNode.setText(description);
		xml.addContent( descriptionNode );
		
		//Stats
		xml.addContent( statBiases.getXML() );
		
		//Skills		
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			if ( generalSkillBiases.containsKey( skill ) ) {
				xml.addContent( generalSkillBiases.get(skill).getXML() );
			}
		}
		return xml;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof Archetype) ) {
			return false;
		}
		Archetype other = (Archetype) obj;

		if ( !StringUtils.equals(name, other.getName() ) ) {
			return false;
		}
		if ( !StringUtils.equals(author, other.getAuthor() ) ) {
			return false;
		}
		if ( !ObjectUtils.equals(statBiases, other.getStatBiases() ) ) {
			return false;
		}
		
		if ( !skillsMatch( other ) ) {
			return false;
		}
		
		return true;
	}
	
	private boolean skillsMatch( Archetype other ) {
		if ( generalSkillBiases.size()!=other.getGeneralSkillBiases().size() ) {
			return false;
		}
		for ( GeneralSkill skill : generalSkillBiases.keySet() ) {
			if ( !other.getGeneralSkillBiases().containsKey(skill) ){
				return false;
			}
			GeneralSkillBias localSkill = generalSkillBiases.get( skill );
			GeneralSkillBias otherSkill = other.getGeneralSkillBiases().get( skill );
			if ( !localSkill.equals(otherSkill) ) {
				return false;
			}
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
