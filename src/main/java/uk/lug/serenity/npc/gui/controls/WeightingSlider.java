/**
 * 
 */
package uk.lug.serenity.npc.gui.controls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.StringUtils;

import uk.lug.serenity.npc.random.archetype.Weighting;

/**
 * @author Luggy
 *
 */
public class WeightingSlider extends JButtonRow implements PropertyChangeListener, ChangeListener {
	private Weighting dataModel;
	private boolean valueIsAdjusting=false;	
	
	/**
	 * @param weighting
	 * @param maximum
	 */
	public WeightingSlider(Weighting weighting) {
		super(1, 7);
		valueIsAdjusting=true;
		dataModel = weighting;
		super.setValue( weighting.getValue() );
		
		dataModel.addPropertyChangeListener( this );
		super.addChangeListener( this );
		valueIsAdjusting = false;
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ( valueIsAdjusting ) {
			return;
		}
		valueIsAdjusting=true;
		if ( !StringUtils.equals( Weighting.Properties.VALUE, evt.getPropertyName() ) ) {
			return;
		}
		int oldValue = ((Integer)evt.getOldValue()).intValue();
		int newValue = ((Integer)evt.getNewValue()).intValue();
		if ( newValue==super.getValue() ) {
			valueIsAdjusting =false;
			return;
		}
		
		value = newValue;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLabelIcons();
			}
		});
		valueIsAdjusting=false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if ( valueIsAdjusting ) {
			return ;
		}
		valueIsAdjusting=true;
		dataModel.setValue( super.getValue() );
		valueIsAdjusting=false;
	}

	/**
	 * @param weighting2
	 */
	public void setWeighting(Weighting weighting2) {
		dataModel = weighting2;
		valueIsAdjusting=true;
		super.setValue(dataModel.getValue());
		valueIsAdjusting=false;
	}
}
