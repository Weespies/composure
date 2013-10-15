/**
 * 
 */
package uk.lug.serenity.npc.random.archetype.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.MutableList;
import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.skills.SkillSheet;
import uk.lug.serenity.npc.model.skills.Specialty;
import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.util.JDOMUtils;

/**
 * @author Luggy
 * Datamodel for the biasing of a complete general skill, including
 * all childskills and groups.
 */
public class GeneralSkillBias {
	private static final String WEIGHTING = "weighting";
	public static final String NAME = "name";
	public static final String SKILL = "skill";
	private GeneralSkill parentSkill;
	private Weighting weighting;
	private MutableList<WeightedChildSkill> childSkills;
	private MutableList<ChildSkillGroup> childGroups;
	private List<SkillBiasListener> listeners = new ArrayList<SkillBiasListener>();
	
	
	/**
	 * Construct a general skill bias will all child skills
	 * inside of their own weighting.
	 * @param skill
	 */
	public GeneralSkillBias(GeneralSkill skill) {
		super();
		this.parentSkill = skill;
		initBlankBias();
	}

	/**
	 * Initialise this bias as a new one.
	 */
	private void initBlankBias() {
		childGroups = new MutableList<ChildSkillGroup>();
		childSkills = new MutableList<WeightedChildSkill>();
		List<String> children = SkillsManager.getChildrenFor( parentSkill );
		Collections.sort( children );
		for ( String childName : children ) {
			WeightedChildSkill wcs = new WeightedChildSkill(childName);
			childSkills.add( wcs );
		}
		weighting = new Weighting();
	}
	
	/**
	 * Add a new named child skill with an initial weighting of 1
	 * @param name
	 */
	public void addNewChildSkill(String name) {
		List<String> children = SkillsManager.getChildrenFor( parentSkill );
		Collections.sort( children );
		for ( String str : children ) {
			if ( StringUtils.equalsIgnoreCase(str,name) ) {
				throw new IllegalArgumentException("There is already a child skill of that name.");
			}
		}
		
		WeightedChildSkill wcs = new WeightedChildSkill( name );
		childSkills.add( wcs ) ;
		SkillsManager.getSkill( parentSkill ).getSpecialtyList().add( new Specialty(name) );
		
		for ( SkillBiasListener listener : listeners ) {
			listener.addedChildSkill( wcs );
		}
	}
	
	private List<WeightedChildSkill> compileChildList(List<String> skillNames) {
		List<WeightedChildSkill> ret = new ArrayList<WeightedChildSkill>( skillNames.size() );
		for (String childName : skillNames ) {
			WeightedChildSkill child = null;
			for ( WeightedChildSkill wcs: childSkills ) {
				if ( StringUtils.equals( wcs.getSkillName(), childName) ) {
					child =wcs;
					break;
				}
			}
			if ( child==null ) {
				child = new WeightedChildSkill(childName);
			}
			ret.add(child);
		}
		return ret;
	}
	
	public void createExclusiveGroup(String groupName, List<String> groupSkills) {
		//Build group
		ExclusiveChildSkillGroup exclusiveGroup = new ExclusiveChildSkillGroup(groupName);
		List<WeightedChildSkill> members = compileChildList( groupSkills );
		exclusiveGroup.addSkills(members);
		
		childSkills.removeAll(members);
		childGroups.add(exclusiveGroup);
		
		//Fire listeners
		for ( SkillBiasListener listener : listeners ) {
			listener.groupFormed( exclusiveGroup );
		}
		
	}
	
	public void createWeightingGroup(String groupName, List<String> groupSkills) {
		//Build group
		WeightedChildSkillGroup weightedGroup = new WeightedChildSkillGroup(groupName);
		List<WeightedChildSkill> members = compileChildList( groupSkills );
		weightedGroup.addSkills(members);
		
		childSkills.removeAll(members);
		childGroups.add(weightedGroup);
		
		//Fire listeners
		for ( SkillBiasListener listener : listeners ) {
			listener.groupFormed( weightedGroup );
		}
		
	}
	
	/**
	 * Remove the weighting for a child skill whether it is inside of a group
	 * or on it's own.  Child skill is NEVER removed from the global child skills list.
	 * @param name name of child skill to remove.  (Case insensitive)
	 */
	public void removeChildSkill(String name ) {
		if ( isInGroup(name) ) {
			for ( ChildSkillGroup group : childGroups ) {
				if ( group.hasSkill(name) ) {
					removeFromGroup(group.getName(), name);
					
				}
			}
		} else {
			for ( int i=0; i<childSkills.size();i++ ) {
				if ( StringUtils.equalsIgnoreCase(name, childSkills.get(i).getSkillName() ) ){
					WeightedChildSkill wcs = childSkills.get(i);
					childSkills.remove(i);
					for ( SkillBiasListener listener : listeners ) {
						listener.removedChildSkill( wcs );
					}
					return;
				}
			}
		}
	}
	
