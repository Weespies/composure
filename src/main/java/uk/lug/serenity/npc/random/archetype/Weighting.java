/**
 * 
 */
package uk.lug.serenity.npc.random.archetype;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Luggy
 * A weighting represents a number between 0 and 100.
 */
public class Weighting {
	public static final int MAXIMUM_VALUE = 100;
	public static final int MINIMUM_VALUE = 0;
	
	private int value;
	
	public interface Properties {
		public static final String VALUE="VALUE";
	};
	
	private PropertyChangeSupport propertyChangeSupport;

	
	/**
	 * Construct a weighting object of a specific value.
	 * @param value
	 * @throws IllegalArgumentException if an attempt is made to set a value less than 
	 * minimum or greater than maximum.
	 */
	public Weighting(int value) {
		super();
		if ( value<MINIMUM_VALUE ) {
			throw new IllegalArgumentException("New value is less than minimum weighting value.");
		}
		if ( value>MAXIMUM_VALUE ) {
			throw new IllegalArgumentException("New value is greater than maximum weighting value.");
		}
		this.value = value;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Construct a weighting object at the minimum/
	 */
	public Weighting() {
		super();
		value = MINIMUM_VALUE;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * @return the current weighting value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the value
	 * @param value
	 * @throws IllegalArgumentException if an attempt is made to set a value less than 
	 * minimum or greater than maximum.
	 */
	public void setValue(int value) {
		if ( value<MINIMUM_VALUE ) {
			throw new IllegalArgumentException("New value is less than minimum weighting value.");
		}
		if ( value>MAXIMUM_VALUE ) {
			throw new IllegalArgumentException("New value is greater than maximum weighting value.");
		}
		int oldValue = this.value;
		this.value = value;
		propertyChangeSupport.firePropertyChange(Properties.VALUE, oldValue, this.value);
	}
	
	/**
	 * Add a listener to be notified of property changes.
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);	
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener ) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
}
