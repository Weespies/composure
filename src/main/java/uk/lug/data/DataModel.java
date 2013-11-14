package uk.lug.data;

import java.util.ArrayList;
import java.util.List;

import uk.lug.serenity.npc.gui.SkillSheetPanel;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Class for holding a object .  Other classes should get passed the dataobject so 
 * they can attach listeners and be notified when the data object changes.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class DataModel<T extends Object> {
	private T data;
	private List<DataModelListener<T>> listeners = new ArrayList<DataModelListener<T>>();
	
	/**
	 * Creates a DataModel with an initial object.
	 * @param data
	 */
	public DataModel(T data) {
		super();
		this.data = data;
	}
	
	/**
	 * Create with no data.
	 */
	public DataModel() {
		
	}

	/**
	 * @return the data object
	 */
		
	public T getData() {
			return data;
	}

	/**
	 * @param data the data object to set
	 */
	public void setData(T data) {
		T oldData = this.data;
		this.data = data;
		for ( DataModelListener<T> dml : listeners ) {
			if ( dml instanceof SkillSheetPanel ) {
			}
			dml.dataChanged( oldData, this.data );
		}
	}

	/**
	 * Adds a listener to be notified when the data in this model changes.
	 * @param dml
	 */
	public void addDataModelListener( DataModelListener<T> dml ) {
		listeners.add( dml );
	}
	
	/**
	 * Removes a listener to no longer be notified when the data in this model changes.
	 * @param dml
	 */
	public void removeDataModelListener( DataModelListener<T> dml ) {
		listeners.remove( dml );
	}
	
}
