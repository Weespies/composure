/**
 * 
 */
package uk.lug.serenity.npc.model.traits;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uk.lug.gui.CachedImageLoader;

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
public enum TraitType {
	
	ASSET			(0, "Asset", CachedImageLoader.getCachedImage("images/face-angel.png")),
	COMPLICATION	(1,"Complication", CachedImageLoader.getCachedImage("images/face-devil-grin.png")),
	UNKNOWN 		(2,"Unknown", CachedImageLoader.getCachedImage("images/delete.png"));
	
	private int id;
	private String name;
	private Image smallImage;
	
	
	/**
	 * Construct a trait type.
	 * @param id
	 * @param name
	 * @param smallImage
	 */
	private TraitType(int id, String name, Image smallImage) {
		this.id = id;
		this.name = name;
		this.smallImage = smallImage;
	}

	/**
	 * @return the trait id number.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name of the trait type.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the image for trait type.
	 */
	public Image getImage() {
		return smallImage;
	}
	
	/**
	 * @return the icon for a this TraitType.
	 */
	public Icon getIcon() {
		return new ImageIcon( smallImage );
	}
}
