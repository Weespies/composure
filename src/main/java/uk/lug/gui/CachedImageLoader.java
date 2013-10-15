package uk.lug.gui;

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
