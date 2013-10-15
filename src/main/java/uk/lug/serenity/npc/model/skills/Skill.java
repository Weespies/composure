/**
 * 
 */
package uk.lug.serenity.npc.model.skills;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.MutableList;
import uk.lug.util.JDOMUtils;

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
public class Skill {
	public static final String XML_KEY="skill";
	private String name =null;
	protected MutableList<String> children = new MutableList<String>();
	private boolean skilledOnly=false;
	private MutableList<Specialty> specialtyList;
	
	/**
	 * Read xml
	 * @param xml
	 */
	public void setXML( Element xml ) {
		if ( !xml.getName().equals( XML_KEY ) ) {
			throw new IllegalArgumentException("Element not of type \""+XML_KEY+"\".");
		}
		String nstr = xml.getAttributeValue("name");
		if ( StringUtils.isEmpty(nstr)) {
			throw new IllegalArgumentException("Name attribute is missing or empty.");
		}
		skilledOnly = StringUtils.equals( xml.getAttributeValue("skilledOnly"), "yes");
		name = nstr;
		children.clear();
		Element[] elems = JDOMUtils.getChildArray( xml, "specialty");
		for ( Element e : elems ) {
			children.add( e.getAttributeValue("name") ) ;
		}
	}
	
	/**
	 * Write xml
	 */
	public Element getXML() {
		Element xml = new Element( XML_KEY );
		if ( skilledOnly ) {
			xml.setAttribute("skilledOnly","yes");
		}
		xml.setAttribute("name" , name);
		for ( String s : children  ) {
			Element e= new Element("specialty");
			e.setAttribute("name",s);
			xml.addContent( e );
		}
		return xml;
	}
	
	/**
	 * Build a skill from an element.
	 * @param xml
	 * @return
	 */
	public static Skill createSkill( Element xml ) {
		Skill res= new Skill();
		res.setXML( xml ) ;
		return res;
	}
	
	@Override
	public boolean equals(Object o ) {
		if ( !(o instanceof Skill )) {
			return false;
		}
		Skill s = (Skill)o;
		if ( !StringUtils.equals(name, s.getName() ) ) {
			return false;
		}
		if ( children==null && s.getChildren()!=null ) {
			return false;
		} else if ( children!=null && s.getChildren()==null ) {
			return false;
		} else if ( children==null && s.getChildren()==null ) {
			return true;
		} else {
			for ( String str : children ) {
				if ( !s.getChildren().contains( str ) ) {
					return false;
				}
			}
			return true;
		}
		
	}

	/**
	 * @return Returns the children.
	 */
	public MutableList<String> getChildren() {
		convertFromCastorData();
		return children;
	}

	/**
	 * @param children The children to set.
	 */
	public void setChildren(MutableList<String> children) {
		this.children = children;
		convertFromCastorData();
	}
	

	/**
	 * @param specialtyList
	 */
	private void convertFromCastorData() {
		if ( specialtyList!=null ) {
			children = new MutableList<String>();
	
			for ( Specialty sp : specialtyList ) {
				children.add( sp.getName() );
			}
		}
	}
	


	public List<Specialty> getSpecialtyList() {
		return specialtyList;
	}

	public void setSpecialtyList(ArrayList<Specialty> specialtyList) {
		this.specialtyList = new MutableList( specialtyList );
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the skilledOnly.
	 */
	public boolean isSkilledOnly() {
		return skilledOnly;
	}

	/**
	 * @param skilledOnly The skilledOnly to set.
	 */
	public void setSkilledOnly(boolean skilledOnly) {
		this.skilledOnly = skilledOnly;
	}
	
	/**
	 * Return the XML key.
	 * @return
	 */
	protected String getXMLKey() {
		return XML_KEY;
	}
	
	public void removeChild( String name ) {
		for ( int i=0;i<specialtyList.size();i++) {
			if ( specialtyList.get(i).getName().equalsIgnoreCase(name) ) {
				specialtyList.remove(i);
				return;
			}
		}
	}
}