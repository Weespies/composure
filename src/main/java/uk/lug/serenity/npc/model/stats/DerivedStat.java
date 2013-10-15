/**
 * 
 */
package uk.lug.serenity.npc.model.stats;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.lug.serenity.npc.model.event.StatChangeEvent;
import uk.lug.serenity.npc.model.event.StatChangeListener;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>A stat whos value is that of two or more stats combined.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class DerivedStat implements StatChangeListener{
	private String name;
	private NamedStat[] componentStats;
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
	
	/**
	 * @param name
	 * @param stats
	 **/
	public DerivedStat(String name, NamedStat[] stats) {
		super();
		this.name = name;
		this.componentStats = stats;
		for (NamedStat stat : componentStats) {
			stat.addStatChangeListener( this );
		}
	}
	
	/**
	 * @return Returns the componentStats.
	 */
	public NamedStat[] getComponentStats() {
		return componentStats;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the combined point total for this stat.
	 * This is not public because the total value is not relevant to the game, instead
	 * the combined dice should be used
	 */
	protected int getCombinedValue() {
		int res=0;
		for ( NamedStat stat : componentStats ) {
			res = res+ stat.getValue() ;
		}
		return res;
	}
	
	/**
	 * Returns the actual dice for this combined stat.
	 * @return
	 */
	public String getDice() {
		StringBuilder sb= new StringBuilder();
		for ( NamedStat stat : componentStats ) {
			sb.append(stat.getDice() );
			sb.append(" + ");
		}
		String res = sb.toString();
		if ( res.endsWith(" + ") ) {
			res = res.substring(0, res.length()-3) ;
		}
		return res;
	}
	
	/**
	 * Returns the combined value for this combined stat.
	 * @return
	 */
	public int getValue() {
		int res =0;
		for ( NamedStat stat : componentStats ) {
			res=res+stat.getValue();
		}
		return res;
	}
	
	/**
	 * Add a listener to be notified when this stat changes (i.e. when one of the component 
	 * stats changes).
	 * @param args
	 */
	public void addChangeListener(ChangeListener cl ) {
		if ( !listeners.contains(cl) ) {
			listeners.add( cl );
		}
	}

	/**
	 * Removes a listener to no longer be notified when this stat changes (i.e. when one of the component 
	 * stats changes).
	 * @param args
	 */
	public void removeChangeListener(ChangeListener cl ) {
		listeners.remove( cl );
	}
	
	/**
	 * Fire listeners
	 * @param args
	 */
	private void fireAllListeners() {
		ChangeEvent event = new ChangeEvent(this);
		for ( ChangeListener cl : listeners ) {
			cl.stateChanged( event );
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" = ");
		for (NamedStat stat : componentStats ) {
			sb.append( stat.getAbbrevation() );
			sb.append( " " );
			sb.append( stat.getDice() );
			sb.append( " + ");
		}
		String res = sb.toString();
		if ( res.endsWith(" + ") ) {
			res = res.substring(0, res.length()-3) ;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see lug.serenity.event.StatChangeListener#statChanged(lug.serenity.event.StatChangeEvent)
	 */
	public void statChanged(StatChangeEvent sce) {
		fireAllListeners();
	}
}
