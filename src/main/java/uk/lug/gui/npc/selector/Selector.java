/**
 * 
 */
package uk.lug.gui.npc.selector;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.lug.gui.util.CachedImageLoader;

/**
 * @author Luggy
 *
 */
public class Selector<L extends Comparable<L>, R extends Comparable<R>> extends JPanel implements ListSelectionListener {
	private static final Icon DEFAULT_ADD_ICON = CachedImageLoader.getCachedIcon("images/right_arrow.png");
	private static final Icon DEFAULT_ADD_ALL_ICON = CachedImageLoader.getCachedIcon("images/right_double_arrow.png");
	private static final Icon DEFAULT_REMOVE_ICON = CachedImageLoader.getCachedIcon("images/left_arrow.png");
	private static final Icon DEFAULT_REMOVE_ALL_ICON = CachedImageLoader.getCachedIcon("images/left_double_arrow.png");
	private HashMap<String, Object> properties = new HashMap<String,Object>();
	
	private SelectorModel<L,R> selectorModel;
	
	private JList leftList;
	private JList rightList;
	private JButton addButton;
	private JButton addAllButton;
	private JButton removeButton;
	private JButton removeAllButton;
	private JScrollPane leftScroll;
	private JScrollPane rightScroll;
	private JPanel centerPanel;
	
	private AbstractAction addAction = new AbstractAction( "Add", DEFAULT_ADD_ICON) {
		{
			super.putValue( Action.LONG_DESCRIPTION, "Add selected entries" );
		}

		public void actionPerformed(ActionEvent e) {
			performAdd();
		}
		
	};
	
	private AbstractAction addAllAction = new AbstractAction("Add all", DEFAULT_ADD_ALL_ICON) {
		{
			super.putValue( Action.LONG_DESCRIPTION, "Add all valid entries" );
		}

		public void actionPerformed(ActionEvent e) {
			performAddAll();
		}
		
	};
	
	private AbstractAction removeAction = new AbstractAction("Remove",DEFAULT_REMOVE_ICON) {
		{
			super.putValue( Action.LONG_DESCRIPTION, "Remove selected entries" );
		}

		public void actionPerformed(ActionEvent e) {
			performRemove();
		}
		
	};
	
	private AbstractAction removeAllAction = new AbstractAction("Remove all", DEFAULT_REMOVE_ALL_ICON) {
		{
			super.putValue( Action.SMALL_ICON, DEFAULT_REMOVE_ALL_ICON );
		}

		public void actionPerformed(ActionEvent e) {
			performRemoveAll();
		}
		
	};

	/**
	 * @param selectorModel
	 */
	public Selector(SelectorModel<L, R> selectorModel) {
		this.selectorModel = selectorModel;
		buildGUI();
	}

	public Selector(SelectorMode mode, List<L> availableList, List<R> selectedList,SelectorMarshaller<L,R> marshaller) {
		selectorModel = new SelectorModel<L,R>( mode, availableList, selectedList,marshaller);
		buildGUI();
	}

	/**
	 * 
	 */
	private void buildGUI() {
		buildLeftListComponent();
		buildRightListComponent();
		buildCentrePanel();
		buildMain();
		enableAddRemoveActions();
	}

