/**
 * 
 */
package uk.lug.serenity.npc.gui.equipment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.lug.MutableList;
import uk.lug.gui.CachedImageLoader;
import uk.lug.serenity.npc.managers.EquipmentManager;
import uk.lug.serenity.npc.model.equipment.Equipment;
import uk.lug.util.RandomFactory;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>List to allow the addition and removal of items from a catalog.</p>
 * 
 * @author Luggy
 *
 */
public class EquipmentSelector<eqType extends Equipment> extends JPanel {
	public static final String REMOVE_BUTTON = "removeButton";
	public static final String ADD_BUTTON = "addButton";
	public static final String CATALOG_SCROLL = "catalogScroll";
	public static final String SELECTED_SCROLL = "selectedScroll";
	public static final String RIGHT_PANEL = "rightPanel";
	public static final String LEFT_PANEL = "leftPanel";
	public static final String SPLIT_PANE = "splitPane";
	public static final String CATALOG_LIST = "catalogList";
	public static final String SELECTED_LIST = "selectedList";
	private static final long serialVersionUID = 1L;
	private MutableList<eqType> selected, catalog;
	private JScrollPane selectedScroll;
	private JScrollPane catalogScroll;
	private JList selectedList;
	private JList catalogList;
	private JSplitPane splitPane;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private static final Icon REMOVE_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	private static final Icon ADD_ICON = CachedImageLoader.getCachedIcon("images/add.png");
	private JButton addButton;
	private JButton removeButton;
	
	private Action addAction = new AbstractAction("Add Selected" , ADD_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doAddSelected();
		}
	};
	
	private Action removeAction = new AbstractAction("Remove Selected" , REMOVE_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doRemoveSelected();
		}
	};
		
	/**
	 * Construct a selector panel.
	 * @param selected
	 * @param catalog
	 */
	public EquipmentSelector(MutableList<eqType> selected, MutableList<eqType> catalog) {
		super();
		this.selected = selected;
		this.catalog = catalog;
		makeGUI();
	}
	
	/**
	 * Remove currently selected items
	 */
	protected void doRemoveSelected() {
		int[] sels = selectedList.getSelectedIndices();
		for ( int i : sels ) {
			selected.remove( i );
		}
		selectedList.setSelectedIndices( new int[0]);
		selectedList.repaint();
	}
	
	/**
	 * Add currently select items to the selection list on the left
	 */
	protected void doAddSelected() {
		int[] sels = catalogList.getSelectedIndices();
		for ( int i : sels ) {
			doQuickAdd(i);
		}
		
		selectedList.repaint();
		catalogList.setSelectedIndices( new int[0]);
		catalogList.repaint();
	}
	
	

	/**
	 * Construct user interface.
	 */
	private void makeGUI() {
		setLayout( new BorderLayout() );
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitPane.setDividerLocation(0.5d);
		
		//build left panel
		leftPanel = new JPanel( new BorderLayout() );
		selectedList = new JList();
		selectedList.setModel( selected );
		selectedScroll = new JScrollPane( selectedList );
		removeButton = new JButton( removeAction );
		leftPanel.add( selectedScroll, BorderLayout.CENTER );
		leftPanel.add( removeButton , BorderLayout.SOUTH );
		
		
		//build right panel
		rightPanel = new JPanel( new BorderLayout() );
		catalogList= new JList( catalog ) ;
		catalogScroll = new JScrollPane( catalogList );
		addButton = new JButton( addAction );
		rightPanel.add( catalogScroll, BorderLayout.CENTER );
		rightPanel.add( addButton, BorderLayout.SOUTH );
		
		splitPane.setLeftComponent( leftPanel );
		splitPane.setRightComponent( rightPanel );
		

		selectedList.setCellRenderer( Equipment.getCellRenderer() );
		catalogList.setCellRenderer( Equipment.getCellRenderer() );
		
		splitPane.setDividerSize(5);	
		add(splitPane, BorderLayout.CENTER );
		
		//activate/deactive lists based on selection
		selectedList.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				removeAction.setEnabled( selectedList.getSelectedIndices().length>0 );
			}});
		catalogList.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				addAction.setEnabled( catalogList.getSelectedIndices().length>0 );
			}});
		removeAction.setEnabled(false);
		addAction.setEnabled(false);
		
		//Quick add with double click
		MouseListener mouseListener = new MouseAdapter() {
		    @Override
			public void mouseClicked(MouseEvent e) {
		         if (e.getClickCount() >= 2) {
		        	 int index = catalogList.locationToIndex(e.getPoint());
		             doQuickAdd( index );
		             e.consume();
		          }
		     }
		};
		catalogList.addMouseListener(mouseListener);
		MouseListener mouseListener2 = new MouseAdapter() {
		    @Override
			public void mouseClicked(MouseEvent e) {
		         if (e.getClickCount() >= 2) {
		             int index = catalogList.locationToIndex(e.getPoint());
		             doQuickRemove( index );
		             e.consume();
		          }
		     }
		};
		selectedList.addMouseListener(mouseListener2);
		
		selectedList.setName(SELECTED_LIST);
		catalogList.setName(CATALOG_LIST);
		splitPane.setName(SPLIT_PANE);
		leftPanel.setName(LEFT_PANEL);
		rightPanel.setName(RIGHT_PANEL);
		selectedScroll.setName(SELECTED_SCROLL);
		catalogScroll.setName(CATALOG_SCROLL);
		addButton.setName(ADD_BUTTON);
		removeButton.setName(REMOVE_BUTTON);
	}
	
	/**
	 * Quickly add an item from the right list to the left.
	 * @param index
	 */
	public void doQuickAdd(int index) {
		selected.add( catalog.get( index ) );
		selectedList.setBackground(Color.WHITE);
	}
	
	/**
	 * Quickly add an item from the right list to the left.
	 * @param index
	 */
	public void doQuickRemove(int index) {
		selected.remove( index );
		
	}

	/**
	 * @return the list of items that can be added
	 */
	public MutableList<eqType> getCatalogList() {
		return catalog;
	}
	/**
	 * @param catalogList the list of items to be added
	 */
	public void setCatalogList(MutableList<eqType> catalogList) {
		this.catalog = catalogList;
	}
	
	/**
	 * @return the list of currently selected items.
	 */
	public MutableList<eqType> getSelectedList() {
		return selected;
	}
	
	/**
	 * @param selectedList list of currently selected items.
	 */
	public void setSelectedList(MutableList<eqType> newSelected) {
		this.selected = newSelected;
		selectedList.setModel( new DefaultListModel() );
		selectedList.setModel( this.selected );
		selectedList.setSelectedIndices( new int[0] );
		selectedList.repaint();
		
	}

	
}