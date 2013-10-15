/**
 * 
 */
package uk.lug.serenity.npc.random;

import org.jdom.Element;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Contains the name of a skill and a percentage bias towards that skill.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class SkillBias {
	private String skillName;
	private int bias;
	
	/**
	 * Construct a skill profile from an xml element.
	 * @param xml
	 * @return
	 */
	public static SkillBias createFromXML( Element xml ) {
		if ( xml.getName().equals("profile") ) {
			throw new IllegalArgumentException(
					"Wrong element type. Expected \"profile\" got \""
							+ xml.getName() + "\"");
		}
		SkillBias profile = new SkillBias();
		profile.setSkillName( xml.getAttributeValue("name") );
		profile.setBias( Integer.parseInt( xml.getAttributeValue("bias") ) );
		return profile;
	}
	
	/**
	 * C'tor
	 */
	public SkillBias() {
		
	}
	
	/**
	 * @param skillName
	 * @param bias
	 */
	public SkillBias(String skillName, int bias) {
		super();
		this.skillName = skillName;
		this.bias = bias;
	}
	/**
	 * @return the bias
	 */
	public int getBias() {
		return bias;
	}
	/**
	 * @param bias the bias to set
	 */
	public void setBias(int bias) {
		this.bias = bias;
	}
	/**
	 * @return the skillName
	 */
	public String getSkillName() {
		return skillName;
	}
	/**
	 * @param skillName the skillName to set
	 */
	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}
}
