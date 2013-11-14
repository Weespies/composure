/**
 * 
 */
package uk.lug.gui.npc.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import uk.lug.MutableList;


/**
 * @author Luggy
 * Model for holding left and right lists for selectors.  A marshaller is used
 * to move object from one side to the other.  
 * 
 * Comparators can be added to both sides to ensure the lists maintain order. 
 */
public class SelectorModel<L extends Comparable<L>, R extends Comparable<R>> {
	private SelectorMode mode = SelectorMode.MOVE_BETWEEN;
	private MutableList<L> availableList;
	private MutableList<R> selectedList;
	private SelectorMarshaller<L,R> marshaller;
	private Comparator<R> rightComparator;
	private Comparator<L> leftComparator;
	private List<L> originalLeftList;
	private List<R> originalRightList;
	


	/**
	 * @param mode
	 * @param availableList
	 * @param selectedList
	 */
	public SelectorModel(SelectorMode mode, List<L> availableList,
			List<R> selectedList, SelectorMarshaller<L,R> marshaller) {
		super();
		this.marshaller= marshaller;
		this.mode = mode;
		this.availableList = new MutableList<L>(availableList);
		this.selectedList = new MutableList<R>(selectedList);
		originalLeftList = new ArrayList<L>( availableList );
		originalRightList = new ArrayList<R>( selectedList );
	}
	
	SelectorMode getMode() {
		return mode;
	}

	/**
	 * Change selector mode.
	 * @param mode
	 */
	void setMode(SelectorMode mode) {
		this.mode = mode;
	}
	
	/**
	 * Find the correct point to insert an object in the right list,
	 * using the comparator.
	 * @param insertObject
	 * @return
	 */
	int getRightInsertIndex(R insertObject) {
		if ( rightComparator==null ) {
			return selectedList.size();
		}
		for ( int i=0; i<selectedList.size();i++ ) {
			if ( rightComparator.compare(selectedList.get(i), insertObject)>0 ) {
				return i;
			}
		}
		return selectedList.size();
	}
	
	/**
	 * Find the correct point to insert an object in the left list,
	 * using the comparator.
	 * @param insertObject
	 * @return
	 */
	int getLeftInsertIndex(L insertObject) {
		if ( leftComparator==null ) {
			return availableList.size();
		}
		for ( int i=0; i<availableList.size();i++ ) {
			if ( leftComparator.compare(availableList.get(i), insertObject)>0 ) {
				return i;
			}
		}
		return availableList.size();
	}

	/**
	 * @return a copy of the available list.
	 */
	public MutableList<L> getAvailableList() {
		return availableList;
	}
	
	/**
	 * @return a copy of the selected list
	 */
	public MutableList<R> getSelectedList() {
		return selectedList;
	}
	
	/**
	 * @param index 
	 * @return true if index within the available list is a valid add.
	 */
	public boolean canAdd(int index) {
		if ( index<0 || index>=availableList.size() ) {
			return false;
		}
		if ( mode==SelectorMode.MOVE_BETWEEN || mode==SelectorMode.ADD_SINGLE ) {
			return !selectedList.contains( availableList.get(index) );
		} else if ( mode==SelectorMode.ADD_MULTIPLE ) {
			return true;
		}
		throw new IllegalStateException("Unknown selector mode.");
	}
	