	/**
	 * Add a child skill to the named group.
	 * If the skill does has a weightedchildskill in this bias then it will 
	 * be moved into the group.  If not, a new WeightedChildSkill will be created. 
	 * @param skillName
	 * @throws IllegalArgumentException if the skill is already in a group.
	 */
	public void addToGroup(ChildSkillGroup group, String skillName) {
		if ( isInGroup(skillName) ) {
			throw new IllegalArgumentException("That child skill is already in a group.");
		}
		WeightedChildSkill member = getWeightedChildSkill(skillName);
		if ( member==null ) {
			member= new WeightedChildSkill(skillName);
		} else {
			childSkills.remove( member );
		}
		
		group.addSkill( member );
		for ( SkillBiasListener listener : listeners ) {		
			listener.addedToGroup( group, member );
		}
	}

	/**
	 * Remove a named child skill from a ChildSkillGroup and make it 
	 * an individual skill .
	 * @param group group containing the child.
	 * @param skillName child to remove.
	 */
	public void removeFromGroup(String groupName, String skillName) {
		ChildSkillGroup group = getChildSkillGroup(groupName);
		if ( group==null ) {
			throw new IllegalArgumentException("No such named group.");
		}
		if ( !group.hasSkill(skillName)) {
			throw new IllegalArgumentException("ChildSkillGroup does not contain that skill.");
		}
		WeightedChildSkill skill = group.getWeightedChildSkill(skillName);
		group.removeSkill(skillName);
		childSkills.add(skill);
		for ( SkillBiasListener listener : listeners ) {		
			listener.removedFromGroup(group, skill);
		}
	}
	
	public Weighting getWeighting() {
		return weighting;
	}
	
