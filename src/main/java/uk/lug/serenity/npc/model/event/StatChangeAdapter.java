/**
 * 
 */
package uk.lug.serenity.npc.model.event;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Listener for the stat change event with do nothing methods .</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class StatChangeAdapter implements StatChangeListener {
	/**
	 * Notification of a stat value increase
	 * @param sce The change event.
	 * @param increasedBy Amount by which the stat increased.
	 */
	@SuppressWarnings("unused")
	public void statIncreased(StatChangeEvent sce, int increasedBy) {
	}

	/**
	 * Notification of a stat value increase
	 * @param sce The change event.
	 * @param increasedBy Amount by which the stat increased.
	 */
	@SuppressWarnings("unused")
	public void statDecreased(StatChangeEvent sce, int decreasedBy) {
	}

	/**
	 * Notification of a stat value increase
	 * @param sce The change event.
	 * @param increasedBy Amount by which the stat increased.
	 */
	public void statChanged(StatChangeEvent sce) {
	}

}
