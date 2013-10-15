/**
 * 
 */
package uk.lug.serenity.npc.model.event;

import uk.lug.serenity.npc.model.stats.NamedStat;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Events signifying a change in stat.</p>
 * @author Luggy
 *
 */

public class StatChangeEvent {
	private NamedStat stat;
	private int ID =0;
	public static final int EVENT_CHANGED=1;
	public static final int EVENT_INCREASED=2;
	public static final int EVENT_DECREASED=3;
	public int adjustAmount =0;
	
	/**
	 * Create a Stat Change Event
	 * @param type type of event.
	 * @param namedStat the stat in the stat it was after the change.
	 * @param adjustedBy the amout the stat's value changed by .
	 */
	public StatChangeEvent(int type , NamedStat namedStat, int adjustedBy) {
		if ( type!=EVENT_CHANGED && type!=EVENT_INCREASED && type!=EVENT_DECREASED ) {
			throw new IllegalArgumentException("Invalid stat change event type");
		}
		ID=type;
		stat = namedStat;		
		adjustAmount=adjustedBy;
	}

	/**
	 * @return Returns the stat.
	 */
	public NamedStat getStat() {
		return stat;
	}

	/**
	 * @return Returns the iD.
	 */
	public int getType() {
		return ID;
	}

	/**
	 * @return Returns the adjustAmount.
	 */
	public int getAdjustAmount() {
		return adjustAmount;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj==null || !(obj instanceof StatChangeEvent) ) {
			return false;
		}
		StatChangeEvent sce = (StatChangeEvent)obj;
		if (sce.getType()!=getType() ){
			return false;
		}
		if ( sce.getAdjustAmount()!=getAdjustAmount() ) {
			return false;
		}
		if ( !sce.getStat().equals( getStat() ) ) {
			return false;
		}
		return true;
	}

}
