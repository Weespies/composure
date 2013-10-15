/**
 * 
 */
package uk.lug.serenity.npc.model.equipment;

import javax.swing.Icon;

import uk.lug.gui.CachedImageLoader;

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
public enum Availability {
	EVERYWHERE ("E", "Everywhere", CachedImageLoader.getCachedIcon( "images/everywhere.png") ),
	CORE       ("C", "Core", CachedImageLoader.getCachedIcon( "images/core.png") ),
	RIM        ("R", "Rim", CachedImageLoader.getCachedIcon( "images/rim.png") ),
	ILLEGAL    ("I", "Illegal", CachedImageLoader.getCachedIcon( "images/illegal.png") );
	private String code;
	private String description;
	private Icon icon;
	
	/**
	 * @return the icon
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param code
	 * @param description
	 */
	private Availability(String code, String description, Icon icon) {
		this.code = code;
		this.description = description;
		this.icon = icon;
	}
	
	public static Availability getForCode( String forCode ) {
		for ( Availability avail : Availability.values() ) {
			if ( avail.getCode().equals( forCode ) ) {
				return avail;
			}
		}
		return null;
	}
}
