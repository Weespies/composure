/**
 * 
 */
package uk.lug.serenity.npc.model.traits;

import java.util.LinkedList;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.data.IDescribed;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Root class for Complications & Assets.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class Trait implements IDescribed {
	public static final int FOCUS_NONE = 1;
	public static final int FOCUS_ANY = 2;
	public static final int FOCUS_LIST = 3;
	public static final int SKILL_NONE = 4;
	public static final int SKILL_ANY = 5;
	public static final int SKILL_LIST = 6;
	public static final int SKILL_PARENT = 7;
	protected boolean major;
	protected boolean minor;
	protected String name;
	protected int focusType = FOCUS_NONE;
	protected String[] focusList;
	protected int skillType = SKILL_NONE;
	protected String[] skillsList;
	protected TraitType type;
	protected String description;

	/**
	 * @return Returns the focusType.
	 */
	public int getFocusType() {
		return focusType;
	}

	/**
	 * @param focusType The focusType to set.
	 */
	public void setFocusType(int focusType) {
		this.focusType = focusType;
	}
	
	
	/**
	 * @return Is this a major asset ?
	 */
	public boolean isMajor() {
		return major;
	}
	
	/**
	 * @param major The major to set.
	 */
	public void setMajor(boolean major) {
		this.major = major;
	}
	
	/**
	 * @return Is this a minor asset ?
	 */
	public boolean isMinor() {
		return minor;
	}
	
	/**
	 * @param minor The minor to set.
	 */
	public void setMinor(boolean minor) {
		this.minor = minor;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return Returns the options.
	 */
	public String[] getFocusList() {
		return focusList;
	}
	
	/**
	 * @param options The Focus options to set.
	 */
	public void setFocusList(String[] options) {
		this.focusList = options;
	}
	
	/**
	 * Returns a string array from a list of | seperated values in a string.
	 * @param options
	 * @return
	 */
	public static String[] splitStrings(String options) {
		LinkedList<String> list = new LinkedList<String>();
		StringTokenizer token = new StringTokenizer(options, "|" ,false);
		while ( token.hasMoreTokens() ) {
			list.add( token.nextToken() ) ;
		}
		return list.toArray( new String[list.size()] );
	}
	
	/**
	 * Returns a list of | seperated values in a string buillt from a string array.
	 * @param options
	 * @return
	 */
	public static String combineStrings(String[] items) {
		StringBuilder sb = new StringBuilder(items.length*10);
		for ( int i=0; i<items.length; i++ ) {
			sb.append( items[i] );
			if ( i+1<items.length ) {
				sb.append("|");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Sets the focus options from the given string, which is usually acquired from the xml
	 * element for the Trait.
	 * @param ftype <ul><li>If null or zero-length the focus is FOCUS_NONE.
	 * <li>If "any" the focus is FOCUS_ANY.
	 * <li>If contains a "|" character then is FOCUS_LIST.
	 * </ul>
	 * @throws IllegalArgumentException if the given string does not fit any of the
	 * three cases given. 
	 */
	public void setFocusAttribute( String ftype ) {
		if ( ftype==null || ftype.length()==0 ) {
			focusType = FOCUS_NONE;
 		} else if ( ftype.equalsIgnoreCase("any")) {
 			focusType = FOCUS_ANY ;
 		} else if ( ftype.indexOf("|")!=-1) {
 			focusType = FOCUS_LIST;
 			focusList= splitStrings( ftype ) ;
 		} else {
 			throw new IllegalArgumentException("Cannot parse \""+ftype+"\" as a valid focus type.");
 		}
	}
	
	/**
	 * @return the ZML attribute describing this traits focus.
	 */
	public String getFocusAttribute() {
		if (focusType==FOCUS_NONE) {
			return null;
		}
		if ( focusType==FOCUS_ANY ) {
			return "any";
		}
		if ( focusType==FOCUS_LIST ) {
			return combineStrings(focusList);
		}
		throw new IllegalStateException("Invalid focus type");
	}
	
	/**
	 * @return xml encoding for this traits focus type
	 */
	protected String getFocusTypeXML() {
		if ( focusType==FOCUS_NONE ) {
			return null;
		} else if ( focusType==FOCUS_ANY ) {
			return "any";
		} else {
			//focusType==FOCUS_LIST ) 
			return combineStrings( focusList );
		}
		
	}
	
	/**
	 * Sets the skill options from the given string, which is usually acquired from the xml
	 * element for the Trait.
	 * @param ftype <ul><li>If null or zero-length the focus is SKILL_NONE.
	 * <li>If "any" the focus is SKILL_ANY.
	 * <li>If contains a "|" character then is SKILL_LIST.
	 * </ul>
	 *  three cases given. 
	 */
	public void setSkillAttribute( String stype ) {
		if ( stype==null || stype.length()==0 ) {
			skillType = SKILL_NONE;
 		} else if ( stype.equalsIgnoreCase("any")) {
 			skillType = SKILL_ANY ;
 		} else if ( stype.indexOf("|")!=-1) {
 			skillType = SKILL_LIST;
 			skillsList= splitStrings( stype ) ;
 		} else {
 			skillType = SKILL_PARENT;
 			skillsList = new String[]{ stype };
 		}
	}
	
	/**
	 * Convert the current string type back into strings for xml encoding.
	 * @return
	 */
	public String getSkillAttribute() {
		if ( skillType==SKILL_NONE ) {
			return null;
		} else if ( skillType==SKILL_ANY ) {
			return "any";
		} else if ( skillType==SKILL_LIST) {
			return combineStrings( skillsList );
		} else {
			return skillsList[0];
		}
	}
		
	/**
	 * Sets the Major/Minor flags from the xml attribute "type"
	 */
	public void setType(String type) {
		if ( type.equalsIgnoreCase("minor") ) {
			minor=true;
			major=false;
		} else if ( type.equalsIgnoreCase("major")) {
			major=true;
			minor=false;
		} else if ( type.equalsIgnoreCase("both")) {
			major=true;
			minor=true;
		} else {
			throw new IllegalArgumentException("Unrecognisable type string \""+type+"\".");
		}
	}
	
	protected String getMajorOrMinorXML() {
		if ( isMajor() && isMinor() ) {
			return "BOTH";
		} else if ( isMajor() ) {
			return "MAJOR";
		}
		return "MINOR";
	}

	/**
	 * @return Returns the list of skills (or parent skills) that can be 
	 * applied to this trait.
	 */
	public String[] getSkillsList() {
		return skillsList;
	}

	/**
	 * @param skillsList The the list of skills (or parent skills) that can be 
	 * applied to this trait.
	 */
	public void setSkillsList(String[] skillsList) {
		this.skillsList = skillsList;
	}

	/**
	 * @return Returns the type of skill association to this trait ( SKILL_NONE, SKILL_ANY, SKILL_LIST ).
	 */
	public int getSkillType() {
		return skillType;
	}

	/**
	 * @param skillType The skillType to set.
	 */
	public void setSkillType(int skillType) {
		this.skillType = skillType;
	}
	
	/**
	 * Construct a major only asset
	 * @param name
	 * @return
	 */
	public static Trait CreateMajorAsset(String name) {
		Trait res = CreateMajorTrait(name);
		res.setTraitType( TraitType.ASSET );
		return res;
	}
	
	/**
	 * Construct a minor only asset
	 * @param name
	 * @return
	 */
	public static Trait CreateMinorAsset(String name) {
		Trait res = CreateMinorTrait(name);
		res.setTraitType( TraitType.ASSET );
		return res;
	}
	
	/**
	 * Construct a major/minor asset
	 * @param name
	 * @return
	 */
	public static Trait CreateDualAsset(String name) {
		Trait res = CreateDualTrait(name);
		res.setTraitType( TraitType.ASSET );
		return res;
	}
	
	/**
	 * Construct a major only complication
	 * @param name
	 * @return
	 */
	public static Trait CreateMajorComplication(String name) {
		Trait res = CreateMajorTrait(name);
		res.setTraitType( TraitType.COMPLICATION );
		return res;
	}
	
	/**
	 * Construct a minor only complication
	 * @param name
	 * @return
	 */
	public static Trait CreateMinorComplication(String name) {
		Trait res = CreateMinorTrait(name);
		res.setTraitType( TraitType.COMPLICATION );
		return res;
	}
	
	/**
	 * Construct a major/minor complication
	 * @param name
	 * @return
	 */
	public static Trait CreateDualComplication(String name) {
		Trait res = CreateDualTrait(name);
		res.setTraitType( TraitType.COMPLICATION );
		return res;
	}
	
	/**
	 * Creates trait which is a Major only Complication.
	 * @param name
	 * @return
	 */
	public static Trait CreateMajorTrait(String name) {
		Trait res = new Trait();
		res.setMajor(true);
		res.setMinor(false);
		res.setName(name);
		return res;
	}
	
	/**
	 * Creates a trait which is a Minor only Complication.
	 * @param name
	 * @return
	 */
	public static Trait CreateMinorTrait(String name) {
		Trait res = new Trait();
		res.setMajor(true);
		res.setMinor(false);
		res.setName(name);
		return res;
	}
	
	/**
	 * Creates a trait which is a Major or Minor Complication.
	 * @param name
	 * @return
	 */
	public static Trait CreateDualTrait(String name) {
		Trait res = new Trait();
		res.setMajor(true);
		res.setMinor(true);
		res.setName(name);
		return res;
	}
	
	/**
	 * Creates a trait which is a Major or Minor Complication.
	 * @param name
	 * @return
	 */
	public static Trait CreateTraitFromXML(Element xml) {
		Trait res = new Trait();
		res.setXML( xml );
		return res;
	}
	
	public void setXML(Element xml) {
		type = ( xml.getName().equals("asset") ? TraitType.ASSET : TraitType.COMPLICATION );
		String nstr = xml.getAttributeValue("name");
		String tstr = xml.getAttributeValue("type");
		String fstr = xml.getAttributeValue("focus");
		String sstr = xml.getAttributeValue("skill");
		String dstr = xml.getText();
		if ( nstr==null ) {
			throw new IllegalArgumentException("No name attribute.");
		}
		if ( tstr==null ) {
			throw new IllegalArgumentException("No type attribute.");
		}
		setName(nstr);
		setType(tstr);
		setFocusAttribute( fstr );
		setSkillAttribute( sstr );
		setDescription( StringUtils.isEmpty(dstr) ? name : dstr );
	}
	
	/**
	 * Encode this trait as an XML element.
	 * @return
	 */
	public Element getXML() {
		String name = ( getType()==TraitType.ASSET ? "asset" : "complication" );
		Element ret = new Element( name );
		ret.setAttribute("name", getName() );
		
		//Major/minor
		ret.setAttribute("type", getMajorOrMinorXML() );
		
		String skillXML = getSkillAttribute();
		if ( !StringUtils.isEmpty( skillXML ) ) {
			ret.setAttribute( "skill", skillXML );
		}
		
		String focusXML = getFocusTypeXML();
		if ( !StringUtils.isEmpty( focusXML ) ) {
			ret.setAttribute( "focus", focusXML ) ;
		}
		if ( !StringUtils.isEmpty( getDescription() ) ) {
			ret.setText(getDescription());
		}
		return ret;
	}
	
	/**
	 * @return the type of trait
	 */
	public TraitType getType() {
		return type;
	}

	/**
	 * A long textual description of the trait, or null if the trait has no description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * A long textual description of the trait, or null if the trait has no description.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
	public String getTypeString() {
		if ( isMajor() && isMinor() ) {
			return "BOTH";
		} else if ( isMajor() ) {
			return "MAJOR";
		}
		return "MINOR";
	}
	
	public String getTraitInformation() {
		StringBuilder sb = new StringBuilder(245);
		sb.append( getName() );
		if ( isMajor() ) {
			sb.append(" MAJOR ");
		}
		if ( isMinor() ) {
			sb.append(" MINOR ");
		}
		if ( getSkillType()!=SKILL_NONE ) {
			sb.append("\n\tSkill=");
			sb.append(getSkillAttribute());
		}
		if ( getFocusType()!=FOCUS_NONE ) {
			sb.append("\n\tFocus=");
			sb.append(getFocusAttribute());
		}
		if ( description!=null ) {
			sb.append("\n");
			sb.append(description);
		}
		return sb.toString();
	}

	public void setTraitType(TraitType type) {
		this.type = type;
	}
}
