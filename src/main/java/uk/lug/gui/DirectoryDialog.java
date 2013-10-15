/**
 * 
 */
package uk.lug.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * Copyright 3 Jun 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public class DirectoryDialog extends JDialog implements DirectorySelectionListener {
	private static final long serialVersionUID = 1L;
	private static final Dimension MINIMUM_SIZE = new Dimension(240,300);
	private static final Dimension INITIAL_SIZE = new Dimension(300,400);
	private static final Icon OK_ICON = CachedImageLoader.getCachedIcon("images/check.png");
	private static final Icon CANCEL_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	private JButton okButton;
	private JButton cancelButton;
	private File initialDir;
	private DirectoryChooser chooser;
	private String message = "The data files for this application need to be extracted locally " +
	"so that they can be test.  Please choose where to store this files.";
	private File selection;
	private String dialogTitle="Directory Chooser";
	
	private Component parent;
	
	
	
	/**
	 * @param initialDir
	 * @param message
	 * @param dialogTitle
	 * @param parent
	 */
	private DirectoryDialog(Component parent, File initialDir, String message, String dialogTitle ) {
		super();
		setModal(true);
		this.initialDir = initialDir;
		this.message = message;
		this.dialogTitle = dialogTitle;
		this.parent = parent;

		buildGUI();
		orientToParent();
	}
	
	/**
	 * Position the dialog as appropriate to the parent component (if any)
	 */
	private void orientToParent() {
		Point center = getParentCenter();
		center.x = center.x - getWidth()/2;
		center.y = center.y - getHeight()/2;
		setLocation( center );
	}
	
	/**
	 * @return the center of the parent component or the screen center if the parent is
	 * null or not visible.
	 */
	private Point getParentCenter() {
		Point ret;
		if ( parent==null || !parent.isVisible() ) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			ret= new Point( screenSize.width/2, screenSize.height/2 );
		} else {
			ret = new Point( parent.getLocationOnScreen() );
			ret.x =ret.x+(parent.getWidth()/2);
			ret.y =ret.y+(parent.getHeight()/2);
		}
		return ret;
	}

	/**
	 * Create the user interface
	 */
	private void buildGUI() {
		setTitle( dialogTitle );
		
		setLayout ( new GridBagLayout() );
		okButton = new JButton( okAction );
		
		chooser = new DirectoryChooser( initialDir, message );
		add( chooser, new GridBagConstraints(0,0, 2,1, 1.0,1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2,2,2,2), 0,0));
		chooser.addSelectionListener( this );
		
		//OK Button
		add( okButton, new GridBagConstraints(0,10, 1,1, 0,0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2,5,2,2), 0,0));
		okAction.setEnabled( chooser.getSelectedDirectory()!=null);
		
		//Cancel butotn
		cancelButton = new JButton( cancelAction);
		add( cancelButton, new GridBagConstraints(1,10, 1,1, 0,0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2,5,2,2), 0,0));
		
		setSize( INITIAL_SIZE );
		setMinimumSize( MINIMUM_SIZE );
	}
	
	/**
	 * Action invoked by the OK button.
	 */
	private Action okAction = new AbstractAction("OK", OK_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doOK();
		}
	};
	
	/**
	 * Action invoked by the cancel button.
	 */
	private Action cancelAction = new AbstractAction("Cancel", CANCEL_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doCancel();
		}
	};
	

	/**
	 * OK this dialog.
	 */
	protected void doOK() {
		selection = chooser.getSelectedDirectory();
		this.setVisible(false);
		
	}

	/**
	 * Cancel this dialog
	 */
	protected void doCancel() {
		selection=null;
		this.setVisible(false);
	}

	/* (non-Javadoc)
	 * @see lug.gui.DirectorySelectionListener#directorySelected(java.io.File, java.io.File)
	 */
	public void directorySelected(File oldSelection, File newSelection) {
		okAction.setEnabled( newSelection!=null && newSelection.isDirectory() );
	}

	/**
	 * @return the selected directory or null if dialog was cancelled.
	 */
	public File getSelectedDirectory() {
		return selection;
	}	
	
	/**
	 * Show a standard directory chooser dialog.  Initial message is "Choose a directory",
	 * the initial diectory is the user home directory and the dialog title is "Directory Chooser".
	 * Dialog will be centered on the screen
	 * @return File selected by the directory dialog or null if the selected was cancelled.
	 */
	public static File showDirectoryDialog() {
		File initDir= new File( System.getProperty("user.home") );
		String title = "Directory Chooser";
		String message = "Choose a directory";
		DirectoryDialog dialog = new DirectoryDialog( null, initDir, message, title );
		dialog.setVisible(true);
		return dialog.getSelectedDirectory();
	}

	/**
	 * Show a standard directory chooser dialog, defaulting to the user home directory.  
	 * @param parentComponent Component around which the dialog will be centered.
	 * @param message Information message shown at the top of the dialog.
	 * @return The selected File object for the chosen directory or null if cancel was selected.
	 */
	public static File showDirectoryDialog(Component parentComponent, String message) {
		File initDir= new File( System.getProperty("user.home") );
		DirectoryDialog dialog = new DirectoryDialog( parentComponent, initDir, message, "Choose a directory.");
		dialog.setVisible(true);
		return dialog.getSelectedDirectory();
	}

	/**
	 * Show a standard directory chooser dialog, defaulting to the user home directory.  
	 * @param parentComponent Component around which the dialog will be centered.
	 * @param message Information message shown at the top of the dialog.
	 * @param title title for the dialog window. 
	 * @return The selected File object for the chosen directory or null if cancel was selected.
	 */
	public static File showDirectoryDialog(Component parentComponent, String message, String title) {
		File initDir= new File( System.getProperty("user.home") );
		DirectoryDialog dialog = new DirectoryDialog( parentComponent, initDir, message, title );
		dialog.setVisible(true);
		return dialog.getSelectedDirectory();
	}
	
	/**
	 * Show a standard directory chooser dialog.  
	 * @param parentComponent Component around which the dialog will be centered.
	 * @param initialDirectory initialy selected directory path.
	 * @param message Information message shown at the top of the dialog.
	 * @param title title for the dialog window. 
	 * @return The selected File object for the chosen directory or null if cancel was selected.
	 */
	public static File showDirectoryDialog(Component parentComponent, File initialDirectory, String message, String title) {
		DirectoryDialog dialog = new DirectoryDialog( parentComponent, initialDirectory, message, title );
		dialog.setVisible(true);
		return dialog.getSelectedDirectory();
	}

}
