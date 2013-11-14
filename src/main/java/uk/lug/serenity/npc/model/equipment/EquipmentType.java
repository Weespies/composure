/**
 * 
 */
package uk.lug.serenity.npc.model.equipment;

import javax.swing.Icon;

import org.apache.commons.lang.StringUtils;

import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.gui.equipment.EquipmentTypeCellRenderer;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public enum EquipmentType {
	ARMOR("Armor",CachedImageLoader.getCachedIcon( "images/shield.png") ),
	COMMUNICATIONS("Communications",CachedImageLoader.getCachedIcon("images/irkick.png")),
	COMPUTING("Computing", CachedImageLoader.getCachedIcon("images/background.png") ),
	COVERT("Covert",CachedImageLoader.getCachedIcon( "images/viewmag.png" ) ),
	ENGINEERING("Engineering",CachedImageLoader.getCachedIcon( "images/configure.png" ) ),
	EXPLOSIVE("Explosive", CachedImageLoader.getCachedIcon( "images/clanbomber.png")),
	FOOD_AND_DRINK("Food and Drink",CachedImageLoader.getCachedIcon( "images/cookie.png")),
	MEDICAL("Medical",CachedImageLoader.getCachedIcon( "images/kcmdrkonqi.png" ) ),
	MELEE_WEAPON("Melee",CachedImageLoader.getCachedIcon( "images/kcmsystem.png" ) ),
	MISC("Misc",CachedImageLoader.getCachedIcon( "images/package.png" ) ),
	RANGED_WEAPON("Ranged",CachedImageLoader.getCachedIcon( "images/317.gif") ),
	TOOLS("Tools", CachedImageLoader.getCachedIcon( "images/package_development.png" ) );
	
	/**
	 * Return a named equipment type or MISC is no match can be found.
	 * @param n
	 * @return
	 */
	public static EquipmentType getForName(String n ) {
		if ( StringUtils.isEmpty(n) ) {
			return MISC;
		}
		for ( EquipmentType et : EquipmentType.values() ) {
			if ( et.getName().equalsIgnoreCase( n ) ) {
				return et;
			}
		}
		return MISC;
	}
	
	private String name;
	private Icon icon;
	
	private EquipmentType(String name, Icon icon) {
		this.name = name;
		this.icon = icon;
	}
	
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public static EquipmentTypeCellRenderer getCellRenderer() {
		return new EquipmentTypeCellRenderer();
	}
}

