/**
 * 
 */
package uk.lug.serenity.npc.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import uk.lug.gui.archetype.skills.DirectoryDialog;

/**
 * $Id: This will be filled in on CVS commit $
 * 
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 *         <p>
 */
/**
 * <p>
 * Factory class that handles local filesystem storage of data files.
 * </p>
 * <p>
 * LocalFileController has 2 functions . First, when first run it asks the user
 * where to extract the data files to and storing this in the local
 * java.util.preferences system. Secondly it extracts the initial data resources
 * out of the application into the filesystem.
 * <p>
 * 
 * @author luggy
 */
public class LocalFileController {
	private static final int EXTRACT_BUFFER_SIZE = 1024*64;

	private static LocalFileController instance;

	private static Preferences prefs;
	private static File dataDirectory;

	private static final String ROOT_DIRECTORY = "serenityRoot";
	private static ClassLoader classLoader;
	private static final String APPLICATION_DIRECTORY = 
		"C:/Documents and Settings/All Users/Application Data";
	private static Unmarshaller unmarshaller;
	
	/**
	 * Construct the controller.
	 */
	private LocalFileController() {
		super();
//		LocalConfiguration.getInstance().getProperties().setProperty("org.exolab.castor.indent","true");
		classLoader = this.getClass().getClassLoader();
		prefs = Preferences.userNodeForPackage(LocalFileController.class);
		unmarshaller = new Unmarshaller();
		if (prefs.get(ROOT_DIRECTORY, null) == null) {
			firstTimeRun();
		} else {
			nonFirstTimeRun();
		}

	}

	/**
	 * Perform first time run activities.
	 */
	private void firstTimeRun() {
		chooseRootDir();
	}
	
