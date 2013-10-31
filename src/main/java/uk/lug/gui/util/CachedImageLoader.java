package uk.lug.gui.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Resource loader for local resourced images.  Utilities a cache for speeding up repeated loading.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class CachedImageLoader {
	private static HashMap<String , BufferedImage> cache = new HashMap<String,BufferedImage>();
	public static final Icon ADD_ICON = CachedImageLoader.getCachedIcon("images/add.png");
	public static final Icon DELETE_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	public static final Icon DICE_ICON = CachedImageLoader.getCachedIcon("images/die_48.png");
	public static final Icon DICE_SMALL = CachedImageLoader.getCachedIcon("images/die_16.png");
	public static final Icon GLOVES_ICON = CachedImageLoader.getCachedIcon("images/gloves.png");
	public static final Icon CONFIGURE_ICON = CachedImageLoader.getCachedIcon("images/configure.png");
	public static final Icon CLEAR_CHARACTER_ICON = CachedImageLoader.getCachedIcon("images/clearcharacter.png");
	public static final Icon LOAD_ICON = CachedImageLoader.getCachedIcon("images/document-open.png");
	public static final Icon SAVE_ICON = CachedImageLoader.getCachedIcon("images/document-save.png");
	public static final Icon SAVE_AS_ICON = CachedImageLoader.getCachedIcon("images/document-save-as.png");
	public static final Icon SAVE_ASCII = CachedImageLoader.getCachedIcon("images/document_text.png");
	public static final Icon ARCHETYPES_MANAGER_ICON = CachedImageLoader.getCachedIcon("images/configure.png");
	
	/**
	 * Loads an icon from the cache, or local resource if not already in the cache.
	 * @param resourceName resource path for the icon's image.
	 * @return an Icon containing the image from the given resource or an empty 16x16 icon
	 * if the resource cannot be found.
	 */
	public static Icon getCachedIcon(String resourceName) {
		BufferedImage img = cache.get( resourceName );
		if ( img==null ) {
			//Not in cache.
			try {
				ClassLoader loader = ClassLoader.getSystemClassLoader();
				InputStream instream= loader.getResourceAsStream(resourceName);
				img = ImageIO.read(instream);
				cache.put( resourceName , img );
			} catch (Exception e) {
				//Cannot load, create a blank one.
				img = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
			}
		}
		return new ImageIcon( img );
	}
	
	/**
	 * Loads an image from the cache, or local resource if not already in the cache.
	 * @param resourceName resource path for the image.
	 * @return the image from the resource provided from the given resource or an empty 16x16 icon
	 * if the resource cannot be found.
	 */
	public static BufferedImage getCachedImage(String resourceName) {
		BufferedImage img = cache.get( resourceName );
		if ( img==null ) {
			//Not in cache.
			try {
				ClassLoader loader = ClassLoader.getSystemClassLoader();
				InputStream instream= loader.getResourceAsStream(resourceName);
				img = ImageIO.read(instream);
				cache.put( resourceName , img );
			} catch (Exception e) {
				//Cannot load, create a blank one.
				img = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
			}
		}
		return img;
	}
}