	/**
	 * Test multiple indices for an add action.
	 * @param indices 
	 * @return true all indices are valid for an add action.
	 */
	public boolean canAdd(int[] indices) {
		if ( indices==null || indices.length==0 ) {
			return false;
		}
		for ( int i : indices ) {
			if ( !canAdd(i) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Test multiple indices for a remove action.
	 * @param indices 
	 * @return true all indices are valid for an add action.
	 */
	public boolean canRemove(int[] indices) {
		if ( indices==null || indices.length==0 ) {
			return false;
		}
		for ( int i : indices ) {
			if ( !canRemove(i) ) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * @param index
	 * @return true if the index within the selected list is a valid remove.
	 */
	public boolean canRemove(int index) {
		if ( index<0 || index>=selectedList.size() ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Perform an add on an item in the available list.
	 * @param index
	 * @return true if the add was successfull.
	 */
	public boolean add( int index ) {
		if ( !canAdd(index) ) {
			return false;
		}
		
		R rightObject = marshaller.select( availableList.get(index) );
		selectedList.add( getRightInsertIndex(rightObject), rightObject );
		if ( mode==SelectorMode.MOVE_BETWEEN ) {
			availableList.remove( index );
		}
		return true;
	}
	
	/**
	 * Perform an add on multiple selections/
	 * @param indices list of indices of items to added
	 */
	public void add(int[] indices) {
		int added=0;
		//List to added
		List<L> toAdd = new ArrayList<L>( indices.length );
		for ( int i : indices ) {
			if ( canAdd(i) ) {
				toAdd.add( availableList.get(i) );
			}
		}
		
		R rightObject;
		for ( L addCandidate : toAdd ) {
			rightObject = marshaller.select( addCandidate );
			selectedList.add( getRightInsertIndex(rightObject), rightObject );
			if ( mode==SelectorMode.MOVE_BETWEEN ) {
				availableList.remove( addCandidate );
			}
		}
	}
	
	/**
	 * Perform a remove on multiple selections.
	 * @param indices list of indices of items to added
	 */
	public void remove(int[] indices) {
		int added=0;
		//List to added
		List<R> toRemove = new ArrayList<R>( indices.length );
		for ( int i : indices ) {
			if ( canRemove(i) ) {
				toRemove.add( selectedList.get(i) );
			}
		}
		
		L leftObject; 
		for ( R removeCandidate : toRemove ) {
			leftObject = marshaller.unselect( removeCandidate );
			availableList.add( getLeftInsertIndex(leftObject), leftObject );
			if ( mode==SelectorMode.MOVE_BETWEEN ) {
				selectedList.remove( removeCandidate );
			}
		}
	}
	
	/**
	 * Move a list of items from the left list to the right list.
	 * @param leftList
	 * @return the number of items successfully added.
	 */
	public int add(List<Integer> leftList) {
		int ret=0;
		List<L> toAdd = new ArrayList<L>( leftList.size() );
		for ( Integer i : leftList ) {
			if ( canAdd(i) ) {
				toAdd.add( availableList.get(i) );
			}
		}
		for ( L leftObject : toAdd ) {
			R rightObject = marshaller.select( leftObject );
			selectedList.add( getRightInsertIndex( rightObject ), rightObject);
			if ( mode==SelectorMode.MOVE_BETWEEN ) {
				availableList.remove( leftObject );
			}
			ret++;
		}
		return ret;
	}
	
	/**
	 * Add all valid selections from left to right.
	 * @return the number of additions
	 */
	public int addAll() {
		int ret=0;
		List<L> toAdd = new ArrayList<L>( availableList.size() );
		for ( int i=0;i<availableList.size();i++ ) {
			if ( canAdd(i) ) {
				toAdd.add( availableList.get(i) );
			}
		}
		for ( L leftObject : toAdd ) {
			R rightObject = marshaller.select( leftObject );
			selectedList.add( getRightInsertIndex( rightObject ), rightObject);
			if ( mode==SelectorMode.MOVE_BETWEEN ) {
				availableList.remove( leftObject );
			}
			ret++;
		}
		return ret;
	}
	
	/**
	 * Move a list of items from the right list to the left list.
	 * @param rightList
	 * @return the number of items successfully added.
	 */
	public int remove(List<Integer> rightList) {
		int ret=0;
		List<R> toRemove = new ArrayList<R>( rightList.size() ) ;
		for ( Integer i : rightList ) {
			if ( canRemove(i) ) {
				toRemove.add( selectedList.get(i) );
			}
		}
		for ( R rightObject : toRemove ) {
			selectedList.remove( rightObject ) ;
			if ( mode==SelectorMode.MOVE_BETWEEN ) {
				L leftObject = marshaller.unselect( rightObject ) ;
				availableList.add( getLeftInsertIndex(leftObject), leftObject );	
			}
			ret++;
		}
		return ret;
	}
	
	/**
	 * Add all valid selections from right to left.
	 * @return the number of removal
	 */
	public int removeAll() {
		int ret=0;
		List<R> toRemove = new ArrayList<R>( selectedList.size() ) ;
		for ( int i=0; i<selectedList.size();i++ ) {
			if ( canRemove(i) ) {
				toRemove.add( selectedList.get(i) );
			}
		}
		for ( R rightObject : toRemove ) {
			selectedList.remove( rightObject ) ;
			if ( mode==SelectorMode.MOVE_BETWEEN ) {
				L leftObject = marshaller.unselect( rightObject ) ;
				availableList.add( getLeftInsertIndex(leftObject), leftObject );	
			}
			ret++;
		}
		return ret;
	}
	
	/**
	 * Remove the item at the given index from the right side list and place it
	 * back in the left list.
	 * @param index
	 * @return true if the remove was successfull.
	 */
	public boolean remove(int index) {
		if ( !canRemove(index) ) {
			return false;
		}
		
		if ( mode==SelectorMode.MOVE_BETWEEN ) {
			L leftObject = marshaller.unselect( selectedList.get(index) ) ;
			availableList.add( getLeftInsertIndex(leftObject), leftObject);
		}
		selectedList.remove( index );
		return true;
	}

	/**
	 * @return right side list comparator, used for preserving list orders.  Null is returned if
	 * ordering is not enforced.
	 */
	public Comparator<R> getRightComparator() {
		return rightComparator;
	}

	/**
	 * Set the right side list comparator.
	 * @param rightComparator comparator for list ordering or null if ordering is not required.
	 */
	public void setRightComparator(Comparator<R> rightComparator) {
		this.rightComparator = rightComparator;
	}

	/**
	 * @return left side list comparator, used for preserving list orders.  Null is returned if
	 * ordering is not enforced.
	 */
	public Comparator<L> getLeftComparator() {
		return leftComparator;
	}
	
	/**
	 * Set the leftside list comparator.
	 * @param leftComparator comparator for list ordering or null if ordering is not required.
	 */
	public void setLeftComparator(Comparator<L> leftComparator) {
		this.leftComparator = leftComparator;
	}
	
	/**
	 * Add  new item to the left (available) list.
	 * @param leftItem
	 */
	public void addToLeftList(L leftItem) {
		availableList.add( getLeftInsertIndex( leftItem ), leftItem );
	}
	
	/**
	 * Add a new item to the right (selected) list.
	 * @param rightItem
	 */
	public void addToRightList(R rightItem) {
		selectedList.add( getRightInsertIndex(rightItem), rightItem);
	}

	/**
	 * @return true of "remove all" is valid for the current selection.
	 */
	public boolean canRemoveAll() {
		return !selectedList.isEmpty();
	}

	/**
	 * @return true if "add all" is valid for the current selection.
	 */
	public boolean canAddAll() {
		return selectedList.size()<availableList.size();
	}
	
	private boolean selectionListChanged() {
		if ( originalRightList.size()!=selectedList.size() ) {
			return true;
		}
		for ( int i=0; i<originalRightList.size(); i++ ){
			if ( originalRightList.indexOf( selectedList.get(i) )==-1 ) {
				return true;
			}
			if ( selectedList.indexOf( originalRightList.get(i) )==-1 ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSelectionChanged() {
		return selectionListChanged();
		
	}
	
}