	/**
	 * 
	 */
	private void buildMain() {
		setLayout( new GridBagLayout() );
		add( leftScroll, new GridBagConstraints(0,0, 1,3 ,0.5,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0) );
		add( centerPanel, new GridBagConstraints(1,0, 1,3, 0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2,2,2,2),0,0) );
		add( rightScroll, new GridBagConstraints(2,0, 1,3, 0.5,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0) );
	}
	
	/**
	 * Turn each action on or off as it's revelant scroll component dictates
	 */
	private void enableAddRemoveActions() {
		addAction.setEnabled( selectorModel.canAdd(leftList.getSelectedIndices()) );
		addAllAction.setEnabled(selectorModel.canAddAll());
		removeAction.setEnabled(selectorModel.canRemove(rightList.getSelectedIndices()) );
		removeAllAction.setEnabled(selectorModel.canRemoveAll() );
	}

	/**
	 * Build the center panels
	 */
	protected void buildCentrePanel() {
		addAllButton = new JButton( addAllAction );
		addAllButton.setHorizontalTextPosition(SwingConstants.LEFT );
		addButton = new JButton( addAction );
		addButton.setHorizontalTextPosition(SwingConstants.LEFT );
		removeButton = new JButton( removeAction );
		removeButton.setHorizontalTextPosition(SwingConstants.RIGHT );
		removeAllButton = new JButton( removeAllAction );
		removeAllButton.setHorizontalTextPosition(SwingConstants.RIGHT );
		
		addButton.setMargin( new Insets(2,0,2,0) );
		addAllButton.setMargin( new Insets(2,0,2,0) );
		removeButton.setMargin( new Insets(2,0,2,0) );
		removeAllButton.setMargin( new Insets(2,0,2,0) );
		
		GridLayout gridlayout = new GridLayout(4,1);
		gridlayout.setVgap(5);
		centerPanel = new JPanel( gridlayout );
		centerPanel.add( addButton );
		centerPanel.add( addAllButton );
		centerPanel.add( removeButton );
		centerPanel.add( removeAllButton );
		addButton.setName("addButton");
		addAllButton.setName("addAllButton");
		removeButton.setName("removeButton");
		removeAllButton.setName("removeAllButton");
	}

	/**
	 * Build the right (selected) list
	 */
	protected void buildRightListComponent() {
		rightList = new JList( selectorModel.getSelectedList() );
		rightScroll = new JScrollPane( rightList );
		rightList.addListSelectionListener( this );
		rightList.addMouseListener( new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getClickCount()>1 ) {
					performQuickRemove( leftList.locationToIndex( e.getPoint() ) );
				}
			}
		});
		rightList.setName("rightList");
		rightScroll.setName("rightScroll");
	}

	/**
	 * @param locationToIndex
	 */
	protected void performQuickRemove(int idx) {
		List<R> toRemove = new ArrayList<R>(1);
		toRemove.add( selectorModel.getSelectedList().get(idx) );
		preRemove( toRemove ) ;
		selectorModel.remove( new int[]{idx} );
		postRemove();
	}

	/**
	 * build the left side (selected) list.
	 */
	protected void buildLeftListComponent() {
		leftList = new JList( selectorModel.getAvailableList() );
		leftScroll = new JScrollPane( leftList );
		leftList.addListSelectionListener( this );
		leftList.addMouseListener( new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getClickCount()>1 ) {
					performQuickAdd( leftList.locationToIndex( e.getPoint() ) );
				}
			}
		});
		leftList.setName("leftList");
		leftScroll.setName("leftScroll");
		
	}
	
	/**
	 * Perform a quick remove
	 * @param idx index of the left item to remove.
	 */
	protected void performQuickAdd(int idx) {
		List<L> toAdd = new ArrayList<L>(1);
		toAdd.add( selectorModel.getAvailableList().get(idx) );
		preAdd( toAdd );
		selectorModel.add( new int[]{idx} );
		postAdd();
	}

	private int maxOf( int... values ) {
		int ret =0;
		
		for ( int i :values ) {
			if ( i>ret ) {
				ret=i;
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		enableAddRemoveActions();
	}
	

	
	/**
	 * Performs the add selected function.
	 */
	protected void performRemoveAll() {
		preRemoveAll();
		selectorModel.removeAll();
		postRemoveAll();
	}

	/**
	 * Performed just after an add all operation.
	 */
	protected void postRemoveAll() {
		enableAddRemoveActions();
	}

	/**
	 * Performed just after an add all operation.
	 */
	protected void preRemoveAll() {
	}

	/**
	 * Performs the all all function 
	 */
	protected void performAddAll() {
		preAddAll();
		selectorModel.addAll();
		postAddAll();
	}

	/**
	 * Performed just after an add all operation.
	 */
	protected void postAddAll() {
		enableAddRemoveActions();
	}

	/**
	 * Performed just after an add all operation.
	 */
	protected void preAddAll() {
	}

	/**
	 * Performs the remove selected function.
	 */
	protected void performRemove() {
		int[] indices = rightList.getSelectedIndices();
		List<R> toRemove = new ArrayList<R>();
		for ( int i : indices ) {
			toRemove.add( selectorModel.getSelectedList().get(i) );
		}
		preRemove( toRemove ) ;
		selectorModel.remove( indices );
		postRemove();
	}
	
	/**
	 * Perform just prior to a remove action.
	 * @param toRemove list of items to be removed.
	 */
	protected void preRemove(List<R> toRemove) {
	}
	
	/**
	 * Performed just after a remove operation.
	 */
	protected void postRemove() {
		enableAddRemoveActions();
	}

	/**
	 * Perform the remove all function
	 */
	protected void performAdd() {
		int[] indices = leftList.getSelectedIndices();
		List<L> toAdd = new ArrayList<L>(indices.length);
		for ( int i : indices) {
			toAdd.add( selectorModel.getAvailableList().get(i) );
		}
		preAdd( toAdd );
		selectorModel.add( indices );
		postAdd();
	}
	
	

	/**
	 * Called just after an add operation has been performed.
	 */
	private void postAdd() {
		enableAddRemoveActions();
	}

	/**
	 * Called just prior to an add operation.
	 * @param toAdd list of items about to be added. 
	 */
	protected void preAdd(List<L> toAdd) {
	}
	
	/**
	 * Set a cell renderer for rendering the left list entries.
	 * @param renderer
	 */
	public void setLeftCellRenderer( CellRenderer<JList,L> renderer ) {
		System.out.println("Renderer set");
		leftList.setCellRenderer( new LeftCellRenderer(renderer) );
	}
	
	/**
	 * Set a cell renderer for rendering the right list entries.
	 * @param renderer
	 */
	public void setRightCellRenderer( CellRenderer<JList,R> renderer ) {
		rightList.setCellRenderer( new RightCellRenderer(renderer) );
	}
	
	/**
	 * Wrapper for placing CellRenderer into a ListCellRenderer for the available list.
	 * @author Luggy
	 *
	 */
	class LeftCellRenderer implements ListCellRenderer{
		private CellRenderer<JList,L> subrenderer;	
		
		/**
		 * @param subrenderer
		 */
		public LeftCellRenderer(CellRenderer<JList, L> subrenderer) {
			this.subrenderer = subrenderer;
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return subrenderer.getRenderedCellComponentCellRendererComponent(leftList,(L)value,index,0,isSelected,cellHasFocus);
		}		
	}
	
	/**
	 * Wrapper for placing CellRenderer into a ListCellRenderer for the selection list.
	 * @author Luggy
	 *
	 */
	class RightCellRenderer implements ListCellRenderer{
		private CellRenderer<JList,R> subrenderer;	
		
		/**
		 * @param subrenderer
		 */
		public RightCellRenderer(CellRenderer<JList, R> subrenderer) {
			this.subrenderer = subrenderer;
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return subrenderer.getRenderedCellComponentCellRendererComponent(leftList,(R)value,index,0,isSelected,cellHasFocus);
		}		
	}
	
	/**
	 * @return the model used by this selector.
	 */
	public SelectorModel<L,R> getModel() {
		 return selectorModel;
	}
}


