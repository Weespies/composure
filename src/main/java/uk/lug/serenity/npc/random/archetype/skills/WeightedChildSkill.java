/**
 * 
 */
package uk.lug.serenity.npc.random.archetype.skills;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.random.archetype.Weighting;

/**
 * @author Luggy
 * Represents a weighting for a single named child skill.
 */
public class WeightedChildSkill {
	private static final String WEIGHTING = "weighting";
	private static final String NAME = "name";
	static final String SPECIALTY = "specialty";
	private String skillName;
	private Weighting weighting = new Weighting();
	
	/**
	 * @param skillName
	 * @param weighting
	 */
	public WeightedChildSkill(String skillName) {
		super();
		this.skillName = skillName;
		weighting = new Weighting(1);
	}
	
	/**
	 * @param xml
	 */
	public WeightedChildSkill(Element xml) {
		super();
		weighting = new Weighting(1);
		setXML( xml );
	}
	
	
	
	/**
	 * @param skillName
	 * @param value weighting value
	 */
	public WeightedChildSkill(String skillName, int value) {
		this( skillName );
		weighting.setValue(value);
	}



	public String getSkillName() {
		return skillName;
	}
	
	public Weighting getWeighting() {
		return weighting;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof WeightedChildSkill ) ) {
			return false;
		}
		
		WeightedChildSkill other = (WeightedChildSkill)obj;
		if ( !StringUtils.equalsIgnoreCase( skillName, other.getSkillName() ) ) { 
			return false;
		}
		if ( weighting.getValue()!=other.getWeighting().getValue() ) {
			return false;
		}
		
		//Ungrouped childskills
		
		return true;
	}
	
	public Element getXML() {
		Element xml = new Element(SPECIALTY);
		xml.setAttribute(NAME, skillName );
		xml.setAttribute(WEIGHTING, Integer.toString( weighting.getValue()) );
		return xml;		
	}
	
	public void setXML(Element xml) {
		skillName= xml.getAttributeValue(NAME);
		weighting.setValue( Integer.parseInt( xml.getAttributeValue(WEIGHTING) ) );
	}
	
}
