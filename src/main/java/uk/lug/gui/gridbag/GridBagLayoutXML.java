package uk.lug.gui.gridbag;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Extension of the GridBagLayout which allows you to use an XML
 * Element as source of settings for GridBagConstraints.*/
public class GridBagLayoutXML extends GridBagLayout {
	private static final String elementName="gridBagLayout";
	private Element layout = null;
	private GridBagParser parser = null;

	/**
	 * Create an empty GridBagLayoutXML.*/
	public GridBagLayoutXML() {
		super();
	}

	/**
	 * Construct a GridBagLayoutXML from the given element.
	 * @param e JDOM element of type "gridbaglayout" containing the layout information to use.
	 * @throws GridBagExcpetion should the reader be invalid, unusable or the layout document not be valid.*/
	public GridBagLayoutXML(Element e) throws GridBagException {
		super();
		if (e.getName().equals(elementName)==false) {
			throw new GridBagException("Element supplied not of type \""+elementName+"\" , it's \""+e.getName()+"\".");
		}
		layout=e;
		parser = new GridBagParser(layout);
	}

	/**
	 * Construct a from a file containing a gridbag layout.
	 * @param f File which contains the layout document.
	 * @throws GridBagExcpetion should the reader be invalid, unusable or the layout document not be valid.*/
	public GridBagLayoutXML(File f) throws GridBagException {
		super();
        //Do we have a better option ?
        String fp = f.getAbsolutePath();
        fp = StringUtils.replace(fp,".xml",".gbc");
        if (new File(fp).exists()) {
                parser = new AsciiParser(new File(fp));
        } else {
    		try {
    			parser = new GridBagParser(f);
    
    		} catch (IOException ie1) {
    			throw new GridBagException (ie1);
    		} catch (JDOMException jde1) {
    			throw new GridBagException (jde1);
    		}
        }
	}
    

	/**
	 * Construct a from an inputstream.
	 * @param in InputStream to use as the source fo the layout document.
	 * @throws GridBagExcpetion should the reader be invalid, unusable or the layout document not be valid.*/
	public GridBagLayoutXML(InputStream in) throws GridBagException {
		super();
		if ( in==null ){
			throw new IllegalArgumentException("Input stream is null.");
		}
		try {
			parser = new GridBagParser(in);

		} catch (IOException ie1) {
			throw new GridBagException (ie1);
		} catch (JDOMException jde1) {
			throw new GridBagException (jde1);
		}
	}

	/**
	 * Construct a from a reader.
	 * @param r Reader to use as the source of the layout document.
	 * @throws GridBagExcpetion should the reader be invalid, unusable or the layout document not be valid.*/
	public GridBagLayoutXML(Reader r) throws GridBagException {
		super();
		try {
			parser = new GridBagParser(r);

		} catch (IOException ie1) {
			throw new GridBagException (ie1);
		} catch (JDOMException jde1) {
			throw new GridBagException (jde1);
		}
	}


	/**
	 * Construct from a URI.
	 * @param uri URI indicating where to get the layout document.
	 * @throws GridBagExcpetion should the URI be invalid, unreadable or the layout document contain an error.*/
	public GridBagLayoutXML(String uri) throws GridBagException {
		super();
        
		try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(uri);
            Element e = doc.getRootElement();
            parser = new GridBagParser(e);
		} catch (IOException ie1) {
			throw new GridBagException (ie1);
		} catch (JDOMException jde1) {
			throw new GridBagException (jde1);
		}
	}

	/**
	 * Construct from a URL.
	 * @param url URL containing the document to use for layout information.
	 * @throws GridBagException if the URL cannot be found, read or does not contain
	 * sane data.*/
	public GridBagLayoutXML(URL url) throws GridBagException {
		super();
		try {
			parser = new GridBagParser(url);

		} catch (IOException ie1) {
			throw new GridBagException (ie1);
		} catch (JDOMException jde1) {
			throw new GridBagException (jde1);
		}
	}

	/**
	 * Set's the constraints for the specified component in this layout.
	 * @param comp the component to be modified.
	 * @param name the component name is it appears in the layout document.
	 * @throws GridBagException if name does not exist or is malformed.*/
	public void setConstraints(Component comp, String name) throws GridBagException {
		GridBagConstraints c= parser.parse(name);
		if (c==null) {
			throw new GridBagException("Component layout data for \""+name+"\" not found in layout document.");
		}
        comp.setName(name);
		super.setConstraints(comp,c);
	}

	/**
	 * Convienience method for loading URI based layout documents.
	 * @param uri URI indicating where to get the layout document.
	 */
	public static Element getLayout(String uri) throws JDOMException,IOException {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		URL url = loader.getResource(uri);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(url);
		return doc.getRootElement();
	}

	/**
	 * Returns the GridBagConstraints for the named component.
	 * @return an appropriately configured GridBagConstraints or null should the name component not exist within the layout document.*/
	public GridBagConstraints getConstraints(String n) throws GridBagException {
		return parser.parse(n);
	}

	/**
	 * Returns the GridBagConstraints for the named component.
	 * @return an appropriately configured GridBagConstraints or null should the name component not exist within the layout document.*/
	public GridBagConstraints place(String n) throws GridBagException {
		return parser.parse(n);
	}
    
    /**
     * Returns the names of all components in the parser.
     */
    public String[] getComponentNames() {
        return parser.getComponentNames();
    }

}