/**
 * 
 */
package uk.lug.serenity.npc.managers;

import java.io.IOException;
import java.util.LinkedList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.lug.serenity.npc.random.SkillBias;
import uk.lug.serenity.npc.random.SkillProfile;
import uk.lug.util.JDOMUtils;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Manager for the skills profiles.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class SkillProfileManager {
	private static final String SKILLS_PROFILE_RESOURCE="data/skillProfiles.xml";
	private static Element profileList;
	
	static {
		try {
			loadResource();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read in a document from a local resource.
	 * @param resource
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	private static void loadResource() throws JDOMException, IOException {
		Document doc = LocalFileController.getInstance().getDocumentResource( SKILLS_PROFILE_RESOURCE,false);
		profileList =doc.getRootElement();
	}
	
	/**
	 * Return a named skillprofile.
	 * @param name
	 * @return
	 */
	public static SkillProfile getProfile( String name ) {
		Element[] children = JDOMUtils.getChildArray(profileList, "profile");
		for ( Element e : children ) {
			if (e.getAttributeValue("name").equalsIgnoreCase(name) ) {
				return buildProfile( e );
			}
		}
		
		
		return null;
	}

	/**
	 * Build the profile form an element.
	 * @param e
	 */
	private static SkillProfile buildProfile(Element e) {
		SkillProfile profile= new SkillProfile( e.getAttributeValue("name") );
		Element[] children = JDOMUtils.getChildArray( e, "skill" ) ;
		for ( Element skill : children )  {
			SkillBias bias = SkillBias.createFromXML( skill ) ;
			profile.addBias( bias );
		}
		return profile;
	}
	
	/**
	 * Return the names of all stored skill profiles.
	 */
	public static String[] getAllProfileNames() {
		LinkedList<String> list = new LinkedList<String>();
		for ( Element e : JDOMUtils.getChildArray(profileList, "profile") ) {
			list.add( e.getAttributeValue("name") ) ;
		}
		return list.toArray( new String[ list.size() ] );
	}
}
