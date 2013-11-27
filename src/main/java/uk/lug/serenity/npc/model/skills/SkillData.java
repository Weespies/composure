/**
 * 
 */
package uk.lug.serenity.npc.model.skills;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.model.stats.StepStat;
import uk.lug.util.JDOMUtils;


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
public class SkillData extends Skill {
	public static final int CHILD_SKILL_MAX = 16;
	public static final int PARENT_SKILL_MAX = 6;
	public static final String XML_KEY="skillData";
	private static final int MAX_GENERAL_POINTS = 6;
	public HashMap<String, Integer> childrenPoints = new HashMap<String,Integer>();
	public int points = 0;
	private LinkedList<SkillChangeListener> listeners = new LinkedList<SkillChangeListener>();
		
	/**
	 * Blanks the skill, returing the available skill points.
	 */
	public void blank() {
		if ( this.getTotalPoints()==0 ) {
			return;
		}
		String[] names = childrenPoints.keySet().toArray( new String[0] );
		for ( String s : names) {
			this.setChildPoints( s,0);
		}
		this.setPoints(0);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if ( !super.equals(o) ) {
			return false;
		}
		SkillData data = (SkillData) o;
		if ( points!=data.getPoints() ) {
			return false;
		}
		
		//Check allocated children for all pointed specialities in this skilldata
		String[] ptrsKeys = childrenPoints.keySet().toArray( new String[0] ) ;
		for ( String s : ptrsKeys ) {
			Integer a = childrenPoints.get( s ) ;
			Integer b = data.getChildrenPoints().get( s ) ;
			
			if ( !IntegerEquals(a,b) ) {
				return false;
			}
		}
		//Check for all specialities in the supplied skilldata
		ptrsKeys = data.getChildrenPoints().keySet().toArray( new String[0] ) ;
		for ( String s : ptrsKeys ) {
			Integer a = childrenPoints.get( s ) ;
			
			Integer b = data.getChildrenPoints().get( s ) ;
			if ( !IntegerEquals(a,b) ) {
				return false;
			}
		}
		return true;		
	}

	private static boolean IntegerEquals(Integer intA, Integer intB ) {
		int a = ( intA==null ? 0 : intA.intValue() ) ;
		int b = ( intB==null ? 0 : intB.intValue() ) ;
		return a==b;
	}
	
	/**
	 * Create a skill from an element.
	 * @param xml
	 */
	public static SkillData createSkillData(Element xml ) {
		SkillData data= new SkillData();
		data.setXML( xml ) ;
		return data;
	}
	
	/**
	 * Create an empty skill at 0 points from a Skill.
	 */
	public static SkillData createSkillData(Skill skill) {
		SkillData data= new SkillData();
		data.setName( skill.getName() );
		data.setPoints(0);
		data.setSkilledOnly( skill.isSkilledOnly() );
		for ( String s : skill.getChildren() ) {
			data.setChildPoints( s , 0 );
		}
		return data;
	}
	
	/**
	 * Create an empty skill at 0 points from a Skill.
	 */
	public static SkillData createSkillData(GeneralSkill skill) {
		SkillData data= new SkillData();
		data.setName( skill.getName() );
		data.setPoints(0);
		for ( String childName : SkillsManager.getChildrenFor(skill) ) {
			data.setChildPoints(childName,0);
		}
		return data;
	}
	
	/**
	 * Read XML
	 */
	@Override
	public Element getXML() {
		Element xml = super.getXML();
		xml.setName(XML_KEY);
		xml.setAttribute("points",Integer.toString( points ) );
		Element[] elems = JDOMUtils.getChildArray(xml, "specialty");
		for ( Element e : elems ) {
			String name = e.getAttributeValue("name");
			if ( childrenPoints.containsKey( name ) ) {
				int pts = childrenPoints.get( name ) ;
				if ( pts>0 ) {
					e.setAttribute( "points", Integer.toString(pts) );
					
				}
			}
			
		}	
			
		return xml;
	}
	
	/**
	 * Write XML
	 */
	@Override
	public void setXML(Element xml) {
		xml.setName( super.getXMLKey() );
		super.setXML( xml ) ;
		xml.setName( XML_KEY ) ;
		points = Integer.parseInt( xml.getAttributeValue("points") );
		
		Element[] elems = JDOMUtils.getChildArray(xml, "specialty");
		for ( Element e : elems ) {
			String name = e.getAttributeValue("name");
			String pstr = e.getAttributeValue("points") ;
			if ( StringUtils.isEmpty( pstr) ) {
				childrenPoints.put( name , new Integer(0) ) ;
			} else {
				childrenPoints.put( name , Integer.parseInt( pstr ) ) ;
			}
			
		}	
	}
	
