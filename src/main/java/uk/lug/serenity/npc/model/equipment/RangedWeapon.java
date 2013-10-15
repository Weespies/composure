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
public class RangedWeapon extends Equipment {
	private String damage = null;
	private double rateOfFire = 1d;
	private int clipSize =1;
	private int rangeIncrement = 1;
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

	/**
	 * @return the clipSize
	 */
	public int getClipSize() {
		return clipSize;
	}

	/**
	 * @param clipSize the clipSize to set
	 */
	public void setClipSize(int clipSize) {
		this.clipSize = clipSize;
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

	/**
	 * @return the rangeIncrement
	 */
	public int getRangeIncrement() {
		return rangeIncrement;
	}

	/**
	 * @param rangeIncrement the rangeIncrement to set
	 */
	public void setRangeIncrement(int rangeIncrement) {
		this.rangeIncrement = rangeIncrement;
	}

	/**
	 * @return the rateOfFire
	 */
	public double getRateOfFire() {
		return rateOfFire;
	}

	/**
	 * @param rateOfFire the rateOfFire to set
	 */
	public void setRateOfFire(double rateOfFire) {
		this.rateOfFire = rateOfFire;
	}

	public RangedWeapon() {
		super();
	}
	
	/**
	 * Construct from element
	 * @param xml
	 */
	public RangedWeapon(Element xml) {
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
		if ( !StringUtils.isEmpty( skill ) ) {
			xml.setAttribute("skill", skill);
		}
		
		xml.setAttribute("rate", Double.toString(rateOfFire) );
		xml.setAttribute("clip", Integer.toString( clipSize ) );
		xml.setAttribute("range", Integer.toString( rangeIncrement ) );
		return xml;
	}
	
	/**
	 * Set this armor from xml.
	 */
	@Override
	public void setXML( Element xml ) {
		super.setXML( xml );
		String damStr = xml.getAttributeValue("damage");
		String rateStr = xml.getAttributeValue("rate");
		String clipStr = xml.getAttributeValue("clip");
		String rangeStr = xml.getAttributeValue("range");
		String skillStr = xml.getAttributeValue("skill");
		damage = ( StringUtils.isEmpty( damStr ) ? null : damStr );
		rateOfFire = ( StringUtils.isEmpty( rateStr ) ? 1 : Double.parseDouble( rateStr ) ) ;
		clipSize = ( StringUtils.isEmpty( clipStr ) ? 1 : Integer.parseInt( clipStr ) ) ;
		rangeIncrement = ( StringUtils.isEmpty( rangeStr) ? 0 : Integer.parseInt( rangeStr ) ) ;
		skill = ( StringUtils.isEmpty( skillStr ) ? null : skillStr );
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
		if ( rangeIncrement>0 ) {
			sb.append( rangeIncrement ) ;
			sb.append("ft ");
		}
		if ( rateOfFire!=1 ) {
			sb.append("RoF=");
			sb.append( rateOfFire );
			sb.append(" ");
		}
		if ( clipSize>1 ) {
			sb.append("clip=");
			sb.append(clipSize);
			sb.append(" ");
		}
		sb.append( availability.getDescription() );
		sb.append( ")");
		return sb.toString();
	}
	
}
