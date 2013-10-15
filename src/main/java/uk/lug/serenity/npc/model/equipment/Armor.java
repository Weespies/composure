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
public class Armor extends Equipment {
	private String rating = null;
	private int agilityMod = 0;
	private int alertnessMod = 0;
	private String covers=null;
	
	/**
	 * @return the covers
	 */
	public String getCovers() {
		return covers;
	}

	/**
	 * @param covers the covers to set
	 */
	public void setCovers(String covers) {
		this.covers = covers;
	}

	public Armor() {
		super();
	}
	
	/**
	 * Construct from element
	 * @param xml
	 */
	public Armor(Element xml) {
		setXML( xml );
	}
	
	/**
	 * Return this armor as an xml element.
	 */
	@Override
	public Element getXML() {
		Element xml = super.getXML();
		xml.setName("armor");
		if ( !StringUtils.isEmpty( rating ) ) {
			xml.setAttribute("rating", rating );
		}
		if ( agilityMod!=0 ) {
			xml.setAttribute("agility",Integer.toString(agilityMod) ) ;
		}
		if ( alertnessMod!=0 ) {
			xml.setAttribute("alertness",Integer.toString(alertnessMod) ) ;
		}
		if ( !StringUtils.isEmpty( rating ) ) {
			xml.setAttribute("rating",rating);
		}
		if ( !StringUtils.isEmpty( covers ) && !StringUtils.equals(covers,"none")) {
			xml.setAttribute("covers",covers);
		}
		return xml;
	}
	
	/**
	 * Set this armor from xml.
	 */
	@Override
	public void setXML( Element xml ) {
		super.setXML( xml );
		String aglStr = xml.getAttributeValue("agility");
		String artStr = xml.getAttributeValue("alertness");
		String rStr = xml.getAttributeValue("rating");
		String cStr = xml.getAttributeValue("covers");
		if ( StringUtils.isEmpty( aglStr ) ) {
			agilityMod=0;
		} else {
			agilityMod = Integer.parseInt( aglStr ) ;
		}
		if ( StringUtils.isEmpty( artStr ) ) {
			alertnessMod=0;
		} else {
			alertnessMod = Integer.parseInt( artStr );
		}
		if ( StringUtils.isEmpty( rStr ) ) {
			rating=null;
		} else {
			rating=rStr;
		}
		if ( StringUtils.isEmpty( cStr ) ) {
			covers=cStr;
		} else {
			covers="none";
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder(256);
		sb.append( name ) ;
		sb.append( " (");
		if ( !StringUtils.isEmpty(rating) ) {
			sb.append( rating );
			sb.append( " - ");
		}
		sb.append( availability.getDescription() );
		sb.append( ")");
		return sb.toString();
	}

	/**
	 * @return the agilityMod
	 */
	public int getAgilityMod() {
		return agilityMod;
	}

	/**
	 * @param agilityMod the agilityMod to set
	 */
	public void setAgilityMod(int agilityMod) {
		this.agilityMod = agilityMod;
	}

	/**
	 * @return the alertnessMod
	 */
	public int getAlertnessMod() {
		return alertnessMod;
	}

	/**
	 * @param alertnessMod the alertnessMod to set
	 */
	public void setAlertnessMod(int alertnessMod) {
		this.alertnessMod = alertnessMod;
	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	
}