	/**
	 * Return points value for this skill.
	 * @return
	 */
	public String getDice() {
		return StepStat.getDiceFor( points );
	}
	
	/**
	 * Return points value for this child skill.
	 * @return
	 */
	public String getChildDice(String childName) {
		if ( !children.contains(childName) ) {
			throw new IllegalArgumentException("Unknown child skill : "+childName);
		}
		if ( childrenPoints.containsKey( childName) ) {
			return StepStat.getDiceFor( points + childrenPoints.get(childName).intValue());
		} 
	
		return getDice();
	}
	
	/**
	 * Returns true if the given child skill exists.
	 * @param s
	 */
	public boolean containsChild(String s) {
		return children.contains( s );
	}
	
	/**
	 * Add a new child skill
	 */
	public void addChild( String s ) {
		if ( !children.contains( s ) ) {
			children.add( s ) ;
			fireListeners( SkillChangeEvent.CHILD_ADDED );
		}
	}
	
	/**
	 * Set the points of a specific child skill.  Note that you can add a new child skill at this point.
	 * @param childName
	 * @param pts
	 */
	public void setChildPoints(String childName, int pts) {
		if ( points<6 && pts>0 ) {
			throw new IllegalArgumentException("Cannot set child skill while general skill points are less than "+
					Integer.toString(MAX_GENERAL_POINTS) );
		}
		if ( !children.contains( childName ) ) {
			children.add( childName );
			fireListeners( SkillChangeEvent.CHILD_ADDED );
		}
		childrenPoints.remove( childName ) ;
		childrenPoints.put( childName, pts );
		fireListeners( SkillChangeEvent.CHILD_POINTS_CHANGED );
	}
	
	/**
	 * Returns the points of a specific child skill.
	 * @param childName
	 */
	public int getChildPoints(String childName) {
		if (childName.equals("Pistol")) {
			childName="Pistols";
		}
		if ( !children.contains(childName) ) {
			throw new IllegalArgumentException("Unknown child skill : "+childName+" in " + getName());
		} else if ( childrenPoints.containsKey(childName) && getPoints()==6 ) {
			return childrenPoints.get( childName ) ;
		} else {
			return 0;
		}
	}
	
	/**
	 * Checks for a child skill, regardless of whether there are any points in it.
	 * @param name
	 * @return
	 */
	public boolean hasChild(String name) {
		return children.contains( name );
	}
	
	/**
	 * Increment the points of a child skill.
	 * @param childName name of the child skill.
	 * @param increment amount to increment the child skill by. 
	 */
	public void adjustChildPoints(String childName, int increment) {
		if ( getChildPoints( childName )+increment<0 ) {
			throw new IllegalArgumentException("Adjustment would take child skill below 0.");
		}
		setChildPoints( childName, getChildPoints(childName)+increment );
	}
	
	/**
	 * Returns the total points spent on child skills.
	 */
	public int getAllChildPoints() {
		int ret=0;
		for ( String s : children ) {
			if ( childrenPoints.containsKey( s ) ) {
				ret =ret + childrenPoints.get( s ) ;
			}
		}
		return ret;
	}
	
	/**
	 * @return Returns the childrenPoints.
	 */
	public HashMap<String, Integer> getChildrenPoints() {
		return childrenPoints;
	}
	
	/**
	 * Return the number of child skills with actual points invested in them.
	 */
	public int getInvestedChildCount() {
		int res = 0;
		String[] keys = childrenPoints.keySet().toArray(new String[0]);
		for ( String s : keys ) {
			int x = childrenPoints.get(s).intValue();
			if ( x>0 ) {
				res++;
			}
		}
		return res;
	}
	
	/**
	 * @param childrenPoints The childrenPoints to set.
	 */
	public void setChildrenPoints(HashMap<String, Integer> childrenPoints) {
		this.childrenPoints = childrenPoints;
	}
	/**
	 * @return Returns the points.
	 */
	public int getPoints() {
		return points;
	}
	/**
	 * @param points The points to set.
	 */
	public void setPoints(int points) {
		this.points = points;
		fireListeners( SkillChangeEvent.POINTS_CHANGED);
	}
	
	/**
	 * Apply an incremental adjustment to the general skill total.
	 * @param increment amount to increase the general points by.
	 */
	public void adjust( int increment ) {
		if ( getPoints()+increment>MAX_GENERAL_POINTS ) {
			throw new IllegalArgumentException("Increment to skill of "+(increment)+" would cause skill points to exceed maximum.");
		} else if ( getPoints()+increment<0) {
			throw new IllegalArgumentException("Increment to skill of "+(increment)+" would cause skill points to drop below 0.");
		}
		setPoints( getPoints()+increment );
	}
	
