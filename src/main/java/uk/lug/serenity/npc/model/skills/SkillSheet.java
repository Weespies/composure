/**
 * 
 */
package uk.lug.serenity.npc.model.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.model.stats.StepStat;
import uk.lug.util.JDOMUtils;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Sheet for holding a characters skillDataMap and values.</p>
 * @author Luggy
 *
 */
public class SkillSheet implements SkillChangeListener {
	private HashMap<GeneralSkill, SkillData> skillDataMap = new HashMap<GeneralSkill, SkillData>();
	private LinkedList<SkillChangeListener> listeners = new LinkedList<SkillChangeListener>();
	private boolean isAdjusting = false;
	
	/**
	 * Perform equality test on this skillsheet. To pass the
	 * supplied object must be another skillsheet, have the same skillDataMap with the same
	 * values.
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof SkillSheet) ) {
			return false;
		}
		SkillSheet sheet = (SkillSheet) obj;
		for ( GeneralSkill key : GeneralSkill.values() ) {
			SkillData data1 = skillDataMap.get( key );
			SkillData data2 = sheet.getSkillDataMap().get(key);
			if ( data1==null || data2==null ) {
				return false;
			}
			if ( !data1.equals(data2) ) {
				return false;
			}
		}
		return true;
	}
	
	public SkillSheet() {
		
	}
	
	/**
	 * Blank all skillDataMap
	 */
	public void blank() {
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			skillDataMap.get( skill ) .blank();
		}
	}
	
	/**
	 * Return an ASCII output of this skillsheet.
	 */
	public StringBuilder getAsText() {
		StringBuilder sb= new StringBuilder(1024);
		
		String[] keys = GeneralSkill.getNames().toArray(new String[0]);
		int len = 0;
		for ( String s: keys ) {
			len = ( s.length()>len ? s.length() : len );
		}
		for (GeneralSkill skill: GeneralSkill.values() ) {
			//General skill
			SkillData sdata = skillDataMap.get( skill );
			sb.append( skill.getName() ) ;
			for ( int i=0;i<len-skill.getName().length();i++) {
				sb.append(" ");
			}
			sb.append( " " ) ;
			sb.append( StepStat.getDiceFor( sdata.getPoints() ) );
			sb.append( "\n" );
			//Show child skillDataMap if any
			HashMap<String, Integer> children = sdata.getChildrenPoints();
			String[] childKeys = children.keySet().toArray( new String[ children.size() ] );
			for ( String childname : childKeys ) {
				int pts = children.get( childname ).intValue();
				if ( pts> 0 ) {
					sb.append(" +--");
					sb.append( childname );
					sb.append( " " ) ;
					sb.append( StepStat.getDiceFor( 6 + pts ) );
					sb.append( "\n");
				}
			}
		}
		
		return sb;
	}
	
	/**
	 * Initialise the skillsheet, which involves loading all skills
	 * at 0 points.
	 */
	public void init() {
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			SkillData data = skillDataMap.get( skill.getName() ) ;
			if ( data==null ) {
				data = SkillData.createSkillData( skill ) ; 
				skillDataMap.put( skill, data ) ;
				data.addSkillChangeListener( this ) ;
			} else {
				data.setPoints( 0 ) ;
				for ( String s : data.getChildren() ) {
					data.setChildPoints( s , 0 );
				}
			}
		}
	}

	
	/**
	 * @return Returns the skillDataMap.
	 */
	public HashMap<GeneralSkill, SkillData> getSkillDataMap() {
		return skillDataMap;
	}

	/**
	 * Adds a listener to be notified of any skill changes.
	 * @param cl
	 */
	public void addSkillChangeListener(SkillChangeListener cl ) {
		listeners.add(cl);
	}
	
	/**
	 * Removes a listener from the skill change notification list.
	 * @param cl
	 */
	public void removeSkillChangeListener( SkillChangeListener cl ) {
		listeners.remove(cl);
	}
	
	/**
	 * Don't care, just pass this event on to my listeners
	 */
	public void SkillChanged(SkillChangeEvent evt) {
		switch ( evt.getType() ) {
			case SkillChangeEvent.POINTS_CHANGED :
				
		}
		if ( !isAdjusting ) {
			for ( SkillChangeListener cl : listeners ) {
				cl.SkillChanged( evt );
			}
		}
	}
	
	/**
	 * Returns the total number of skill points in use over all skillDataMap , including child skillDataMap.
	 */
	public int getTotalPoints() {
		int pts = 0;
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			pts = pts+ skillDataMap.get( skill ).getTotalPoints() ;
		}
		return pts;
	}
	
	/**
	 * Returns the number of points for a skill.  If that skill has no points
	 * the only the points for the parent is returned.
	 * @param skill
	 * @return
	 */
	public int getPointsIn( String skill ) {
		int ret=0;
		if ( skillDataMap.containsKey( skill ) ) {
			ret = skillDataMap.get( skill ).points;
		} else {
			String parent = findParent( skill );
			ret=ret+ skillDataMap.get( GeneralSkill.getForName(parent) ).getPoints();
			ret=ret+ skillDataMap.get( GeneralSkill.getForName(parent) ).getChildPoints( skill );
		
		}
		return ret;
	}
	
	/**
	 * Find the parent of the given skill name.
	 * @param name
	 * @return
	 */
	public GeneralSkill findParentSkill( String name ) {
		for( GeneralSkill s : GeneralSkill.values() ) {
			if ( skillDataMap.get(s).getChildren().contains( name ) ) {
				return s;
			}
		}
		return null;
	}
	
	public String findParent( String name ) {
		GeneralSkill s = findParentSkill(name);
		return (s==null ? null : s.getName() );
	}
	
	/**
	 * Returns a named skill
	 * @param name
	 * @return
	 */
	public SkillData getNamed( String name ) {
		return skillDataMap.get( GeneralSkill.getForName(name) );
	}
	
	public SkillData getFor(GeneralSkill skill) {
		if ( !skillDataMap.containsKey(skill) ) {
			SkillData data = SkillData.createSkillData(skill);
			skillDataMap.put( skill, data);
		}
		return skillDataMap.get( skill );
	}
	
	/**
	 * Return skill sheet as an xml element.
	 * @return
	 */
	public Element getXML() {
		Element xml= new Element("skillDataMap");
		for ( GeneralSkill s : GeneralSkill.values() ) {
			SkillData data = skillDataMap.get( s ) ;
			xml.addContent( data.getXML() );
		}
		return xml;
	}
	
	/**
	 * Set from an xml element
	 */
	public void setXML( Element xml ) {
		Element[] data = JDOMUtils.getChildArray( xml , "skillData" );
		for ( Element e : data ) {
			String name = e.getAttributeValue("name");
			SkillData skilldata = skillDataMap.get( name ) ;
			skillDataMap.remove( name );
			skillDataMap.put( GeneralSkill.getForName(name), SkillData.createSkillData(e ) );
		}
	}
	
	/**
	 * Empty the skillDataMap list, returning all skillDataMap to 0 and
	 * giving back all original skill points.
	 *
	 */
	public void empty() {
		for ( GeneralSkill s : GeneralSkill.values() ) {
			skillDataMap.get(s).blank();
			
		}
	}

	/**
	 * Return the point total (children inclusive) for a given skill.
	 * @param skill
	 * @return
	 */
	public int getPointsIn(GeneralSkill skill) {
		if ( !skillDataMap.containsKey(skill) ) {
			return 0;
		}
		return getPointsIn( skill.getName() );
	}

	public boolean hasSkill(String skillName) {
		for ( GeneralSkill gs: skillDataMap.keySet() ) {
			if (StringUtils.equals(gs.getName(), skillName) ) {
				return true;
			}
			Map<String,Integer> childPoints = skillDataMap.get(gs).getChildrenPoints();
			if ( childPoints.containsKey(skillName) && childPoints.get(skillName)>0) {
				return true;
			}
		}
		return false;
	}
	
}
