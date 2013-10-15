/**
 * 
 */
package uk.lug.serenity.npc.model.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 10 Jul 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public enum MainStat {
	Agility		(0,"AGL","Agility"),
	Strength	(1,"STR","Strength"),
	Vitality	(2,"VIT","Vitality"),
	Alertness	(3,"ALT","Alertness"),
	Intelligence(4,"INT","Intelligence"),
	WillPower	(5,"WIL","Willpower");
	
	
	/**
	 * @return the code
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	private MainStat( int i, String k, String n) {
		key =k;
		name=n;
		index =i;
	}
	private String key;
	private String name;
	private int index;
	
	/**
	 * Check to see if a given string is a statkey's key.
	 * @param c
	 * @return
	 */
	public static boolean isStatCode(String c) {
		for ( MainStat key : values() ) {
			if ( key.getKey().equalsIgnoreCase(c) ) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	

	/**
	 * Get the statkey for a given index.
	 * @param idx
	 * @return
	 */
	public static MainStat getForIndex(int idx) {
		for ( MainStat key : values() ) {
			if ( key.getIndex()==idx ) {
				return key;
			}
		}
		return null;
	}
	
	/**
	 * Get the statkey for a given code.
	 * @param c
	 * @return
	 */
	public static MainStat getForKey(String c) {
		for ( MainStat key : values() ) {
			if ( key.getKey().equalsIgnoreCase(c) ) {
				return key;
			}
		}
		return null;
	}
	
	/**
	 * List of the keys for all stats.
	 * @return
	 */
	public static List<String> keys() {
		ArrayList<String> ret = new ArrayList<String>( MainStat.values().length ) ;
		for ( MainStat  stat : MainStat.values() ) {
			ret.add( stat.getKey() );
		}
		return ret;
	}
	
	/**
	 * List of the Names for all stats.
	 * @return
	 */
	public static List<String> getNames() {
		ArrayList<String> ret = new ArrayList<String>( MainStat.values().length ) ;
		for ( MainStat  stat : MainStat.values() ) {
			ret.add( stat.getName() );
		}
		return ret;
	}
	
	/**
	 * @return
	 */
	public static int count() {
		return MainStat.values().length;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