	/**
	 * Check for the presence of a named child skill, whether in a group or not.
	 * @param name
	 * @return
	 */
	public boolean hasChildSkill(String name) {
		for ( WeightedChildSkill wcs : childSkills ) {
			if ( StringUtils.equalsIgnoreCase( wcs.getSkillName(), name ) ) {
				return true;
			}
		}
		for ( ChildSkillGroup group : childGroups ){
			if ( group.hasSkill(name) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check for the presence of a named child skill group
	 * @param name
	 * @return
	 */
	public boolean hasChildGroup(String name) {
		for ( ChildSkillGroup group : childGroups ){
			if ( StringUtils.equalsIgnoreCase(name, group.getName()) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Test to see if the given child skill exists in a group.
	 * @param name
	 * @return true if any group in this bias has a skill with the name.
	 */
	public boolean isInGroup(String name) {
		return getGroupContaining(name)!=null;
	}
	
	/**
	 * Search all child groups and return the one containing the named skill.
	 * @param skillName name of skill to be searched for.
	 * @return the group containing the skill or null if the no group contains the skill.
	 */
	public ChildSkillGroup getGroupContaining(String skillName) {
		for ( ChildSkillGroup group : childGroups ){
			if ( group.hasSkill(skillName) ) {
				return group;
			}
		}
		return null;
	}

	/**
	 * Return the named weighted child skill, searching in groups if not found 
	 * as an individual child skill.
	 * @param name
	 * @return the WeightedChildSkill with the name or null if no skill could be found.
	 */
	public WeightedChildSkill getWeightedChildSkill(String name) {
		for ( WeightedChildSkill wcs : childSkills ) {
			if ( StringUtils.equalsIgnoreCase( wcs.getSkillName(), name ) ) {
				return wcs;
			}
		}
		for ( ChildSkillGroup group : childGroups ){
			if ( group.hasSkill(name) ) {
				return group.getWeightedChildSkill(name);
			}
		}
		return null;
	}
	
	/**
	 * Get the named skill group
	 * @param name
	 * @return the group with the name or null if there is no such named group.
	 */
	public ChildSkillGroup getChildSkillGroup(String name) {
		for ( ChildSkillGroup group : childGroups ){
			if ( StringUtils.equalsIgnoreCase(name, group.getName() ) ) {
				return group;
			}
		}
		return null;
	}
	
	/**
	 * Return unground child skills
	 * @return
	 */
	public MutableList<WeightedChildSkill> getChildSkills() {
		return childSkills;
	}

	
	public MutableList<ChildSkillGroup> getChildGroups() {
		return childGroups;
	}

	
	public GeneralSkill getParentSkill() {
		return parentSkill;
	}
	
	/**
	 * Add a listener to be notified of events in this skill bias.
	 * @param listener
	 */
	public void addListener( SkillBiasListener listener ) {
		listeners.add( listener );
	}
	
	/**
	 * Add a listener to be notified of events in this skill bias.
	 * @param listener
	 */
	public void removeListener( SkillBiasListener listener ) {
		listeners.remove( listener );
	}

	public List<SkillBiasListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<SkillBiasListener> listeners) {
		this.listeners = listeners;
	}

	public void setParentSkill(GeneralSkill parentSkill) {
		this.parentSkill = parentSkill;
	}

	public void setWeighting(Weighting weighting) {
		this.weighting = weighting;
	}

	public void setChildSkills(MutableList<WeightedChildSkill> childSkills) {
		this.childSkills = childSkills;
	}

	public void setChildGroups(MutableList<ChildSkillGroup> childGroups) {
		this.childGroups = childGroups;
	}
	
	public void disbandGroup(String groupName) {
		ChildSkillGroup group = getChildSkillGroup(groupName);
		if ( group==null ) {
			throw new IllegalArgumentException("No group of that name.");
		}
		List<WeightedChildSkill> members= new ArrayList<WeightedChildSkill>(group.getChildSkills());
		childSkills.addAll( members );
		childGroups.remove(group);
		for (SkillBiasListener listener : listeners ) {
			listener.groupDisbanded( group, members );
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof GeneralSkillBias) ){
			return true;
		}
		
		GeneralSkillBias other = (GeneralSkillBias)obj;
		if ( !ObjectUtils.equals( parentSkill, other.getParentSkill()) ) {
			return false;
		}
		if ( weighting.getValue()!=other.getWeighting().getValue() ) {
			return false;
		}
		
		if ( !childSkillMatch( other) ) {
			return false;
		}
		
		if ( !childGroupsMatch( other ) ) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Test the child skills (ungrouped) against those of another GeneralSkillBias.
	 * @param other
	 * @return true if the other GeneralSkillBias has exactly the same ungrouped child
	 * skills with the same weightings.
	 */
	private boolean childSkillMatch( GeneralSkillBias other ) {
		if ( childSkills.size()!=other.getChildSkills().size() ) {
			return false;
		}
		for ( WeightedChildSkill wcs : childSkills ) {
			if ( !other.hasChildSkill(wcs.getSkillName())) {
				return false;
			}
			if ( other.isInGroup(wcs.getSkillName())) {
				return false;
			}
			if ( !ObjectUtils.equals( other.getWeightedChildSkill(wcs.getSkillName()), wcs ) ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Test the child skill grous against those of another GeneralSkillBias.
	 * @param other
	 * @return true if the other GeneralSkillBias has exactly the same ungrouped child
	 * skills with the same weightings.
	 */
	private boolean childGroupsMatch( GeneralSkillBias other ) {
		if ( childGroups.size()!=other.getChildGroups().size() ) {
			return false;
		}
		for (ChildSkillGroup localGroup : childGroups) {
			if (!other.hasChildGroup(localGroup.getName())) {
				return false;
			}
			ChildSkillGroup otherGroup = other.getChildSkillGroup(localGroup.getName());
			if ( !localGroup.getClass().equals( otherGroup.getClass() ) ) {
				return false;
			}
			if ( !localGroup.equals( otherGroup ) ) {
				return false;
			}

		}
		return true;
	}
	
	/**
	 * @return xml element containing details of this skill bias.
	 */
	public Element getXML() {
		Element xml = new Element(SKILL);
		xml.setAttribute(NAME, parentSkill.getCode() );
		xml.setAttribute(WEIGHTING, Integer.toString( weighting.getValue() ) );
		
		//Ungrouped child skills
		//(keeps it nicer if we sort prior to write)
		Collections.sort( childSkills, new Comparator<WeightedChildSkill>() {
			public int compare(WeightedChildSkill o1, WeightedChildSkill o2) {
				return o1.getSkillName().compareTo(o2.getSkillName());
			}});
		for ( WeightedChildSkill wcs : childSkills ) {
			xml.addContent( wcs.getXML() );
		}
		
		//Grouped child skills
		Collections.sort( childGroups, new Comparator<ChildSkillGroup>() {
			public int compare(ChildSkillGroup o1, ChildSkillGroup o2) {
				return o1.getName().compareTo(o2.getName());
			}});
		for ( ChildSkillGroup csg : childGroups ) {
			xml.addContent( csg.getXML() );
		}
		return xml;
	}
	
	/**
	 * Configure this skill bias from an xml element.
	 * @param xml
	 */
	public void setXML( Element xml) {
		String name = xml.getAttributeValue(NAME);
		parentSkill = GeneralSkill.getForCode(name);
		weighting.setValue( Integer.parseInt( xml.getAttributeValue(WEIGHTING) ) );
		
		//ungrouped child skills
		childSkills.clear();
		for ( Element e : JDOMUtils.getChildren(xml, WeightedChildSkill.SPECIALTY) ) {
			childSkills.add( new WeightedChildSkill(e));
		}
		
		childGroups.clear();
		//weighting groups
		for ( Element e: JDOMUtils.getChildren(xml, WeightedChildSkillGroup.WEIGHTING_GROUP) ) {
			WeightedChildSkillGroup group = new WeightedChildSkillGroup();
			group.setXML( e );
			childGroups.add( group );
		}
		
		//exclusive groups
		for ( Element e: JDOMUtils.getChildren(xml, ExclusiveChildSkillGroup.EXCLUSIVE_GROUP) ) {
			ExclusiveChildSkillGroup group = new ExclusiveChildSkillGroup();
			group.setXML( e );
			childGroups.add( group );
		}
	}
	
	/**
	 * Perform a case insensitive alpha sort on the childskills .
	 */
	public void sortChildSkills() {
		Collections.sort( childSkills, new Comparator<WeightedChildSkill>(){
			public int compare(WeightedChildSkill o1, WeightedChildSkill o2) {
				return o1.getSkillName().toLowerCase().compareTo(o2.getSkillName().toLowerCase());
			}});
	}
	
	/**
	 * Performs a case insensitive alpha sort on the child skill groups.
	 * @return
	 */
	public void sortChildGroups() {
		Collections.sort( childGroups, new Comparator<ChildSkillGroup>() {

			public int compare(ChildSkillGroup o1, ChildSkillGroup o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}});
	}
	
	/**
	 * Get the bias values for all weighted child skills and child skill groups combined, but
	 * not the totals inside the groups;
	 * @return
	 */
	public int getRootBiasTotal() {
		return getWeightedChildrenTotal()+getWeightedGroupsTotal();
	}

	/**
	 * @return
	 */
	public int getWeightedChildrenTotal() {
		int ret=0;
		for ( WeightedChildSkill wcs : childSkills ) {
			ret += wcs.getWeighting().getValue();
		}
		return ret;
	}

	/**
	 * For a random bias roll return the weighted child skill.
	 * @param n
	 * @return
	 */
	public WeightedChildSkill getChildSkillForBias(int n) {
		if ( resultInGroup(n) ) {
			throw new IllegalArgumentException("That bias value returns a group not a weighted child skill.");
		}
		int v=n;
		for ( WeightedChildSkill wcs : childSkills ) {
			v -= wcs.getWeighting().getValue();
			if (v<0) {
				return wcs;
			}
		}
		throw new IllegalArgumentException("No skill for that value.");
	}
	
	public ChildSkillGroup getGroupForBias(int n) {
		if ( !resultInGroup(n) ) {
			throw new IllegalArgumentException("That bias value returns does not a child skill group.");
		}
		int v = n;
		v -= getWeightedChildrenTotal();
		for ( ChildSkillGroup group : childGroups ) {
			v -= group.getWeighting().getValue();
			if ( v<=0 ) {
				return group;
			}
		}
		throw new IllegalArgumentException("No group for that value.");
	}
	
	/**
	 * @return
	 */
	public int getWeightedGroupsTotal() {
		int ret=0;
		for ( ChildSkillGroup group : childGroups ) {
			ret += group.getWeighting().getValue();
		}
		return ret;
	}

	/**
	 * Test to see if a random number is within a group or not.
	 * @param n random roll for a skill between 0 and the root bias total of all child skills and groups.
	 * @return
	 */
	public boolean resultInGroup(int n) {
		if ( n<0 || n>=getRootBiasTotal() ) {
			throw new IllegalArgumentException(n+" is outside of root bias range.");
		}
		int n2 =n;
		for ( WeightedChildSkill wcs : childSkills ) {
			n2 -= wcs.getWeighting().getValue();
			if ( n2<0 ) {
				return false;
			}
		}
		return true;
	}
	
	public String getRandomChild(SkillSheet sheet, Random random) {
		int n = random.nextInt( getRootBiasTotal() );
		if (!resultInGroup(n) ) {
			return getChildSkillForBias(n).getSkillName();
		}
		ChildSkillGroup group = getGroupForBias(n);
		return group.getRandomMember(sheet, random);
	}

}
