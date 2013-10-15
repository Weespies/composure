/**
 * 
 */
package uk.lug.serenity.npc.model.traits;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.data.IDescribed;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Data regarding traits as applied to characters.</p>
 * @author Luggy
 *
 */
public class TraitData implements IDescribed {
	public static String XML_KEY = "traitData";
	private String traitName=null;
	public static final int MINOR_TRAIT = 2;
	public static final int MAJOR_TRAIT = 4;
	private int traitLevel = MINOR_TRAIT;
	public String focus=null;
	public String skill=null;
	private TraitType type = TraitType.UNKNOWN;
	private String description;
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	public TraitType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TraitType type) {
		this.type = type;
	}

	/**
	 * Equality test
	 */
	@Override
	public boolean equals(Object o ) {
		if ( !(o instanceof TraitData) ) {
			return false;
		}
		TraitData data = (TraitData)o;
		if ( !StringUtils.equals( getName(), data.getName() ) ) {
			return false;
		}
		if ( !StringUtils.equals( getFocus(), data.getFocus() ) )  {
			return false;
		}
		if ( !StringUtils.equals( getSkill(), data.getSkill() ) )  {
			return false;
		}
		if ( getTraitLevel()!=data.getTraitLevel()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Save this TraitData as an XML element.
	 */
	public Element getXML() {
		Element xml = new Element(XML_KEY);
		
		//name
		xml.setAttribute("name",traitName);
		xml.setAttribute("level" , ( traitLevel==MINOR_TRAIT ? "minor" : "major" ) ) ;
		if ( focus!=null ) {
			xml.setAttribute("focus" , focus);
		}
		if ( skill!=null ) {
			xml.setAttribute("skill" , skill);	
		}
		
		return xml;
	}
	
	/**
	 * Set the data for this TraitData from an xml element.
	 */
	public void setXML( Element xml ) {
		if ( !xml.getName().equals(XML_KEY)) {
			throw new IllegalArgumentException("Unexpected element \""+xml.getName()+"\" instead of \""+XML_KEY+"\"");
		}
		String nstr = xml.getAttributeValue("name");
		String tstr = xml.getAttributeValue("level");
		skill = xml.getAttributeValue("skill");
		focus = xml.getAttributeValue("focus");
		if ( StringUtils.isEmpty(nstr) ) {
			throw new IllegalArgumentException("Name attribute is missing or empty.");
		}
		if ( StringUtils.isEmpty(tstr) ) {
			throw new IllegalArgumentException("Type attribute is missing or empty.");
		}
		setTraitName( nstr );
		traitLevel = (tstr.equalsIgnoreCase("major") ? MAJOR_TRAIT : MINOR_TRAIT ) ;
	}
	
	/**
	 * Create a TraitData from the given XML.
	 */
	public static TraitData createTraitData(Element xml) {
		TraitData res= new TraitData() ;
		res.setXML( xml ) ;
		return res;
	}
	
	public static TraitData createMinorTrait(String name, boolean asset) {
		TraitData res= new TraitData();
		res.setName( name ) ;
		res.setMajor(false);
		res.setType( asset ? TraitType.ASSET : TraitType.COMPLICATION );
		return res;
	}
	
	public static TraitData createMinorTrait(Trait aTrait ) {
		TraitData res= new TraitData();
		res.setName( aTrait.getName() );
		res.setDescription( aTrait.getDescription() );
		res.setMajor( false ) ;
		res.setType( aTrait.getType() );
		return res;
	}
	
	public static TraitData createMajorTrait(Trait aTrait ) {
		TraitData res= new TraitData();
		res.setName( aTrait.getName() );
		res.setDescription( aTrait.getDescription() );
		res.setMajor( true ) ;
		res.setType( aTrait.getType() );
		return res;
	}
	
	public static TraitData createMajorTrait(String name, boolean asset) {
		TraitData res= new TraitData();
		res.setName( name ) ;
		res.setMajor(true);
		res.setType( asset ? TraitType.ASSET : TraitType.COMPLICATION );
		return res;
	}
	
	/**
	 * @return Returns the focus.
	 */
	public String getFocus() {
		return focus;
	}
	
	/**
	 * @param focus The focus to set.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}
	
	/**
	 * @return Returns the skill.
	 */
	public String getSkill() {
		return skill;
	}
	
	/**
	 * @param skill The skill to set.
	 */
	public void setSkill(String skill) {
		this.skill = skill;
	}
	
	/**
	 * @return Returns the traitLevel.
	 */
	public int getTraitLevel() {
		return traitLevel;
	}
	
	/**
	 * @param traitLevel The traitLevel to set.
	 */
	public void setTraitLevel(int traitLevel) {
		this.traitLevel = traitLevel;
	}
	
	/**
	 * @return Returns the traitName.
	 */
	public String getTraitName() {
		return traitName;
	}
	
	/**
	 * @param traitName The traitName to set.
	 */
	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}
	
	/**
	 * @param traitName The traitName to set.
	 */
	public void setName(String traitName) {
		this.traitName = traitName;
	}
	
	/**
	 * @return Returns the traitName.
	 */
	public String getName() {
		return traitName;
	}
	
	public void setMajor( boolean b ) {
		traitLevel = (b ? MAJOR_TRAIT : MINOR_TRAIT );
	}
	
	
	public boolean isMajor() {
		return traitLevel==MAJOR_TRAIT;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( getName() );
		if ( getFocus()!=null ) {
			sb.append(" - ");
			sb.append( getFocus() );
		}
		sb.append( " (" );
		sb.append( isMajor() ? "Major" : "Minor" );
		sb.append( ")");
		return sb.toString();
	}
	
	/**
	 * @return Return the cost of this trait (2 for major, 4 for minor).
	 */
	public int getCost() {
		return ( isMajor() ? MAJOR_TRAIT : MINOR_TRAIT );
	}
}
