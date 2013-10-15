/**
 * 
 */
package uk.lug.serenity.npc.random;

import java.util.Random;

import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.util.RandomFactory;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Stores main stat profiles to be used as a the bias base
 * for the creation of random main stats.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public enum StatProfile {
	TANK		("Tank", 8, 12, 10, 4, 4, 4),
	ASSASSIN	("Assassin", 12, 8, 4, 4, 8, 6),
	GUARD		("Guard", 8,8,12,6,4,4),
	GENERIC		("Generic", 8,6,8,6,8,6),
	LEADER		("Leader", 6,8,6,10,8,10),
	PILOT		("Pilot",12,6,6,6,6,6),
	TECH		("Technical", 6,4,6,8,12,6),
	MEDIC		("Medic", 6,4,6,8,12,6),
	DIPLOMAT	("Diplomat", 6,4,6,10,6,10);

	private String name;
	
	private int[] statValues;
	
	
	private StatProfile(String pname, int agl, int str, int vit, int alt, int intl, int wil) {
		name=pname;
		statValues = new int[6];
		statValues[0]=agl;
		statValues[1]=str;
		statValues[2]=vit;
		statValues[3]=alt;
		statValues[4]=intl;
		statValues[5]=wil;
	}
	
	public static StatProfile getForName( String s ) {
		for ( StatProfile sp : StatProfile.values() ) {
			if ( sp.getName().equals( s ) ) {
				return sp;
			}
		}
		return null;
	}
	
	/**
	 * Get the stat value for the named stat.
	 * @param statName
	 * @return
	 */
	public int getValueOf(String statName) {
		for (int i=0;i<MainStat.count();i++) {
			if ( statName.equalsIgnoreCase( MainStat.getForIndex(i).getKey() ) ) {
				return statValues[i];
			}
		}
		throw new IllegalArgumentException("Cannot find stat \""+statName+"\"");
	}
	
	/**
	 * Return the total points. By deducting 24 (6*4) you can get the number of 
	 * attribute points to reach this total.
	 * @return
	 */
	public int getTotal() {
		int tot=0;
		for ( int i=0;i<statValues.length;i++) {
			int sv = statValues[i];
			tot=tot+(sv);
		}
		return tot;
	}
	
	/**
	 * Returns a random stat biased towards those of higher value with the current profile.
	 * @return
	 */
	public String getRandomStat() {
		Random random= RandomFactory.getRandom();
		int choice = random.nextInt(getTotal());
		for ( int i=0;i<statValues.length;i++) {
			if (choice<=statValues[i]) {
				return MainStat.getForIndex(i).getKey();
			}
			choice=choice-(statValues[i]);
		}
		return MainStat.WillPower.getKey();
	}
	
	/**
	 * Return a random profile.
	 * @return
	 */
	public static StatProfile getRandom() {
		Random random= RandomFactory.getRandom();
		return StatProfile.values()[ random.nextInt( StatProfile.values().length ) ];
	}
	
	/**
	 * Reutrn the names of all profiles
	 */
	public static String[] getAllNames() {
		String[] res = new String[ StatProfile.values().length ];
		for ( int i=0;i<res.length;i++ ) {
			res[i] = StatProfile.values()[i].getName();
		}
		return res;
	}
	
	public static void main(String[] args) {
		for (int c=0;c<6;c++) {
			StatProfile prof = StatProfile.getRandom();
			System.out.print( prof.getName()+ " : " );
			for ( int i=0;i<10;i++) {
				String s = prof.getRandomStat();
				System.out.print( s+ " : ");	
			}
		}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
}
