/**
 * 
 */
package uk.lug.serenity.npc.random.archetype;

import java.util.HashMap;
import java.util.List;

import org.jdom.Element;

import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.util.JDOMUtils;

/**
 * @author Luggy
 *
 */
public class StatBiasList extends HashMap<MainStat, Weighting> {
	private static final String WEIGHTING = "weighting";
	private static final String STAT = "stat";
	private static final String ELEMENT_NAME = "stats";
	private static final String STAT_CODE="stat";
	public static final int DEFAULT_WEIGHTING = 1;
	
	/**
	 * 
	 */
	public StatBiasList() {
		super(MainStat.count());
		for ( MainStat stat : MainStat.values() ) {
			put( stat, new Weighting(DEFAULT_WEIGHTING));
		}
	}
	
	public void setXML(Element xml) {
		List<Element> statList = JDOMUtils.getChildren(xml, STAT);
		
		for (Element e : statList ) {
			MainStat stat = MainStat.getForKey(e.getAttributeValue(STAT_CODE)); 
			Integer value = Integer.parseInt( e.getAttributeValue(WEIGHTING) );
			if ( containsKey(stat) ) {
				get(stat).setValue( value );
			} else {
				put(stat, new Weighting(value));
			}	
		}
	}
	
	public Element getXML() {
		Element xml = new Element(ELEMENT_NAME);
		
		for ( MainStat stat : MainStat.values() ) {
			Element e= new Element(STAT);
			e.setAttribute(STAT_CODE, stat.getKey() );
			e.setAttribute(WEIGHTING, Integer.toString( get(stat).getValue() ) );
			xml.addContent(e);
		}
		
		return xml;
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof StatBiasList) ) {
			return false;
		}
		StatBiasList other = (StatBiasList)o;
		if ( other.size()!=this.size() ) {
			return false;
		}
		for ( MainStat stat : MainStat.values() ) {
			if ( get(stat).getValue()!=other.get(stat).getValue() ) {
				return false;
			}
		}
		return true;
	}

	
}
