/**
 * 
 */
package uk.lug.serenity.npc.model.traits;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import uk.lug.MutableList;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * Utility class for holding character trait lists.
 *
 */
public class TraitList extends MutableList<TraitData> implements ListDataListener {
	private TraitType type;
	private int totalCost =0 ;

	/**
	 * Construct a trait list.
	 * @param type type of trait to list (ASSET or COMPLICATION)
	 */
	public TraitList(TraitType type) {
		super();
		if ( type==TraitType.UNKNOWN ) {
			throw new IllegalArgumentException("TraitType.UNKNOWN is a placeholder type only and cannot be used in lists.");
		}
		this.type = type;
		super.addListDataListener( this );
	}

	/**
	 * Construct a "wrong type you muppet" error.
	 * @param badType
	 * @return
	 */
	private String typeErrorMessage( TraitType badType ) {
		StringBuilder ret = new StringBuilder(128);
		
		ret.append("Cannot add trait of type ");
		ret.append( badType.getName() ) ;
		ret.append(" to lists of type ");
		ret.append( type.getName() );
		ret.append( " .");
		
		return ret.toString();
	}

	/* (non-Javadoc)
	 * @see luggy.MutableList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, TraitData element) {
		if ( element.getType()!=type ) {
			throw new IllegalArgumentException( typeErrorMessage( element.getType() ) );
		}
		if ( contains(element) ) {
			return;
		}
		super.add(index, element);
	}

	/* (non-Javadoc)
	 * @see luggy.MutableList#add(java.lang.Object)
	 */
	@Override
	public boolean add(TraitData o) {
		if ( o.getType()!=type ) {
			throw new IllegalArgumentException( typeErrorMessage( o.getType() ) );
		}
		if ( contains(o) ) {
			return false;
		}
		return super.add( o );
	}

	/**
	 * Add all traits inside the collection that are of the same type.
	 * @param c collection of traits
	 */
	@Override
	public boolean addAll(Collection<? extends TraitData> c) {
		ArrayList<TraitData> list = new ArrayList<TraitData>();
		for ( TraitData td : c ) {
			if ( td.getType()==type && !contains(td) ) {
				list.add( td );
			}
		}
		boolean b = super.addAll( list );
		list.clear();
		list=null;
		return b;
	}

	/* (non-Javadoc)
	 * @see luggy.MutableList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends TraitData> c) {
		ArrayList<TraitData> list = new ArrayList<TraitData>();
		for ( TraitData td : c ) {
			if ( td.getType()==type ) {
				list.add( td );
			}
		}
		boolean b = super.addAll(index, list );
		list.clear();
		list=null;
		return b;
	}
	
	/**
	 * @return the type of traits that this list holds.
	 */
	public TraitType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	public void contentsChanged(ListDataEvent e) {
		if ( e.getSource()==this ) {
			recomputeTotalCost();
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	public void intervalAdded(ListDataEvent e) {
		if ( e.getSource()==this ) {
			recomputeTotalCost();
		}
	}

	/**
	 * Recompute the total cost of this list .
	 */
	private synchronized void recomputeTotalCost() {
		int tc = 0;
		for ( TraitData tdata : this ) {
			tc=tc+tdata.getCost();
		}
		totalCost = ( getType()==TraitType.COMPLICATION ? -tc : tc );
	}

	/**
	 * Return the cost of the traits, which will be negative if this
	 * list contains only complications.
	 * @return the totalCost of the traits. 
	 */
	public int getTotalCost() {
		return totalCost;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	public void intervalRemoved(ListDataEvent e) {
		recomputeTotalCost();
	}
	
	public TraitData getLast() {
		if ( size()==0 ) {
			return null;
		}
		return get( size()-1 ) ;
	}
	
	/**
	 * Replace a trait.
	 * @param oldTrait trait being replaced.
	 * @param newTrait trait which is the replacement.
	 */
	public void replace(TraitData oldTrait, TraitData newTrait ) {
		int idx = super.indexOf( oldTrait );
		if ( idx==-1 ) {
			throw new IllegalArgumentException("Cannot replace trait, old trait not found in list.");
		}
		super.add(idx, newTrait);
		super.remove(idx+1);
	}
	
	/**
	 * Check for the existence of a trait in the list.
	 * @param trait
	 * @return
	 */
	public boolean containsTrait(Trait trait) {
		for ( TraitData tdata : this ) {
			if ( tdata.getTraitName().equals( trait.getName() ) ) {
				return true;
			}
		}
		return false;
	}
	
}
