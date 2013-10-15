/**
 * 
 */
package uk.lug.serenity.npc.model.event;

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
public interface StatChangeListener {
	
	/**
	 * Notification of a stat value increase
	 * @param sce The change event.
	 * @param increasedBy Amount by which the stat increased.
	 */
	public void statChanged( StatChangeEvent sce );}
