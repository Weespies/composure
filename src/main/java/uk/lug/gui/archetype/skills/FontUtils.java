/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.Component;
import java.awt.Font;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Lots of little font utilities.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class FontUtils {

	
	/**
	 * Take the existing font on the given component and bold it.
	 * @param comp
	 */
	public static void Embolden( Component comp ) {
		Font f = comp.getFont();
		comp.setFont( f.deriveFont( Font.BOLD ) );
	}
	
	/**
	 * Take the existing font on the given component and bold it.
	 * @param comp
	 */
	public static void Italicise( Component comp ) {
		Font f = comp.getFont();
		comp.setFont( f.deriveFont( Font.ITALIC) );
	}
	

	
	/**
	 * Take the existing font on the given component and bold it.
	 * @param comp
	 */
	public static void ItaliciseAndEmbolden( Component comp ) {
		Font f = comp.getFont();
		comp.setFont( f.deriveFont( Font.ITALIC+Font.BOLD) );
	}
	
	public static void Plain( Component comp ) {
		Font f = comp.getFont();
		comp.setFont( f.deriveFont( Font.PLAIN) );
	}
}
