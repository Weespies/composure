/* 
 * Copyright (c) 2004/5 Covalent Software Ltd
 * All rights reserved, on this planet and others.
 */
/**
 * 
 */
package uk.lug.gui.models;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * ListModel for holding a group of objects and filtering based upon them. 
 */
public class FilteringListModel<E extends Object> implements ListModel {

	private static final long serialVersionUID = 1L;
	private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
	
	private LinkedList<E> data ;
	private LinkedList<E> filteredData ;
	
	protected IObjectFilter<E> objectFilter;
	protected String currentCriteria = null;
	
	/**
	 * Construct an initially empty list with no filter.
	 */
	public FilteringListModel() {
		objectFilter=null;
		data = new LinkedList<E>();
		filteredData = new LinkedList<E>();
		applyFilter();
	}
	
	/**
	 * Construct from a List .
	 * @param filter the filter to use.
	 * @param initialData the initial contents of the model 
	 */
	public FilteringListModel(IObjectFilter<E> filter , Collection<E> initialData ) {
		objectFilter = filter;
		data = new LinkedList<E>();
		filteredData = new LinkedList<E>();
		data.addAll( initialData );
		applyFilter();
	}
	
	/**
	 * Construct from a List .
	 * @param filter the filter to use. 
	 */
	public FilteringListModel(IObjectFilter<E> filter ) {
		objectFilter = filter;
		data = new LinkedList<E>();
		filteredData = new LinkedList<E>();
		applyFilter();
	}


	/**
	 * Apply the current filter and criteria to the datamodel 
	 */
	private void applyFilter() {
		//Clear out old data
		filteredData.clear();
		
		//Add new filtered data
		if ( objectFilter==null || currentCriteria==null || currentCriteria.trim().length()==0 ) {
			//No filter
			filteredData.addAll( data );
		} else {
			//Old, check one at a time and apply if matching
			objectFilter.setCriteria( currentCriteria );
			for ( E item : data ) {
				if ( objectFilter.matches( item ) ) {
					filteredData.add( item );
				}
			}
		}
		fireContentsChanged( 0  ,filteredData.size() ) ;	
	}
	
	/**
	 * Returns all items that match a given string using the current filter.
	 * @param s String to check (e.g. "Jones", "Pr", "Finance")
	 * @return the items in a LinkedList.  List will be empty (but not null) if no matches can be found.
	 */
	public LinkedList<E> findMatches( String s ) {
		LinkedList<E> res = new LinkedList<E>();
		objectFilter.setCriteria( s );
		for ( E item : data ) {
			if ( objectFilter.matches( item ) ) {
				res.add( item );
			}
		}
		objectFilter.setCriteria( currentCriteria );
		return res;
	}
	
	/**
	 * @return the current data list, unfiltered.
	 */
	public LinkedList<E> getData() {
		return data;
	}
	
	/**
	 * @return the current data list, filtered.
	 */
	public LinkedList<E> getFilteredData() {
		return filteredData;
	}
	
	/**
	 * Remove & return all currently filtered items.
	 * @return list of all items that are not filtered.
	 */
	public LinkedList<E> extractFiltered() {
		LinkedList<E> res = new LinkedList<E>();
		for ( E item : filteredData ) {
			res.add( item ) ;
		}
		remove( res );
		applyFilter();
		return res;
	}
	
	/**
	 * Remove the contents of a Collection .
	 * @param toRemove
	 */
	public void remove( Collection<E> toRemove ) {
		for ( E item : toRemove ) {
			if ( data.contains( item ) ) {
				data.remove( item );
			}
		}
		applyFilter();
	}
	
	/**
	 * Adds the entire contents of a Collection to this model. 
	 * @param toAdd collection of items to add to this list.
	 */
	public void addAll( Collection<E> toAdd) {
		for ( E item : toAdd ) {
			if ( !data.contains( item ) ) {
				data.add( item );
			}
		}
		applyFilter();
	}

	/**
	 * Remove all contents from the datamodel.
	 */
	public void clear() {
		data.clear();
		applyFilter();
	}
	
