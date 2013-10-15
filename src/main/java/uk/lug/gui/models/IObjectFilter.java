/**
 * 
 */
package uk.lug.gui.models;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Interface used within the ObjectSelectorPanel for filtering by text.</p>
 * @author Luggy
 *
 */
public interface IObjectFilter<FilterElement extends Object> {
	
	/**
	 * @return The String being used as the filtering criteria.
	 */
	public String getCriteria();
	
	/**
	 * @param criteria the string to be used as the filtering criteria.
	 */
	public void setCriteria(String criteria);
	
	/**
	 * Filters the object list based upon the critiera.
	 * @param obj The object to be filtered.
	 * @return TRUE if the object matches the filtering criteria, false if it doesn't.
	 */
	public boolean matches(FilterElement obj);
}
