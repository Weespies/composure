/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import uk.lug.gui.ColorUtils;
import uk.lug.gui.gridbag.GridBagException;
import uk.lug.serenity.npc.gui.controls.StatControl;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.event.StatChangeEvent;
import uk.lug.serenity.npc.model.event.StatChangeListener;
import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.model.stats.NamedStat;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Panel for controlling main stats.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class MainStatsPanel extends JPanel implements StatChangeListener,PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private StatControl[] controls = new StatControl[ MainStat.count() ];
	private Person dataModel ;
//	private static final Color ALT_BACKGROUND = new Color(231,220,175);
	private int available=36;
	private LinkedList<StatChangeListener> listeners = new LinkedList<StatChangeListener>();
	
	/**
	 * Construct a stats panel
	 * @param mainStats Main Stats object to use.
	 * @throws GridBagException 
	 */
	public MainStatsPanel() {
	}
	
	/**
	 * Construct a stats panel
	 * @param mainStats Main Stats object to use.
	 * @throws GridBagException 
	 */
	public MainStatsPanel(Person person) throws GridBagException {
		dataModel = person;
		available = person.getCurrentStatPoints();		
		createGUI();
	}
	
	/**
	 * Builds user interface.
	 * @throws GridBagException 
	 */
	private void createGUI() throws GridBagException {
		setLayout( new GridLayout(controls.length, 1) );
		for ( int i=0; i<controls.length ; i++ ) {
			NamedStat stat = dataModel.getMainStats().getStat( MainStat.keys().get(i)) ;
			controls[i] = new StatControl( stat );
			boolean b = (((i/2)*2)==i);
			if ( b ) {
				controls[i].setBackground( ColorUtils.darken( getBackground() ,0.10f) );
			}
			controls[i].setName( stat.getAbbrevation()+".panel" );
			controls[i].setPointsAvailable( available );
			stat.addStatChangeListener( this );
			add(controls[i]);
		}
	}
	
	/**
	 * Return the control for a given stat.
	 */
	public StatControl getControlFor(String key ) {
		for ( int i=0; i<MainStat.count(); i++ ) {
			if ( MainStat.keys().get(i).equals(key) ) {
				return controls[i];
			}
		}
		return null;
	}
	
	/**
	 * Return the given NamedStat , straight from the control.
	 */
	public NamedStat getNamedStat(String key ) {
		return dataModel.getMainStats().getStat( key );
		
	}
	
	/**
	 * Tells this panel how many points are available to spend, which it passes down to the 
	 * each control in the panel;
	 * @param pts
	 */
	public void setPointsAvailable(int pts) {
		available=pts;
		for ( StatControl control : controls ) {
			control.setPointsAvailable( pts );
		}
	}


	/* (non-Javadoc)
	 * @see lug.serenity.event.StatChangeListener#statChanged(lug.serenity.event.StatChangeEvent)
	 */
	public void statChanged(StatChangeEvent sce) {
		if ( dataModel==null ) {
			return;
		}
		if ( sce.getType()== StatChangeEvent.EVENT_INCREASED ) {
			int pts = dataModel.getCurrentStatPoints()-sce.getAdjustAmount();
			dataModel.setCurrentPoints( pts );
		} else if ( sce.getType()==StatChangeEvent.EVENT_DECREASED ){
			int pts = dataModel.getCurrentStatPoints()+sce.getAdjustAmount();
			dataModel.setCurrentPoints( pts );
		}
		fireAllListeners( sce );
	}
	
	/**
	 * Add a listener to be notified of stat changes.
	 * @param scl
	 */
	public void addStatChangeListener( StatChangeListener scl ) {
		listeners.add( scl );
	}
	
	/**
	 * Remove a listener to no longer be notified of stat changes.
	 * @param scl
	 */	
	public void removeStatChangeListener( StatChangeListener scl ) {
		listeners.remove( scl );
	}
	
	/**
	 * Fires all listeners.
	 */
	private void fireAllListeners( StatChangeEvent sce ) {
		for ( StatChangeListener scl : listeners ){
			scl.statChanged( sce );
		}
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return dataModel;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		if ( this.dataModel!=null ) {
			this.dataModel.removePropertyChangeListener( this );
		}
		this.dataModel = person;
		this.dataModel.addPropertyChangeListener( this );
		available = person.getCurrentStatPoints();
		this.removeAll();
		try {
			createGUI();
		} catch (GridBagException e) {
			e.printStackTrace();
		}
		revalidate();
		repaint();
	}



	/**
	 * respond to changes to the datamodel.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (dataModel==null) {
			return;
		}
		String property = evt.getPropertyName();
		if ( property.equals( Person.PROPERTY_STAT_POINTS ) ) {
			setPointsAvailable( dataModel.getCurrentStatPoints() );
		}
	}
}
