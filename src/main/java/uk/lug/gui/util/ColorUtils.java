/**
 * 
 */
package uk.lug.gui.util;

import java.awt.Color;
import java.util.Random;



/**
 * $Id$
 * 
 * @version $Revision$
 * @author $Author$
 *         <p>
 * 
 */
/**
 * @author Luggy
 * 
 */
public class ColorUtils {
	private static final String hexchars="0123456789abcdef";
	
	/**
	 * Change alpha
	 * 
	 * @param in
	 * @param newAlpha
	 *            The new alpha value, 0..255
	 * @return in, modified to the new alpha
	 */
	public static Color setAlpha(Color in, int newAlpha) {
		if (in.getAlpha() == newAlpha) {
			return in;
		} else {
			return new Color(in.getRed(), in.getGreen(), in.getBlue(), newAlpha);
		}
	}

	/**
	 * Darken a colour by a specified amount
	 * 
	 * @param in
	 * @param amount
	 * @return a new Color
	 */
	public static Color darken(Color in, float amount) {
		return lighten(in, -amount);
	}

	/**
	 * Lighten a colour by a specified amount
	 * 
	 * @param in
	 * @param amount
	 * @return a new Color
	 */
	public static Color lighten(Color in, float amount) {
		float[] hsb = new float[3];
		Color.RGBtoHSB(in.getRed(), in.getGreen(), in.getBlue(), hsb);
		hsb[2] = Math.max(0.0f, Math.min(1.0f, hsb[2] + amount));
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	/**
	 * Blend two colours together
	 * 
	 * @param a
	 * @param b
	 * @param ratio
	 *            (0.0 means all a, 1.0 means all b)
	 */
	public static Color blend(Color a, Color b, float ratio) {
		if (ratio <= 0.0f) {
			return a;
		} else if (ratio >= 1.0f) {
			return b;
		} else {
			float omr = 1.0f - ratio;
			return new Color((int) (a.getRed() * ratio + b.getRed() * omr),
					(int) (a.getGreen() * ratio + b.getGreen() * omr), (int) (a
							.getBlue()
							* ratio + b.getBlue() * omr), (int) (a.getAlpha()
							* ratio + b.getAlpha() * omr));
		}
	}

	/**
	 * Convert's a color into a string suitable for placement within a web page.
	 * For example, submitting a Color(255,0,255) will give the result
	 * "#FF00FF".
	 * 
	 * @param c
	 *            Color to convert.
	 */
	public static String colorToString(Color c) {
		StringBuilder res = new StringBuilder();
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		res.append("#");
		res.append(getDoubleHexString(r));
		res.append(getDoubleHexString(g));
		res.append(getDoubleHexString(b));
		return res.toString().toUpperCase();
	}

	/**
	 * Determines whether or not the supplied string can be interpretted by the
	 * ParseColor() method as a color.
	 * 
	 * @param s
	 *            String to test for colour readability.
	 * @return TRUE if s contains six hexadecimal digits or a hash sign (#)
	 *         followed by six hexadecimal digits.
	 */
	public static boolean isColorString(String s) {
		if (s == null) {
			return false;
		}
		String s2 = s.toLowerCase();
		if (s2.startsWith("#")) {
			s2 = s2.substring(1, s2.length());
		}
		if (s2.length() != 6) {
			return false;
		}
		for (int i = 0; i < s2.length(); i++) {
			char c = s2.charAt(i);
			if (hexchars.indexOf(c) == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Averages out the Reg, Green and Blue components of a Color to determine a
	 * grey value.
	 * 
	 * @param c
	 *            Color object to average.
	 * @return an integer between 0 and 255.
	 */
	public static int getGreyValue(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int grey = ((r + g + b) / 3);
		return grey;
	}

	/**
	 * Returns a reduced saturation (weakened) copy of a Color..
	 * 
	 * @param org
	 *            Color to weaken
	 * @param percent
	 *            Percentage to weaken it by.
	 * @return The weakened Color.
	 */
	public static Color weaken(Color org, int percent) {
		float[] hsb = Color.RGBtoHSB(org.getRed(), org.getGreen(), org
				.getBlue(), null);
		float sat = hsb[1];
		sat = ((sat * (100 - percent)) / 100);
		hsb[1] = sat;
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	/**
	 * Converts the supplied integer to a hexadecimal string which is always at
	 * least 2 characters long.
	 * 
	 * @param i
	 *            int to convert.
	 * @return A string object of hexadecimal characters, of at least length 2.
	 */
	public static String getDoubleHexString(int i) {
		String res = Integer.toHexString(i);
		while (res.length() < 2) {
			res = "0" + res;
		}
		return res;
	}

	/**
	 * <p>
	 * Create a Color from a string describing a colour in RGB format using six
	 * hexadecimal characters. This string can [optionally] contain a # symbol
	 * at the start.
	 * </p>
	 * @param col String describing the colour in hex, in the format RRGGBB or
	 *            #RRGGBB.
	 * @throws IllegalArgumentException
	 *             if the supplied string cannot be interpretted as a colour.
	 */
	public static Color parseColor(String col) {
		if (col.startsWith("#")) {
			col = col.substring(1, col.length());
		}
		if (col.length() != 6) {
			throw new IllegalArgumentException(
					"Colour descriptor must be 6 characters.");
		}
		col = col.toLowerCase();
		// Check for validity of all characters
		for (int i = 0; i < col.length(); i++) {
			if (hexchars.indexOf(col.charAt(i)) == -1) {
				throw new IllegalArgumentException("Cannot parse \"" + col
						+ "\" as a color descriptor.");
			}
		}
		int r = Integer.parseInt(col.substring(0, 2), 16);
		int g = Integer.parseInt(col.substring(2, 4), 16);
		int b = Integer.parseInt(col.substring(4, 6), 16);
		return new Color(r, g, b);
	}

	/**
	 * Return a completely random color. Wow, look at all the crazy colors man.
	 */
	public static Color getRandomColor() {
		Random random = new Random();
		int r = random.nextInt(256);
		int g = random.nextInt(256);
		int b = random.nextInt(256);
		return new Color(r, g, b);
	}
	
	/**
	 * Anaylse a color and return or white depending on its 
	 * brightness. 
	 * @param rgb rgb value of the Color to anaylse
	 * @param whiteLevel brightness required of the color for it to be considered white.
	 * @return Color.BLACK if the brightness is less than 128
	 * or Color.WHITE if the brightness if greater than or equal to 128. 
	 */
	public static Color toBlackOrWhite(int rgb, float whiteLevel) {
		Color c = new Color(rgb);
		float bright = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				null)[2];
		return ( bright<whiteLevel? Color.BLACK : Color.WHITE );
	}
	
	/**
	 * Anaylse a color and return or white depending on its 
	 * brightness. 
	 * @param c Color to anaylse
	 * @param whiteLevel brightness required of the color for it to be considered white.
	 * @return Color.BLACK if the brightness is less than 128
	 * or Color.WHITE if the brightness if greater than or equal to 128. 
	 */
	public static Color toBlackOrWhite(Color c, float whiteLevel) {
		float bright = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				null)[2];
		return ( bright<whiteLevel? Color.BLACK : Color.WHITE );
	}
	
	/**
	 * Anaylse a color and return or white depending on its 
	 * brightness. 
	 * @param c Color to anaylse
	 * @return Color.BLACK if the brightness is less than 0.5f
	 * or Color.WHITE if the brightness if greater than or equal to 128. 
	 */
	public static Color toBlackOrWhite(Color c) {
		return toBlackOrWhite(c,0.5f);
	}
	
	/**
	 * Anaylse a color and return or white depending on its 
	 * brightness. 
	 * @param rgb rgb value of the color to anaylse
	 * @return the int RGB of Color.BLACK if the brightness is less than 128
	 * or Color.WHITE if the brightness if greater than or equal to 128. 
	 */
	public static int toBlackOrWhite(int rgb) {
		return toBlackOrWhite ( new Color(rgb), 0.5f).getRGB();
	}
	
	/**
	 * Return the R G & B components of a color as an int array.
	 * @return an integer array of the color, containing red, green and blue in that order.
	 */
	public static final int[] getRGBInts(Color c) {
		return new int[]{c.getRed(), c.getGreen(), c.getBlue()};
	}
}