	/**
	 * Perform run each time after the first .
	 * Checks the data file directory is there and offers
	 * choices if not.
	 */
	private void nonFirstTimeRun() {
		String dir = prefs.get(ROOT_DIRECTORY,null);
		if ( dir==null ) {
			firstTimeRun();
			return;
		}
		dataDirectory = new File( dir );
		if ( dataDirectory.exists() ) {
			//its all good.
			return;
		}
		
		//Cant find data store, what do I do?
		String msg = "Cannot find the data storage directory where it was expected \n(\""+dir+"\").\nHow do you wish to proceeed ?";
		String opt1 = "Quit the program";
		String opt2 = "Select a new data storage directory.";
		int ret = JOptionPane.showOptionDialog(null, msg, "Data store not found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null,  new String[]{opt1,opt2}, opt1); 	
		if ( ret==0 ) {
			System.exit(0);
		} else {
			firstTimeRun();
		}
	}

	/**
	 * Choose the root directory.
	 */
	private void chooseRootDir() {
		String message = "Composure needs a place " +
				"to store it's files locally.  You can either " +
				"accept the default as shown below or choose another directory.";
		File homedir = getDefaultDirectory();
		File chosenDir = DirectoryDialog.showDirectoryDialog(null, homedir, message, "Data Directory");
		if ( chosenDir==null ) {
			System.exit(0);
		}
		dataDirectory = chosenDir;
		prefs.put( ROOT_DIRECTORY, dataDirectory.getAbsolutePath() );
	}

	/**
	 * Construct the default directory.  Will try to make an application data folder but 
	 * if that fails will use the home folder.
	 * @return
	 */
	private File getDefaultDirectory() {
		File homedir = new File(System.getProperty("user.home"));
		if ( System.getProperty("os.name").indexOf("Windows")==-1 ) {
			return homedir;
		}
		File appDir = new File( System.getProperty("user.home")+File.separator+"composure" ) ;
		
		
		return homedir;
	}

	/**
	 * @return the instance
	 */
	public static LocalFileController getInstance() {
		if (instance == null) {
			instance = new LocalFileController();
		}
		return instance;
	}
	
	/**
	 * Translate a resource URI to the corresponding file
	 * in the storage directory.
	 * @param resourcename
	 * @return
	 */
	private File getLocalFile(String resource) {
		StringBuilder sb = new StringBuilder(128);
		sb.append( dataDirectory.getAbsolutePath() );
		sb.append( File.separatorChar );
		sb.append( getResourceName(resource) );
		return new File(sb.toString());
	}
	
	/**
	 * String everything out of a URI string except the name
	 * @param URI
	 * @return
	 */
	public static String getResourceName(String URI ) {
		int idx = StringUtils.lastIndexOf(URI,"/");
		if ( idx==-1 ) {
			return URI;
		}
		return URI.substring(idx+1, URI.length());
	}
	
	/**
	 * Checks to see if the given resource has been extracted into a local file.
	 * @param resource
	 * @return true if the resource has been extracted.
	 */
	private boolean resourceExtracted( String resource ) {
		return getLocalFile(resource).exists();
	}
	
	/**
	 * Extract the given resource into its local file.  
	 * This will overwrite any prior extracted file.
	 * @param resource 
	 * @throws IOException should anything go wrong with the extraction process.
	 */
	private void extractResource( String resource ) throws IOException {
		File resourceFile = getLocalFile( resource );
		InputStream inStream = classLoader.getResourceAsStream( resource );
		byte[] buffer = new byte[ EXTRACT_BUFFER_SIZE ];
		FileOutputStream outStream = new FileOutputStream( resourceFile, false );
		int len=0;
		
		while ( (len=inStream.read(buffer))!=-1 ) {
			outStream.write( buffer, 0, len);
		} 
		inStream.close();
		outStream.flush();
		outStream.close();
	}
	
	
	/**
	 * Read in a document from a local resource.
	 * @param resource resource to load
	 * @param validate true if the document must be validated when loaded.
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Document getDocumentResource(String resource, boolean validate) throws JDOMException, IOException {
		if ( !resourceExtracted(resource) ) {
			extractResource( resource );
		}
		File docFile = getLocalFile(resource);
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		Document doc = builder.build( docFile.toURL() );
		return doc;
	}
	
	/**
	 * Load a resource file and unmarhal the items into a list.
	 * @param resourceFile Resource file to load.
	 * @param mappingFile mapping file for the XML>POJO conversion.
	 * @return
	 * @throws UnmarshallingException
	 */
	public List<?> unmarshalResource(String resourceFile, String mappingFile) throws UnmarshallingException {
		try {
			URL resourceURL = classLoader.getResource(resourceFile);
			URL mappingURL = classLoader.getResource(mappingFile);
			
			Mapping resourceMapping = new Mapping();
			resourceMapping.loadMapping( mappingURL);
			unmarshaller.setClass(ArrayList.class);
			unmarshaller.setMapping( resourceMapping );
			
			InputSource source = new InputSource( resourceURL.toString() );
			return (List<?>)unmarshaller.unmarshal(source);
		} catch (IOException e) {
			throw new UnmarshallingException("IO unmarshalling from resource "+resourceFile,e);
		} catch (MappingException e) {
			throw new UnmarshallingException("Mapping error unmarshalling from resource "+resourceFile,e);
		} catch (MarshalException e) {
			throw new UnmarshallingException("Marshalling error from resource "+resourceFile,e);
		} catch (ValidationException e) {
			throw new UnmarshallingException("Validation error from resource "+resourceFile,e);
		} finally {
			
		}
	}
	
	/**
	 * Marshal resource back into a local file as the appropriate XML document.
	 * @param items list of items to marshal
	 * @param resource filename of the resoruce
	 * @param mappingFile mapping file for the list of elements.
	 * @param rootElement Root element for the document
	 * @param schemaLocation xsd schema locations
	 * @throws MarshallingException should the marshalling fail.
	 */
	public void marshalResource(List<?> items, String resource, String mappingFile, 
			String rootElement, String schemaLocation) throws MarshallingException {
		try {
			URL mappingURL = classLoader.getResource(mappingFile);
			Mapping resourceMapping = new Mapping();
			resourceMapping.loadMapping( mappingURL);
			
			File localFile = getLocalFile(resource);
			FileWriter writer = new FileWriter( localFile, false);
			
			Marshaller marshaller = new Marshaller(writer);
			marshaller.setEncoding("UTF-8");
			marshaller.setRootElement(rootElement);
			marshaller.setSchemaLocation(schemaLocation);
			marshaller.setMapping( resourceMapping );
			marshaller.setSuppressXSIType(true);
			marshaller.marshal( items );
			writer.close();
		} catch (Exception e) {
			throw new MarshallingException("Error marshalling items to list of \""+rootElement+"\".",e);
		} 
	}
	

	/**
	 * Write a document back into the appropriate locate resource file.
	 * @param skills_resource
	 * @param doc
	 * @throws IOException 
	 */
	public void writeDocument(String resource, Document doc) throws IOException {
		XMLOutputter output = new XMLOutputter( Format.getPrettyFormat() );
		File localFile = getLocalFile( resource );
		FileWriter writer = new FileWriter( localFile, false );
		output.output( doc , writer);
		writer.close();		
	}
}