	public void addSkillChangeListener(SkillChangeListener cl ) {
		listeners.add(cl);
	}
	
	public void removeSkillSkillChangeListener( SkillChangeListener cl ) {
		listeners.remove(cl);
	}
	
	private void fireListeners( int eventType) {
		SkillChangeEvent ce = new SkillChangeEvent(eventType, this ) ;
		for ( SkillChangeListener cl : listeners ) {
			cl.SkillChanged( ce );
		}
	}
	
	/**
	 * Return the number of points used by this and all child skills.
	 * @return
	 */
	public int getTotalPoints() {
		int pts = getPoints()+getAllChildPoints();
		return pts;
	}
	
	/**
	 * Returns the maximum value for this skill for the given amount of points free.
	 * @param freePts
	 * @return
	 */
	public int getMax(int freePts ) {
		int max = getPoints()+freePts;
		return ( max>6 ? 6 : max ) ;
	}
	
	/**
	 * Returns the minimums for this skill . which will be 
	 * 6 if any child skills have points or 0 if none do.
	 * @param freePts
	 * @return
	 */
	public int getMin() {
		return ( getAllChildPoints()==0 ? 0 : 6 );
	}
	
	/**
	 * Returns the maximum value for a named childskill for the given number of free points
	 * to spend.
	 * @param name The name of the chlid skill.
	 * @param freePoints Number of points to spend.
	 * @return
	 */
	public int getMaxForChild(String name, int freePoints ) {
		if ( getPoints()<6 ) {
			return 0;
		}
		if ( !childrenPoints.containsKey( name ) ) {
			return ( freePoints>0 ? CHILD_SKILL_MAX : 6 );
		}
		int pts = childrenPoints.get( name )+6 ;
		return ( pts+freePoints>CHILD_SKILL_MAX ? CHILD_SKILL_MAX : pts+freePoints );
	}
	
	/**
	 * Return a string representing this skill in a human readable form.
	 */
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder();
		sb.append( super.getName() );
		sb.append( " " );
		sb.append( StepStat.getDiceFor( getPoints() ) );
		for ( String s: childrenPoints.keySet().toArray( new String[0] ) ) {
			if ( childrenPoints.get(s)>0 ) {
				sb.append("\n  +---- ");
				sb.append(s);
				sb.append(" ");
				sb.append( StepStat.getDiceFor( childrenPoints.get(s) ) );
			}
		}
		return sb.toString();
	}

	/**
	 * @param name
	 * @param i
	 * @return
	 */
	@SuppressWarnings("unused")
	public int getMinForChild(String name, int i) {
		
		if ( getPoints()==6 ) {
			return 6;
		}
		return 0;
	}
	
	/**
	 * @return the number of child skills that have points invested in them.
	 */
	public int countChildrenWithPoints() {
		int ret =0;
		for ( String str : childrenPoints.keySet() ) {
			if ( childrenPoints.get(str).intValue() > 0 ) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * Reduce the general points of the skill by 1 dice step (2 points).
	 */
	public void lowerPoints() {
		if ( getPoints()==0 ) {
			throw new IllegalArgumentException("Cannot reduce a skill below 0.");
		}
		if ( getPoints()==6 && countChildrenWithPoints()>0 ) {
			throw new IllegalArgumentException("Cannot reduce a skill which has specialties.");
		}
		setPoints( getPoints()-2 );
	}

	/**
	 * Increase the general points of the skill by 1 dice step (2 points).
	 */
	public void raisePoints() {
		if ( getPoints()==6 ) {
			throw new IllegalArgumentException("Cannot increase a skill above 6.");
		}
		setPoints( getPoints()+2 );
	}

	/**
	 * @return true if the points in this skill can be raised by 1 step ( 2 points ).
	 */
	public boolean canRaisePoints() {
		return ( getPoints()<6 );
	}
	
	/**
	 * @return true if the points in this skill can be lower by 1 step ( 2 points ).
	 */
	public boolean canLowerPoints() {
		return ( getPoints()>0 && countChildrenWithPoints()==0 );
	}
	
	/**
	 * Add points to a child skill.
	 * @param name
	 * @param increase
	 */
	public void addChildPoints(String name, int increase) {
		int points = getChildPoints( name );
		points = points + increase;
		setChildPoints( name, points );
	}
	
}
