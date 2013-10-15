/**
 * 
 */
package uk.lug.gui.selector;

/**
 * @author Luggy
 * SelectorMarshaller for models where the same type of object is used both on the left 
 * and right sides.
 */
public class SymmetricSelectorMarshaller<T extends Comparable<T>> implements SelectorMarshaller<T,T> {

	/* (non-Javadoc)
	 * @see luggy.gui.selector.SelectorMarshaller#select(java.lang.Comparable)
	 */
	public T select(T leftObject) {
		return leftObject;
	}

	/* (non-Javadoc)
	 * @see luggy.gui.selector.SelectorMarshaller#unselect(java.lang.Comparable)
	 */
	public T unselect(T rightObject) {
		return rightObject;
	}
}