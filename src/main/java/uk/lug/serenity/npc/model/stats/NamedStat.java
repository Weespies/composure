/**
 * 
 */
package uk.lug.serenity.npc.model.stats;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.model.event.StatChangeEvent;
import uk.lug.serenity.npc.model.event.StatChangeListener;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Extension of StepStat with a name and an abbreviation.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class NamedStat extends StepStat implements Serializable {
	private static final long serialVersionUID = 1l;
	public static final String XML_KEY="stat";
	private String name = "Unnamed Stat";
	private String abbrevation = "N/A";
	private LinkedList<StatChangeListener> listeners = new LinkedList<StatChangeListener>();
	
	/**
	 * @param v
	 * @param name
	 * @param abbrevation
	 */
	public NamedStat(int v, String name, String abbrevation) {
		super(v);
		this.name = name;
		this.abbrevation = abbrevation;
	}
	
	/**
	 * @param i
	 * @param mainStat
	 */
	public NamedStat(int i, MainStat mainStat) {
		super(i);
		this.name = mainStat.getName();
		this.abbrevation = mainStat.getKey();
	}

	/**
	 * @param v
	 */
	public NamedStat(int v) {
		super(v);
	}
	
	/**
	 * Construct from an xml element.
	 */
	public NamedStat( Element xml ) {
		super(4);
		setXML( xml );
	}

	/**
	 * Test for equality.  Test is done on value , name and abbreviation.
	 */
	@Override
	public boolean equals(Object o ) {
		if ( !(o instanceof NamedStat ) ) {
			return false;
		}
		NamedStat nstat = (NamedStat)o;
		if ( nstat.getValue()!=getValue() ) {
			return false;
		}
		if ( !nstat.getName().equals( getName() ) ) {
			return false;
		}
		if ( !nstat.getAbbrevation().equals( getAbbrevation() ) ) {
			return false;
		}
		return true;
	}

	/**
	 * @return Returns the abbrevation.
	 */
	public String getAbbrevation() {
		return abbrevation;
	}

	/**
	 * @param abbrevation The abbrevation to set.
	 */
	public void setAbbrevation(String abbr) {
		if ( StringUtils.isEmpty(abbr) ) {
			throw new IllegalArgumentException("Abbreviation is null or empty.");
		} 
		this.abbrevation = abbr.toUpperCase();
		fireAllListeners( new StatChangeEvent(StatChangeEvent.EVENT_CHANGED , this , 0));
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
	public void setName(String nstr) {
		if ( StringUtils.isEmpty(nstr) ) {
			throw new IllegalArgumentException("Name is null or empty.");
		} 
		this.name = nstr;
		fireAllListeners( new StatChangeEvent(StatChangeEvent.EVENT_CHANGED , this , 0));
	}

	/**
	 * @return the stat as an XML element.
	 */
	public Element getXML() {
		Element xml= new Element(XML_KEY);
		
		xml.setAttribute("name",name);
		xml.setAttribute("value", Integer.toString(value));
		xml.setAttribute("short", abbrevation);
		
		return xml;
	}
	
	/**
	 * Set this stat from an xml element.
	 */
	public void setXML(Element xml) {
		String nstr = xml.getAttributeValue("name");
		String astr = xml.getAttributeValue("short");
		String vstr = xml.getAttributeValue("value");
		
		//Value
		if ( NumberUtils.isNumber( vstr ) ) {
			setValue( Integer.parseInt( vstr ) );
		} else {
			throw new IllegalArgumentException("Value \""+vstr+"\" is not a number.");
		}
		
		//Name & abbreviation
		setName( nstr );
		setAbbrevation( astr );
		fireAllListeners( new StatChangeEvent(StatChangeEvent.EVENT_CHANGED , this , 0));
	}
	
	/**
	 * Returns a String displaying both the points and the dice value.
	 */
	public String getDisplay() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( getValue() );
		sb.append( " = ");
		sb.append( StepStat.getDiceFor( getValue() ) );
		
		return sb.toString();
	}
	
	/**
	 * Add a listener to be notified of stat changes.
	 * @param scl
	 */
	public void addStatChangeListener( StatChangeListener scl ) {
		listeners.add( scl );
	}
	
	/**
	 * Remove a listener to no longer be notified of stat changes.
	 * @param scl
	 */	
	public void removeStatChangeListener( StatChangeListener scl ) {
		listeners.remove( scl );
	}
	
	/**
	 * Sets the value and notifies listeners of an increase or decrease change
	 */
	@Override
	public void setValue(int val) {
		int oldValue = getValue();
		super.setValue( val ) ;
		int evtType = ( oldValue>val ? StatChangeEvent.EVENT_DECREASED : StatChangeEvent.EVENT_INCREASED );
		StatChangeEvent sce = new StatChangeEvent( evtType, this, oldValue-val );
		fireAllListeners( sce );
		
	}
	
	/**
	 * Fire all listeners with an increase event.
	 */
	public void fireAllListeners(StatChangeEvent sce) {
		for ( StatChangeListener scl : listeners ) {
			scl.statChanged( sce );
		}
	}
}
