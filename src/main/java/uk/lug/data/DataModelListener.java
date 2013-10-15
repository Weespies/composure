/**
 * 
 */
package uk.lug.data;

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
public interface DataModelListener<T extends Object> {
	public void dataChanged(T oldData, T newData);

}