	/**
	 * Add a single item to the bottom of the list.
	 */
	public void add( E item ) {
		data.add( item );
		applyFilter();
	}
	
	/**
	 * Clear all items in the datamodel that currently match the filter.
	 */
	public void removeFiltered() {
		for ( E item : data ) {
			if ( objectFilter.matches( item ) ) {
				data.remove( item );
			}
		}
		applyFilter();
	}
	
	/**
	 * Remove a single item from the data.
	 */
	public void remove( E item ) {
		data.remove( item );
		applyFilter();
		
	}
	
	/**
	 * Fires all listeners to notify them of the removal of a given set of entries
	 * @param index1 the position with the model of the first entry removed.
	 * @param index1 the position with the model of the last entry removed.
	 */
	private void fireRemoveEvent(int index1, int index2) {
		if ( index1> index2 ) {
			//You got the order the wrong way around you spoon.
			int tmp = index2;
			index2=index1;
			index1=tmp;
		}
		ListDataEvent event = new ListDataEvent( this , ListDataEvent.INTERVAL_REMOVED , index1 , index2 );
		for ( ListDataListener ldl : listeners ) {
			ldl.intervalRemoved( event );
		}
	}

	/**
	 * Fires all listeners to notify them of the addition of a given set of entries
	 * @param index1 the position with the model of the first entry added.
	 * @param index1 the position with the model of the last entry added.
	 */
	private void fireAddedEvent(int index1, int index2) {
		if ( index1> index2 ) {
			//You got the order the wrong way around you spoon.
			int tmp = index2;
			index2=index1;
			index1=tmp;
		}
		ListDataEvent event = new ListDataEvent( this , ListDataEvent.INTERVAL_ADDED , index1 , index2 );
		for ( ListDataListener ldl : listeners ) {
			ldl.intervalAdded( event );
		}
	}

	/**
	 * Fires all listeners to notify them of a change to contents between two points.
	 * @param index1 the position with the model of the first entry changed.
	 * @param index1 the position with the model of the last entry changed.
	 */
	private void fireContentsChanged(int index1, int index2) {
		if ( index1> index2 ) {
			//You got the order the wrong way around you spoon.
			int tmp = index2;
			index2=index1;
			index1=tmp;
		}
		ListDataEvent event = new ListDataEvent( this , ListDataEvent.INTERVAL_ADDED , index1 , index2 );
		for ( ListDataListener ldl : listeners ) {
			ldl.intervalAdded( event );
		}
	}


	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return filteredData.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return filteredData.get( index );
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		if ( listeners.contains( l ) ) {
			listeners.remove( l );
		}
		listeners.add( l );
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		if ( listeners.contains( l ) ) {
			listeners.remove( l ) ;
		}
	}
	
	/**
	 * Fires a "contents changed" event for all data listeners.
	 * @param lde
	 */
	private void fireContentsChanged( ListDataEvent lde ) {
		for ( ListDataListener ldl : listeners ) {
			ldl.contentsChanged( lde );
		}
	}

	/**
	 * @return Returns the current filtering criteria.
	 */
	public String getFilterCriteria() {
		return currentCriteria;
	}

	/**
	 * @param currentCriteria the current filtering criteria
	 */
	public void setFilterCriteria(String currentCriteria) {
		this.currentCriteria = currentCriteria;
		applyFilter();
	}

	/**
	 * @return Returns the objectFilter.
	 */
	public IObjectFilter<E> getObjectFilter() {
		return objectFilter;
	}

	/**
	 * @param objectFilter The objectFilter to set.
	 */
	public void setObjectFilter(IObjectFilter<E> objectFilter) {
		this.objectFilter = objectFilter;
	}
	
	/**
	 * @return indices of all items within the unfiltered list that do not appear
	 * in the filtered list. 
	 */
	public int[] getFilteredIndices() {
		int[] res= new int[ filteredData.size() ];
		for ( int i=0; i<res.length; i++ ) {
			res[i] = i;
		}
		return res;
	}
	
	
}
