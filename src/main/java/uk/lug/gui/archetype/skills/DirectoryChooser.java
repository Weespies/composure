/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Copyright 3 Jun 2007
 * @author Paul Loveridge
 * <p>GUI panel for choosing a directory path.</p>
 * 
 */
public class DirectoryChooser extends JPanel implements DirectorySelectionListener {
	private static final long serialVersionUID = 1L;
	private static final Insets INSETS = new Insets(0,2,0,2);
	private JDirectoryTree dirTree;
	private JTextArea messageArea;
	private JLabel pathLabel;
	private JButton changeButton;
	private JButton mkdirButton;
	private File currentSelection;
	private JPanel selectPanel;
	private JLabel paddingLabel;
	private List<DirectorySelectionListener> listeners ;
	
	private String initialMessage;
	
	/**
	 * Construct a directoryPanel
	 */
	public DirectoryChooser(File directory, String message) {
		super();
		listeners = new ArrayList<DirectorySelectionListener>(3);
		currentSelection = new File(directory.getAbsolutePath() );
		initialMessage = message;
		buildGUI();
	}

	/**
	 * Build the user interface 
	 */
	private void buildGUI() {
		setLayout( new GridBagLayout() ) ;
		
		messageArea = new JTextArea( initialMessage );
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord( true );
		messageArea.setOpaque( false );
		messageArea.setEditable( false );
		add( messageArea, new GridBagConstraints(0,0, 2,1, 1.0,0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(0,2,10,2), 0,0) );
		
		
		createSelectionLabel();
		add( selectPanel, new GridBagConstraints(0,1, 2,1, 1.0, 0, GridBagConstraints.WEST, 
				GridBagConstraints.HORIZONTAL, INSETS, 0,0) );
		
		//Adds the label to take up the padding.
		paddingLabel = new JLabel(" ");
		add( paddingLabel, new GridBagConstraints(0,5, 2,1, 1.0,1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, INSETS,0,0));
		
	}

	
	
	/**
	 * Create the panel that shows the currently selected directory and
	 * the button allowing the jdirectory tree to show. 
	 */
	private void createSelectionLabel() {
		pathLabel = new JLabel( currentSelection.getAbsolutePath() );
		pathLabel.setHorizontalAlignment( SwingConstants.LEFT );
		pathLabel.setBorder( BorderFactory.createEmptyBorder(0,0,0,5));
		
		changeButton = new JButton("Change");
		changeButton.setMargin( new Insets(0,2,0,2) );
		changeButton.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showTree();
			}});
		
		selectPanel = new JPanel( new BorderLayout() ) ;
		selectPanel.add( pathLabel, BorderLayout.CENTER );
		
		selectPanel.add( changeButton, BorderLayout.EAST );
		selectPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(2,2,2,2)) );
	}
	
	/**
	 * Toggle the tree - if it's not showing show it.  If it is showing then hide it.
	 *
	 */
	private void showTree() {
		if ( dirTree==null ) {
			createDirTree();
			changeButton.setVisible(false);
		} 

		if ( !dirTree.isVisible() ) {
			
			paddingLabel.setVisible(false);
			dirTree.setVisible(true);
			mkdirButton.setVisible( dirTree.isAllowMakeDirectoryAction() );
		}
	}
	
	/**
	 * Build the directory tree.
	 */
	private void createDirTree() {
		dirTree = new JDirectoryTree();
		final File init = new File(currentSelection.getAbsolutePath());
		Runnable run = new Runnable() {
			public void run() {
				while ( !dirTree.isVisible() ) {
					Thread.yield();
				}
				if (currentSelection.isDirectory()) {
					dirTree.setSelectedPath( init);
				}
			}
		};
		new Thread( run ).start();
		
		
		paddingLabel.setVisible(false);
		dirTree.addSelectionListener( this );
		mkdirButton = new JButton( dirTree.getMakeDirectoryAction() );
		mkdirButton.setMargin( new Insets(0,2,0,2) );
		add( mkdirButton, new GridBagConstraints(0,5, 1,1, 0,0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(4,2,2,2),0,0));
		add( dirTree, new GridBagConstraints(0,6, 2,1, 1.0,1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2,2,4,2),0,0));
	}

	/* (non-Javadoc)
	 * @see lug.gui.DirectorySelectionListener#directorySelected(java.io.File, java.io.File)
	 */
	public void directorySelected(File oldSelection, File newSelection) {
		if ( newSelection!=null ) {
			setSelection( newSelection );
		}
		for ( DirectorySelectionListener listener : listeners ) {
			listener.directorySelected( oldSelection, newSelection );
		}
	}

	/**
	 * Set the selection.
	 * @param newSelection
	 */
	private void setSelection(File newSelection) {
		currentSelection=newSelection;
		pathLabel.setText( currentSelection==null ? "" : currentSelection.getAbsolutePath() );
	}
	
	public void setAllowMakeDirectory( boolean b ){
		dirTree.setAllowMakeDirectoryAction( b );
		if ( dirTree.isVisible() ) {
			mkdirButton.setVisible( dirTree.isAllowMakeDirectoryAction() );
		}
	}

	
	/**
	 * Add a listener to be notified whenever the directory selection changes.
	 * @param listener
	 */
	public void addSelectionListener( DirectorySelectionListener listener ) {
		listeners.add( listener );
	}
	
	/**
	 * Remove a listener from the notification list for directory selection changes.
	 * @param listener
	 */
	public void removeSelectionListener( DirectorySelectionListener listener ) {
		listeners.remove( listener );
	}

	/**
	 * @return the selected directory or null if there is no selection.
	 */
	public File getSelectedDirectory() {
		return currentSelection;
	}
}
