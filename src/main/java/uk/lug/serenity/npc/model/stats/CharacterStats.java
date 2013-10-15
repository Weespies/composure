/**
 * 
 */
package uk.lug.serenity.npc.model.stats;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Set;

import org.jdom.Element;

import uk.lug.serenity.npc.model.event.StatChangeEvent;
import uk.lug.serenity.npc.model.event.StatChangeListener;
import uk.lug.util.JDOMUtils;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Represents the three physical stats and the three mental stats.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class CharacterStats implements StatChangeListener {
	public static final String XML_KEY ="mainStats";

	public HashMap<String, NamedStat> mainStats;
	private PropertyChangeSupport propertiesSupport = new PropertyChangeSupport(this);
	
	private int lastTotal=0;
	
	public interface properties {
		public static final String TOTAL_PROPERTY = "TOTAL"; 
	}
	
	
	
	/**
	 * @return Returns the mainStats.
	 */
	public HashMap<String, NamedStat> getMainStats() {
		return mainStats;
	}
	
	/**
	 * Create the empty stats list with all 6 stats at value 4 (D4).
	 */
	public CharacterStats() {
		initialise();
	}
	
	/**
	 * Equality test, based on each stat .  Used by the JUnit test.
	 * A MainStats object is considered equal if all 6 stats are present in both
	 * and all 6 stats match using their own equals() method.
	 */
	@Override
	public boolean equals(Object o ) {
		if ( ! (o instanceof CharacterStats ) ) {
			return false;
		}
		CharacterStats test = (CharacterStats)o;
		for ( String s : MainStat.keys() ) {
			NamedStat stat1 = getStat( s );
			NamedStat stat2 = test.getStat( s );
			if ( stat1==null || stat2==null ) {
				return false;
			}
			if ( !stat1.equals( stat2 ) ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Initialise the hashmap if required
	 */
	public void initialise() {
		if ( mainStats==null ) {
			mainStats = new HashMap<String, NamedStat>();
			for ( int i=0;i<MainStat.count(); i++) {
				NamedStat stat = new NamedStat( 4 , MainStat.values()[i]);
				mainStats.put( MainStat.getForIndex(i).getKey(), stat );
				stat.addStatChangeListener( this );
			}
		}
		lastTotal = 24;
	}
	
	/**
	 * Reset all stats back to 4.
	 */
	public void reset() {
		for ( String s : getMapKeys() ) {
			mainStats.get( s ).setValue( 4 );
		}
		propertiesSupport.firePropertyChange( properties.TOTAL_PROPERTY , lastTotal, 24);
		lastTotal = 24;
		
	}
	
	/**
	 * return the stat list hashmap's keys as an array.
	 */
	public String[] getMapKeys() {
		Set<String> set = mainStats.keySet();
		return set.toArray( new String[6]);
	}
	
	/**
	 * Return a stat from its short name key.
	 * @param key
	 * @return NULL if stat cannot be found.
	 */
	public NamedStat getStat( String key ) {
		return mainStats.get( key );
	}
	
	/**
	 * Return the XML configuration for all 6 stats.
	 * @return
	 */
	public Element getXML() {
		Element xml = new Element(XML_KEY);
		for (String s : MainStat.keys() ) {
			xml.addContent( mainStats.get(s).getXML() );
		}
		return xml;
	}
	
	/**
	 * Set all 6 stats from an xml element.
	 * @param xml
	 */
	public void setXML( Element xml) {
		Element[] elems = JDOMUtils.getChildArray( xml , NamedStat.XML_KEY );
		for ( Element e : elems ) {
			String s = e.getAttributeValue("short");
			mainStats.get( s ).setXML( e );
		}
	}
	
	/**
	 * Return the total number of points used by all 6 stats.
	 */
	public int getTotalPoints() {
		int pts =0;
		for (String s : MainStat.keys() ) {
			pts=pts+ mainStats.get( s ).getValue();
		}
		return pts;
	}

	/* (non-Javadoc)
	 * @see lug.serenity.event.StatChangeListener#statIncreased(lug.serenity.event.StatChangeEvent, int)
	 */
	@SuppressWarnings("unused")
	public void statIncreased(StatChangeEvent sce, int increasedBy) {
		propertiesSupport.firePropertyChange( properties.TOTAL_PROPERTY , lastTotal, getTotalPoints() );
		lastTotal = getTotalPoints();
		
	}

	/* (non-Javadoc)
	 * @see lug.serenity.event.StatChangeListener#statDecreased(lug.serenity.event.StatChangeEvent, int)
	 */
	@SuppressWarnings("unused")
	public void statDecreased(StatChangeEvent sce, int decreasedBy) {
		propertiesSupport.firePropertyChange( properties.TOTAL_PROPERTY , lastTotal, getTotalPoints() );
		lastTotal = getTotalPoints();
	}

	/* (non-Javadoc)
	 * @see lug.serenity.event.StatChangeListener#statChanged(lug.serenity.event.StatChangeEvent)
	 */
	public void statChanged(StatChangeEvent sce) {
		if ( lastTotal!=getTotalPoints() ) {
			propertiesSupport.firePropertyChange( properties.TOTAL_PROPERTY , lastTotal, getTotalPoints() );
			lastTotal = getTotalPoints();
		}
	}
	
	/**
	 * Adds a property change listener.
	 * @param pcl
	 */
	public void addPropertyChangeListener( PropertyChangeListener pcl ) {
		propertiesSupport.addPropertyChangeListener( pcl );
	}
	
	/**
	 * Removes a property change listener.
	 * @param pcl
	 */
	public void removePropertyChangeListener( PropertyChangeListener pcl ) {
		propertiesSupport.removePropertyChangeListener( pcl );
	}

	
	/**
	 * Lower all stats down to 4.
	 */
	public void clearAllStats() {
		for ( String key : MainStat.keys() ) {
			getStat( key ).setValue(4);
		}
	}
}
