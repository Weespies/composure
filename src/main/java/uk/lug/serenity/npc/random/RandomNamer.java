/**
 * 
 */
package uk.lug.serenity.npc.random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import uk.lug.serenity.npc.model.stats.StepStat;
import uk.lug.util.RandomFactory;

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
public class RandomNamer {
	private static final String RESOURCE_MALE_FIRST= "data/NamesMaleFirst.txt";
	private static final String RESOURCE_FEMALE_FIRST = "data/NamesFemaleFirst.txt";
	private static final String RESOURCE_LAST = "data/NamesLast.txt";
	private static String[] MALE_FIRST_NAMES;
	private static String[] FEMALE_FIRST_NAMES;
	private static String[] LAST_NAMES;
	private static Random random= RandomFactory.getRandom();
	
	/**
	 * Load resources at class first load.
	 */
	static {
		try {
			MALE_FIRST_NAMES = getAllLines( RESOURCE_MALE_FIRST );
		} catch (IOException ie1) {
			String msg="An IO Exception occured reading the male first names resource.\n";
			msg=msg+"Application will now terminate.";
			msg=msg+"Specific error : "+ie1.getMessage();
		}
		try {
			FEMALE_FIRST_NAMES = getAllLines( RESOURCE_FEMALE_FIRST );
		} catch (IOException ie1) {
			String msg="An IO Exception occured reading the female first names resource.\n";
			msg=msg+"Application will now terminate.";
			msg=msg+"Specific error : "+ie1.getMessage();
		}
		try {
			LAST_NAMES = getAllLines( RESOURCE_LAST );
		} catch (IOException ie1) {
			String msg="An IO Exception occured reading the last names resource.\n";
			msg=msg+"Application will now terminate.";
			msg=msg+"Specific error : "+ie1.getMessage();
		}
	}
	
	/**
	 * Returns all ascii lines in the given resource file;
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static String[] getAllLines(String resource) throws IOException {
		InputStream instream = new StepStat(4).getClass().getClassLoader()
		.getResourceAsStream( resource );
		
		InputStreamReader inreader = new InputStreamReader( instream );
		BufferedReader breader = new BufferedReader( inreader );
		LinkedList<String> res = new LinkedList<String>();
		String inline;
		while ( (inline=breader.readLine())!=null ) {
			res.add( inline );
		}
		
		return res.toArray( new String[res.size()] );
	}
	
	/**
	 * Return a random male first name.
	 */
	public static String getRandomMale() {
		int x = random.nextInt( MALE_FIRST_NAMES.length );
		return StringUtils.capitalize( MALE_FIRST_NAMES[x].toLowerCase() );
	}
	
	/**
	 * Return a random female first name.
	 */
	public static String getRandomFemale() {
		int x = random.nextInt( FEMALE_FIRST_NAMES.length );
		return StringUtils.capitalize( FEMALE_FIRST_NAMES[x].toLowerCase() );
	}
	
	/**
	 * Return a random last name.
	 */
	public static String getRandomLast() {
		int x = random.nextInt( LAST_NAMES.length );
		String res = StringUtils.capitalize( LAST_NAMES[x].toLowerCase() );
		
		if ( res.startsWith("Mc") ) {
			//Ach! It's scottish ! 
			res = res.substring(2,res.length());
			res="Mc"+StringUtils.capitalize( res.toLowerCase() );
		}
		return res;
	}
	
	/**
	 * Return a random name with 1 to 3 first names (75% chance of 1 , 20% of 2, 5% change of 3).
	 * @param male TRUE for a male name, false for a female.
	 * @return
	 */
	public static String getRandomName(boolean male) {
		//How many first names
		int n = random.nextInt(100);
		int firsts =1;
		if ( n>75 && n<96 ) {
			firsts =2;
		} else if (n>97) {
			firsts =3 ;
		}
		
		StringBuilder sb = new StringBuilder();
		
		//First names
		for ( int i=0;i<firsts;i++) {
			if (male) {
				sb.append( getRandomMale() );
				sb.append(" ");
			} else {
				sb.append( getRandomFemale() );
				sb.append(" ");
			}
		}
		
		//Last Name
		sb.append( getRandomLast() );
		return sb.toString();
	}
}
