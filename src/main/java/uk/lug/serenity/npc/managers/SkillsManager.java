/**
 * 
 */
package uk.lug.serenity.npc.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.skills.Skill;


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
public class SkillsManager {
	private static final String SKILLS_RESOURCE="data/skills.xml";
	public static final File SKILLS_FILE = new File("skills.xml");
	private static Skill[] skills;
	
	static {
		try {
			readSkills();
		} catch (UnmarshallingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading skills data file.","XML Error",JOptionPane.ERROR_MESSAGE);	
		}
	}
	
	/**
	 * Read the asset list.
	 * @throws UnmarshallingException 
	 */
	@SuppressWarnings("unchecked")
	private static void readSkills() throws UnmarshallingException {
		List<Skill> list = (List<Skill>)LocalFileController.getInstance()
		.unmarshalResource("data/skills.xml","mapping/skillsMapping.xml");
		skills = list.toArray( new Skill[list.size()]);
	}
	
	/**
	 * Get a specific named skill.
	 * @param s
	 * @return
	 */
	public static Skill getNamedSkill(String s ) {
		for ( Skill skill : skills ) {
			if ( skill.getName().equals( s ) ) {
				return skill;
			}
		}
		return null;
	}
	
	/**
	 * Get a specific skill.
	 * @param generalSkill enumerated skill.
	 * @return
	 */
	public static Skill getSkill(GeneralSkill generalSkill) {
		for ( Skill skill : skills ) {
			if ( skill.getName().equals( generalSkill.getName() ) ) {
				return skill;
			}
		}
		return null;
	}
	
	/**
	 * Write the skills file back into the local file.
	 * @throws IOException
	 */
	public static void writeSkills() throws IOException {
		Document doc = new Document();
		Element root = new Element( "skills" );
		for ( Skill skill : skills ) {
			root.addContent( skill.getXML() ) ;
		}
		doc.setRootElement( root );
		XMLOutputter output = new XMLOutputter( Format.getPrettyFormat() );
		LocalFileController.getInstance().writeDocument( SKILLS_RESOURCE, doc );
		FileWriter writer = new FileWriter(SKILLS_FILE, false);
		output.output( doc , writer);
		writer.close();
	}
	
	/**
	 * Add a skill to the list.  It will not be written to the local file.
	 * @param newSkill
	 */
	public static void addSkill( Skill newSkill ) {
		Skill[] sa = new Skill[ skills.length+1 ];
		int ptr=0;
		for ( Skill skill : skills ) {
			sa[ptr++]=skill;
		}
		sa[ptr++]=newSkill;
		skills=sa;
	}
	
	/**
	 * @return Returns the assets.
	 */
	public static Skill[] getSkills() {
		return skills;
	}
	
	/**
	 * Return the list of all known child skills for the named general skill.
	 */
	public static String[] getChildrenFor(String name) {
		LinkedList<String> ret = new LinkedList<String>();
		for ( Skill sk : skills ) {
			if ( sk.getName().equals(name) ) {
				for ( String s : sk.getChildren() ) {
					ret.add( s );
				}
			}
		}
		return ret.toArray( new String[0]);
	}
	
	
	
	/**
	 * List of child skills for a given general skill
	 * @param parentName
	 * @return
	 */
	public static List<String> getChildrenFor(GeneralSkill parent) {
		List<String> ret = new Vector<String>();
		for ( Skill sk : skills ) {
			if ( sk.getName().equals(parent.getName()) ) {
				for ( String s : sk.getChildren() ) {
					ret.add( s );
				}
			}
		}
		return ret;
	}
}
