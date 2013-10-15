/**
 * 
 */
package uk.lug.gui.selector;

/**
 * @author Luggy
 *
 */
public interface SelectorMarshaller<L extends Comparable<L>, R extends Comparable<R>> {
	/**
	 * Convert an object from the left list into one suitable for the right list.
	 * @param leftObject
	 * @return a right object type version of the left object type.
	 */
	public R select(L leftObject);
	
	/**
	 * Convert an ojbect from the right list into one suitable for the left list.
	 * @param rightObject
	 * @return a left object type version of the right object type.
	 */
	public L unselect(R rightObject);
}
