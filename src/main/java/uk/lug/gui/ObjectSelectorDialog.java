package uk.lug.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Simple dialog to select from a list of choices.
 * @author Luggy
 *
 */
public class ObjectSelectorDialog extends JPanel {
	private static final Icon OK_ICON = CachedImageLoader.getCachedIcon("images/check.png");
	private static final Icon CANCEL_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	
	private Object[] options=null;
	private int choice=-1;
	private JList selection=null;
	private boolean dialogOk;
	private JDialog dialog;
	private boolean cancelAllowed = true;
	
	private JButton select;
	private JButton cancel;

	private Action selectAction = new AbstractAction( "Select", OK_ICON ) {
		public void actionPerformed( ActionEvent ae ) {
			int c=currentSelectionIndex();
			setCurrentChoice(c);
			dialogOk=true;
			dialog.setVisible(false);
		}
	};
	
	private Action cancelAction = new AbstractAction( "Cancel", CANCEL_ICON) {
		public void actionPerformed( ActionEvent ae ) {
			setCurrentChoice(-1);
			dialog.setVisible(false);
			dialogOk=false;
		}
	};
	
	/**
	 * Construct a list dialog from.
	 * @param choices Choices that can be made
	 * @param defChoice The default (initial) choice
	 * @param canCancel can this dialog be cancelled.
	 */
	public ObjectSelectorDialog(Object[] choices, Object defChoice, boolean canCancel) {
		options=choices;
		cancelAllowed=canCancel;
		createGUI();
		if ( defChoice!=null ) {
			for ( int i=0; i<choices.length; i++) {
				if ( choices[i].equals( defChoice ) ) {
					choice=i;
					break;
				}
			}
		}
	}
	
	/**
	 * Set's the cell renderer for the list.
	 * @param renderer
	 */
	public void setCellRenderer( ListCellRenderer renderer ) {
		selection.setCellRenderer( renderer );
		selection.repaint();
	}
	
	/**
	 * Construct the user interface.
	 */
	private void createGUI() {
				
		selection= new JList( options );
		selection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selection.setSelectedIndex( choice );

		//Create listview
		JScrollPane scrollPane = new JScrollPane(selection);
		setLayout(new BorderLayout());
		add(scrollPane,BorderLayout.CENTER);

		//Create OK Cancel Buttons
		JPanel panel = new JPanel( new GridBagLayout() );
		
		select = new JButton( selectAction );
		cancel= new JButton( cancelAction );
		
		
		//OK and cancel buttons
		if ( cancelAllowed ) {
			panel.add(select, new GridBagConstraints(0, 0, 1, 1, 0.5, 0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 2, 0, 2), 0, 0));
			panel.add(cancel, new GridBagConstraints(1, 0, 1, 1, 0.5, 0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 2, 0, 2), 0, 0));
			add(panel,BorderLayout.SOUTH);	
		} else {
			panel.add(select, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 2, 0, 2), 0, 0));
			add(panel,BorderLayout.SOUTH);
		}
		MouseListener mouseListener = new MouseAdapter() {
		     @Override
			public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() > 1) {
					int index = selection.locationToIndex(e.getPoint());
					setCurrentChoice(index);
					dialogOk = true;
					dialog.setVisible(false);
				}
		     }
		 };
		 selection.addMouseListener(mouseListener);
		 selectAction.setEnabled(false);
		 selection.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				selectAction.setEnabled( selection.getSelectedIndex()!=-1 );
			}});
		//Set size of dialog
	}

	public boolean showDialog(Frame parent,String title) {
		dialogOk=false;

		Frame owner=null;
		if (parent instanceof Frame) {
			owner=(Frame)parent;
		} else {
			owner=(Frame)SwingUtilities.getAncestorOfClass(Frame.class,parent);
		}

		if (dialog==null || dialog.getOwner() != owner) {
			owner=null;
			dialog=new JDialog(owner,true);
			dialog.getContentPane().add(this);
			dialog.setSize(200,300);
		}

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension d2=dialog.getSize();
		int x=(d.width/2)-(d2.width/2);
		int y=(d.height/2)-(d2.height/2);

		dialog.setTitle(title);
		dialog.setLocation(x,y);

		dialog.setVisible(true);
		return dialogOk;
	}

	/** Returns the currently selected index*/
	public int getSelectionIndex() {
		return choice;
	}

	/** Returns the selected choice*/
	public Object getSelection() {
		if (choice==-1) {
			return null;
		}
		if (options==null) {
			return null;
		}
		return options[choice];
	}

	private int currentSelectionIndex() {
		return selection.getSelectedIndex();
	}

	private void setCurrentChoice(int c) {
		choice=c;
	}
}
