/**
 * 
 */
package uk.lug.serenity.npc.random.archetype.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.model.skills.SkillSheet;
import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.util.JDOMUtils;

/**
 * @author Luggy
 * Weighting for a group of child skills. 
 */
public abstract class ChildSkillGroup {
	private static final String SPECIALTY = "specialty";
	private static final String WEIGHTING = "weighting";
	private static final String NAME = "name";
	private List<WeightedChildSkill> childSkills;
	private Weighting weighting = new Weighting();
	private String name;
	
	/**
	 * @return the bias total for all group members.
	 */
	public int getBiasTotal() {
		if ( childSkills==null || childSkills.size()<1 ) {
			return -1;
		}
		int ret=0;
		for ( WeightedChildSkill wcs : childSkills ) {
			ret += wcs.getWeighting().getValue();
		}
		return ret;
	}
	
	/**
	 * @param name
	 */
	public ChildSkillGroup(String name) {
		this();
		this.name = name;
	}

	/**
	 * Construct an ExclusiveChildSkillGroup
	 */
	public ChildSkillGroup() {
		super();
		childSkills = new ArrayList<WeightedChildSkill>();
		weighting.setValue(1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Weighting getWeighting() {
		return weighting;
	}

	/**
	 * @return the map of the child skills and their weightings.
	 */
	public List<WeightedChildSkill> getChildSkills() {
		return childSkills;
	}

	/**
	 * Add a weighted child skill choice.  If the skill already exists then the existing weighting 
	 * will be removed.
	 * @param skillName name of the skill.
	 * @param value weighting for the skill.
	 */
	public void addSkill(String skillName, int value) {
		childSkills.add( new WeightedChildSkill(skillName,value));
	}
	
	/**
	 * Add all WeightedChildSkills from the given list.
	 * @param newMembers
	 */
	public void addSkills( List<WeightedChildSkill> newMembers) {
		childSkills.addAll( newMembers );
	}
	
	/**
	 * Add a weighted child skill choice.  If the skill already exists then the existing weighting 
	 * will be removed.
	 * @param skillName name of the skill.
	 * @param weighting weighting for the skill.
	 */
	public void addSkill(WeightedChildSkill childSkill) {
		childSkills.add( childSkill ); 
	}
	
	/**
	 * Check for the presence of a named child skill.
	 * @param skillName
	 * @return true if skillName exists in this group.
	 */
	public boolean hasSkill(String skillName) {
		return getSkillIndex(skillName)!=-1;
	}
	
	/**
	 * Check for the presence of a named child skill .
	 * @param skillName
	 * @return the index within the list of the skill or -1 if not found.
	 */
	private int getSkillIndex(String skillName) {
		for ( int i=0;i<childSkills.size();i++ ) {
			if ( StringUtils.equalsIgnoreCase( childSkills.get(i).getSkillName(), skillName ) ) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Remove the named skill and associated weighting.
	 * @param skillName
	 */
	public void removeSkill(String skillName) {
		int idx = getSkillIndex(skillName);
		if ( idx!=-1) {
			childSkills.remove( idx );
		}
	}
	
	/**
	 * Return the named WeightedChildSkill .
	 * @param skillName
	 * @return the WeightedChildSkill or null if no such skill exists in this group.
	 */
	public WeightedChildSkill getWeightedChildSkill(String name) {
		int idx=getSkillIndex(name);
		return (idx==-1 ? null : childSkills.get(idx) );
	}
	
	/**
	 * @return the total of all the weightings added together.
	 */
	public int getWeightingTotal() {
		int ret=0;
		for ( WeightedChildSkill wcs: childSkills ) {
			ret = ret + wcs.getWeighting().getValue();
		}
		return ret;
	}
	
	/**
	 * @return the number of WeightedChildSkills in this group.
	 */
	public int size() {
		return childSkills.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof ChildSkillGroup) ) {
			return false;
		}
		ChildSkillGroup other = (ChildSkillGroup)obj;
		if ( !StringUtils.equalsIgnoreCase(name, other.getName())) {
			return false;
		}
		if ( other.getChildSkills().size()!=childSkills.size() ) {
			return false;
		}
		for ( int i=0;i<childSkills.size();i++) {
			if ( !childSkills.get(i).equals( other.getChildSkills().get(i) )) {
				return false;
			}
		}
		return true;
	}
	
	public abstract String getDescription();
	
	/**
	 * @return number of child skills in this group.
	 */
	public int getSize() {
		return (childSkills==null ? 0 : childSkills.size() );
	}
	
	public void setXML(Element xml) {
		name = xml.getAttributeValue(NAME);
		weighting.setValue(Integer.parseInt( xml.getAttributeValue(WEIGHTING) ) );
		childSkills.clear();
		List<Element> children = JDOMUtils.getChildren(xml, SPECIALTY );
		for ( Element e : children ) {
			childSkills.add( new WeightedChildSkill(e) );
		}
		
	}
	
	public Element getXML() {
		Element xml = new Element(getXMLName() );
		xml.setAttribute(NAME, name);
		xml.setAttribute(WEIGHTING, Integer.toString(weighting.getValue()));
		for ( WeightedChildSkill child : childSkills ) {
			xml.addContent( child.getXML() );
		}
		return xml;
	}

	/**
	 * @return
	 */
	protected abstract String getXMLName();

	/**
	 * 
	 */
	public void sortChildren() {
		Collections.sort( childSkills, new Comparator<WeightedChildSkill>() {

			public int compare(WeightedChildSkill o1, WeightedChildSkill o2) {
				return o1.getSkillName().compareTo(o2.getSkillName());
			}});
		
	}

	/**
	 * @param sheet
	 * @param random
	 * @return
	 */
	public abstract String getRandomMember(SkillSheet sheet, Random random);
}
