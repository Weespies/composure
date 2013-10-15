/**
 * 
 */
package uk.lug.serenity.npc.model.equipment;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

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
public class MeleeWeapon extends Equipment {
	private String damage = null;
	private String skill=null;
	
	/**
	 * @return the skill
	 */
	public String getSkill() {
		return skill;
	}

	/**
	 * @param skill the skill to set
	 */
	public void setSkill(String skill) {
		this.skill = skill;
	}

	public MeleeWeapon() {
		super();
	}
	
	/**
	 * Construct from element
	 * @param xml
	 */
	public MeleeWeapon(Element xml) {
		setXML( xml );
	}
	
	/**
	 * Return this armor as an xml element.
	 */
	@Override
	public Element getXML() {
		Element xml = super.getXML();
		xml.setName("weapon");
		if ( !StringUtils.isEmpty( damage ) ) {
			xml.setAttribute("damage", damage );
		}
		if ( !StringUtils.isEmpty(skill)) {
			xml.setAttribute("skill",skill);
		}
		return xml;
	}
	
	/**
	 * Set this armor from xml.
	 */
	@Override
	public void setXML( Element xml ) {
		super.setXML( xml );
		String rStr = xml.getAttributeValue("damage");
		String sStr = xml.getAttributeValue("skill");
		if ( StringUtils.isEmpty( rStr ) ) {
			damage=null;
		} else {
			damage=rStr;
		}
		if ( StringUtils.isEmpty( sStr ) ) {
			skill=null;
		} else {
			skill=sStr;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder(256);
		sb.append( name ) ;
		sb.append( " (");
		if ( !StringUtils.isEmpty(damage) ) {
			sb.append( damage );
			sb.append( " - ");
		}
		sb.append( availability.getDescription() );
		sb.append( ")");
		return sb.toString();
	}

	/**
	 * @return the damage
	 */
	public String getDamage() {
		return damage;
	}

	/**
	 * @param damage the damage to set
	 */
	public void setDamage(String damage) {
		this.damage = damage;
	}
	
}
