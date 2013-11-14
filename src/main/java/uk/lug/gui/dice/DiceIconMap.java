package uk.lug.gui.dice;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.Icon;

import uk.lug.gui.util.CachedImageLoader;

public class DiceIconMap extends Hashtable<Integer,Icon> {
	
	public DiceIconMap() {
		super();
		put(4, CachedImageLoader.DICE_D4);
		put(6, CachedImageLoader.DICE_D6);
		put(8, CachedImageLoader.DICE_D8);
		put(10, CachedImageLoader.DICE_D10);
		put(12, CachedImageLoader.DICE_D12);
		put(20, CachedImageLoader.DICE_D20);
	}
}
