/**
 * 
 */
package uk.lug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Performs the functions of both the list model and a list.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class MutableList<T extends Object> implements List<T> , ListModel {
	private ArrayList<T> data = new ArrayList<T>();
	private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	/**
	 * Construct a mutable list which is containts no data.
	 */
	public MutableList() {
	}
	
	/**
	 * Construct a MutableList with an initial data list.
	 * @param initialData
	 */
	public MutableList(Collection<T> initialData) {
		addAll( initialData );
	}

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return data.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	public Iterator<T> iterator() {
		return data.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return data.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	@SuppressWarnings({"hiding","unchecked"})
	public <T> T[] toArray(T[] a) {
		if (a.length < data.size())
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), data.size());
		a = data.toArray( a ) ;
		return a;
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(E)
	 */
	public boolean add(T o) {
		boolean b = data.add(o);
		
		//fire listeners
		ListDataEvent lde = new ListDataEvent( this , ListDataEvent.INTERVAL_ADDED , data.size()-1, data.size()-1 );
		fireListeners(lde);
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		int idx = data.indexOf( o );
		boolean b = data.remove(o);
		if ( idx!=-1) {
			ListDataEvent lde = new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED , idx, idx );
			fireListeners( lde );
		}
		return b;
		
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> c) {
		int indexFrom = data.size();
		boolean b = data.addAll(c);
		int indexTo = data.size()-1;
		if ( b ) {
			ListDataEvent lde = new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED , indexFrom, indexTo );
			fireListeners( lde );
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean b = data.addAll( index, c );
		if ( b ) {
			ListDataEvent lde = new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED , index , index+c.size());
			fireListeners( lde );
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		boolean b = data.removeAll( c );
		if ( b ) {
			ListDataEvent lde = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0 , data.size());
			fireListeners( lde ) ;
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		boolean b = data.retainAll( c );
		if ( b ) {
			ListDataEvent lde = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0 , data.size());
			fireListeners( lde ) ;
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	public void clear() {
		data.clear();
		ListDataEvent lde = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0 , 0);
		fireListeners( lde ) ;
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public T get(int index) {
		return data.get( index ) ;
		
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, E)
	 */
	public T set(int index, T element) {
		T res = data.set( index , element );
		ListDataEvent lde = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, index , index);
		fireListeners( lde ) ;
		return res;
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, E)
	 */
	public void add(int index, T element) {
		data.add( index, element);
		ListDataEvent lde = new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED, index , index);
		fireListeners( lde ) ;
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public T remove(int index) {
		T res = data.remove( index );
		ListDataEvent lde = new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, index , index);
		fireListeners( lde );
		return res;
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return data.indexOf( o );
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return data.lastIndexOf( o );
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<T> listIterator() {
		return data.listIterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<T> listIterator(int index) {
		return data.listIterator( index );
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List<T> subList(int fromIndex, int toIndex) {
		return data.subList( fromIndex, toIndex );
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return data.get(index);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		listeners.add( l );
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		listeners.remove( l );
		
	}
	
	/**
	 * Fires all listeners notifying them of this event.
	 * The appropriate method is called based on the event type.
	 * @param lde
	 */
	private void fireListeners(ListDataEvent lde) {
		for ( ListDataListener ldl : listeners ) {
			switch ( lde.getType() ) {
			case ListDataEvent.INTERVAL_ADDED :
				ldl.intervalAdded( lde );
				break;
			case ListDataEvent.INTERVAL_REMOVED :
				ldl.intervalRemoved( lde );
				break;
			case ListDataEvent.CONTENTS_CHANGED:
				ldl.contentsChanged( lde );
				break;
			}
		}
	}
	
	/**
	 * Checks two lists for contents, regardless of order.
	 * @param list list to compare to 
	 * @return true if both lists are of the same size and have 
	 * the same contents even if the contents are in a different order.
	 */
	public boolean contentsEqual( MutableList<T> list ) {
		if ( list.getSize()!=this.getSize() ) {
			return false;
		}
		for ( T obj : data) {
			if ( !list.contains(obj) ) {
				return false;
			}
		}
		return true;
	}
	
}
