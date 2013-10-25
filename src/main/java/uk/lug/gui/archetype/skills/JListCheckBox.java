package uk.lug.gui.archetype.skills;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

/**
 * A simple JList (with scroll) that has a check box to the left of each item.
 */
public class JListCheckBox extends JPanel {
	public static final int ITEM_SELECTED = 1;
	public static final int ITEM_DESELECTED = 2;
	private static final long serialVersionUID = -5581917439100770158L;
	private JList list;
	private CheckableItem[] items;
	private ActionListener[] listeners=null;

	/**
	 * Creates a JListCheckBox using the given vector as the object list.*/
	public JListCheckBox(Vector listdata) {
		Object[] objs = new Object[listdata.size()];
		for (int i=0;i<objs.length;i++) {
			objs[i]=(listdata.elementAt(i));
		}
		items=createData(objs);
		final JList list = new JList(items);
		list.setCellRenderer(new CheckListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem)list.getModel().getElementAt(index);
				item.setSelected(! item.isSelected());
				Rectangle rect = list.getCellBounds(index, index);
				list.repaint(rect);
				fireListeners(index, ( item.isSelected() ? ITEM_SELECTED : ITEM_DESELECTED ) ) ;
			}
		});
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(list);
		add(scroll, BorderLayout.CENTER);

	}

	/** Creates a JListCheckBox with contents.*/
	public JListCheckBox(Object[] listdata) {
		items=createData(listdata);
		final JList list = new JList( items);
		list.setCellRenderer(new CheckListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem)list.getModel().getElementAt(index);
				item.setSelected(! item.isSelected());
				Rectangle rect = list.getCellBounds(index, index);
				list.repaint(rect);
				fireListeners(index , ( item.isSelected() ? ITEM_SELECTED : ITEM_DESELECTED ));
			}
		});
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(list);
		add(scroll, BorderLayout.CENTER);

	}
	
	/**
	 * Sets all objects to unselected.
	 * @param selected
	 */
	public void setSelected(Object[] selected) {
		for ( CheckableItem ci : items ) {
			boolean check = false;
			for (int i=0;i<selected.length ;i++) {
				if ( selected[i].toString().equals(ci.str)) {
					check=true;
					break;
				}
			}
			ci.isSelected = check;
		}
		if ( list!=null ) {
			list.repaint();
		}
	}
	
	/** Adds an actionlistener to be notified when a list item is
	 * checked or unchecked.
	 * The actionEvent's ID number is the list index number.*/
	public void addActionListener(ActionListener al) {
		if (listeners==null) {
			listeners=new ActionListener[1];
			listeners[0]=al;
		} else {
			ActionListener[] newls=new ActionListener[listeners.length+1];
			int ptr=0;
			for (int i=0;i<listeners.length;i++) {
				newls[ptr++]=listeners[i];
			}
			newls[ptr++]=al;
			listeners=newls;
		}
	}

	/** Returns a count of the number of items in the JList that are checked.*/
	public int getSelectedCount() {
		int c=0;
		for (int i=0;i<items.length;i++) {
			if (items[i].isSelected()) {
				c++;
			}
		}
		return c;
	}

	/** Fires the actionlisteners to inform them of a status selection change.
	 * param index list index of the item who's state has changed.
	 */
	private void fireListeners(int index, int selectionEventType) {
		if ( (listeners==null) || (listeners.length==0)) {
			return;
		}
		String cmd = ( selectionEventType==ITEM_SELECTED ? "SELECTED" : "UNSELECTED" );
		final ActionEvent event =new ActionEvent(this,index, cmd );
		for (int i=0;i<listeners.length;i++) {
			final ActionListener al = listeners[i];
			Runnable run = new Runnable() {
				public void run() {
					al.actionPerformed(event);
				}
			};
			Thread thread =new Thread(run,"JListCheckBox ActionEvent");
			thread.start();
		}
	}

	/** Sets the checked status for all checkbox's
	 * @param sel status to set all boxes for.*/
	public void setAllSelected(boolean sel) {
		for (int i=0;i<items.length;i++) {
			items[i].setSelected(sel);
		}
		repaint();
	}

	/** Gets the checkbox selection status the given item number.
	 * @param index list index of the item to get.
	 * Returns false if index is less than 0 or greater than the list size.*/
	public boolean getSelectionStatus(int index) {
		if ( (index<0) || (index>=items.length)) {
			return false;
		}
		return items[index].isSelected();
	}

	/** Gets the checkbox selection status the given item number.
	 * @param index list index of the item to get.
	 * Returns false if index is less than 0 or greater than the list size.*/
	public boolean isSelected(int index) {
		if ( (index<0) || (index>=items.length)) {
			return false;
		}
		return items[index].isSelected();
	}

	/** Converts an object array into an array of checkable items.*/
	private CheckableItem[] createData(Object[] objs) {
		int n = objs.length;
		CheckableItem[] items = new CheckableItem[n];
		for (int i=0;i<n;i++) {
			items[i] = new CheckableItem(objs[i].toString());
		}
		return items;
	}

	/** Class representing each checkable item.*/
	class CheckableItem {
		private String  str;
		private boolean isSelected;

		public CheckableItem(String str) {
			this.str = str;
			isSelected = false;
		}

		public void setSelected(boolean b) {
			isSelected = b;
		}

		public boolean isSelected() {
			return isSelected;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	/** Custom Cell renderer.*/
	class CheckListRenderer extends JCheckBox implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public CheckListRenderer() {
			setBackground(UIManager.getColor("List.textBackground"));
			setForeground(UIManager.getColor("List.textForeground"));
		}

		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean hasFocus)
		{
			setEnabled(list.isEnabled());
			setSelected(((CheckableItem)value).isSelected());
			setFont(list.getFont());
			setText(value.toString());
			return this;
		}
	}

	public void setAll( boolean b ) {
		for (int i=0;i<items.length ;i++) {
			items[i].setSelected( b );
		}
	}
}