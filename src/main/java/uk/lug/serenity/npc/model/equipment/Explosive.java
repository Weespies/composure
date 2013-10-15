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
 * @author Luggy
 *
 */
public class Explosive extends Equipment {
	private String damage = null;
	private int rangeIncrement = 1;
	
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

	public Explosive() {
		super();
	}
	
	/**
	 * Construct from element
	 * @param xml
	 */
	public Explosive(Element xml) {
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
		String rangeStr = xml.getAttributeValue("range");
		damage = ( StringUtils.isEmpty( damStr ) ? null : damStr );
		rangeIncrement = ( StringUtils.isEmpty( rangeStr) ? 0 : Integer.parseInt( rangeStr ) ) ;
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
		sb.append( availability.getDescription() );
		sb.append( ")");
		return sb.toString();
	}
	
}
