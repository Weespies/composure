package uk.lug.util;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 *  Convienience class for use with jdom, primarily adding Java 5.0 typesafe enum 
 * compatibility.
 */
public class JDOMUtils {

	/**
	 * Return all children form an XML of a specified type.
	 * @param xml element to get the children from.
	 * @param name name of the children.
	 * @return a List<Element> containing all children (or an empty list).
	 */
	public static List<Element> getChildren(Element xml, String name) {
		if ( xml == null ) {
    		return new ArrayList<Element>();
    	}
        List children= xml.getChildren(name);
       	List<Element> ret = new ArrayList<Element>( children.size() );
        for ( Object o : children ) {
        	if ( o instanceof Element ) {
        		ret.add( (Element)o );
        	}
        }
        return ret;
	}
	
    /**
     * Uses the Element.getChildren and returns the result as an Element[]. 
     * @param xml
     * @param name of the child types.
     * @return
     */
    public static Element[] getChildArray(Element xml, String name) {
    	if ( xml == null ) {
    		return new Element[0];
    	}
        List children= xml.getChildren(name);
        Element[] ret = new Element[children.size()];
        for ( int i=0; i<children.size(); i++) {
        	ret[i]=(Element)children.get(i);
        }
        return ret;
    }
}
