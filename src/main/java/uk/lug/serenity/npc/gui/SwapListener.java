/**
 * 
 */
package uk.lug.serenity.npc.gui;


/**
 * Copyright 9 Jul 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public interface SwapListener<T extends Object> {
	
	/**
	 * Signal that a swap is to be required
	 * @param oldObject things that is to be swapped out.
	 */
	public void swapFor( T oldObject);
}
