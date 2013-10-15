/**
 * 
 */
package uk.lug.serenity.npc.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.util.JDOMUtils;
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
public class ArchetypesManager {
	private static final String DOCUMENT_ROOT = "archetypes";
	private static final String ARCHETYPE_RESOURCE="data/archetypes.xml";
	public static final File ARCHETYPE_FILE = new File("archetypes.xml");
	private static List<Archetype> archetypes;

	private static ArchetypesManager instance=null;
	
	public static ArchetypesManager getInstance() {
		if ( instance==null ) {
			try {
				instance = new ArchetypesManager();
			} catch (JDOMException e) {
				throw new IllegalStateException("Unable to initialise the archetypes manager.");
			} catch (IOException e) {
				throw new IllegalStateException("Unable to initialise the archetypes manager.");
			}
		}
		return instance;
	}
	
	
	static {
		try {
			readArchetypes();
		} catch (JDOMException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading archetypes data file.","XML Error",JOptionPane.ERROR_MESSAGE);	
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading archetypes data file.","XML Error",JOptionPane.ERROR_MESSAGE);	
		}
	}
	
	
	
	/**
	 * @throws UnmarshallingException 
	 * @throws IOException 
	 * @throws JDOMException 
	 * 
	 */
	public ArchetypesManager() throws JDOMException, IOException {
		super();
		readArchetypes();
	}


	/**
	 * Read the asset list.
	 * @throws UnmarshallingException 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@SuppressWarnings("unchecked")
	private static void readArchetypes() throws JDOMException, IOException {
		Document archetypeDocument = LocalFileController.getInstance().getDocumentResource( ARCHETYPE_RESOURCE,true );
		List<Element> content = JDOMUtils.getChildren( archetypeDocument.getRootElement(), "archetype" );
		archetypes = new ArrayList<Archetype>();
		
		for ( Element xml : content ) {
			Archetype arche = new Archetype();
			arche.setXML( xml );
			archetypes.add( arche );
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new ArchetypesManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * @return the archetypes
	 */
	public static List<Archetype> getArchetypes() {
		return archetypes;
	}

	/**
	 * Change the list of archetypes.
	 * @param listData
	 */
	public static void setArchtypesList(List<Archetype> listData) {
		archetypes.clear();
		archetypes.addAll( listData );
		Collections.sort( archetypes, new Comparator<Archetype>() {
			public int compare(Archetype o1, Archetype o2) {
				return o1.getName().compareTo( o2.getName() );
			}});
	}
	
	/**
	 * Write the archetypes list to the datafile.
	 * @throws IOException 
	 */
	public static void writeArchetypes() throws IOException {
		Document doc = new Document();
		Element root = new Element(DOCUMENT_ROOT);
		doc.setRootElement( root );
		for ( Archetype archetype : archetypes ) {
			root.addContent( archetype.getXML() );
		}
		LocalFileController.getInstance().writeDocument(ARCHETYPE_RESOURCE,doc);
		
	}


	/**
	 * @return the names of all the archetypes
	 */
	public static List<String> getAllNames() {
		List<String> names = new ArrayList<String>();
		for ( Archetype arche : getArchetypes() ) {
			names.add( arche.getName());
		}
		return names;
	}


	/**
	 * Return the named archetype, or null if no archetype is found.
	 * @param archetypeName
	 * @return
	 */
	public static Archetype getForName(String archetypeName) {
		for ( Archetype arche : getArchetypes() ) {
			if ( StringUtils.equals(arche.getName(), archetypeName ) ) {
				return arche;
			}
		}
		return null;
	}


	/**
	 * @return
	 */
	public static Archetype getRandom() {
		int i = RandomFactory.getRandom().nextInt( archetypes.size() );
		return archetypes.get(i);
	}
	
	public static Archetype getGeneric() {
		for ( Archetype arche : archetypes ) {
			if ( StringUtils.equalsIgnoreCase(arche.getName(), "generic") ) {
				return arche;
			}
		}
		throw new IllegalStateException("Generic archetype not present.");
	}


	/**
	 * 
	 */
	public static void refreshArchetypes() {
		List<Archetype> archetypesBackup = new ArrayList<Archetype>( archetypes );
		try {
			readArchetypes();
		} catch (JDOMException e) {
			archetypes = archetypesBackup;
		} catch (IOException e) {
			archetypes = archetypesBackup;
		}
	}
}
