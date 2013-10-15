/**
 * 
 */
package uk.lug.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
@SuppressWarnings("serial")
public class JMemoryFileChooser extends JFileChooser {
	/** Prepended to all preferences keys written & read */
	public static final String PREFS_KEY = "jmfc_";
	private Preferences prefs = Preferences.userNodeForPackage( this.getClass() );
	
	/**
	 * 
	 */
	public JMemoryFileChooser() {
		super();
	}

	/**
	 * @param currentDirectoryPath
	 */
	public JMemoryFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	/**
	 * @param currentDirectory
	 */
	public JMemoryFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	/**
	 * @param fsv
	 */
	public JMemoryFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	/**
	 * @param currentDirectory
	 * @param fsv
	 */
	public JMemoryFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	/**
	 * @param currentDirectoryPath
	 * @param fsv
	 */
	public JMemoryFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	/**
	 * Attempt to recall and set current directory for the named dialog.
	 */
	private void recallLastDir(String name) {
		String lastDir = prefs.get( PREFS_KEY+name , null ) ;
		if ( lastDir==null ) {
			return;
		}
		File last = new File( lastDir ) ;
		if ( last.exists() && last.isDirectory() ) {
			setCurrentDirectory( last );
		}
	}
	
	/**
	 * Save the last directory
	 * @param name name to save as.
	 * @param last if a File then the parent directory is used, else the directory name itself is used.
	 */
	private void setLastDir( String name, File last ) {
		if ( last.isDirectory() ) {
			prefs.put( PREFS_KEY+name, last.getAbsolutePath() );
		} else {
			prefs.put( PREFS_KEY+name, last.getParentFile().getAbsolutePath());
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#showDialog(java.awt.Component, java.lang.String)
	 */
	public int showDialog(String name, Component parent, String approveButtonText) throws HeadlessException {
		recallLastDir( name );
		int ret = super.showDialog(parent, approveButtonText);
		if ( ret == JFileChooser.APPROVE_OPTION ) {
			File selected = super.getSelectedFile();
			setLastDir(name, selected);
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#showOpenDialog(java.awt.Component)
	 */
	public int showOpenDialog(String name, Component parent) throws HeadlessException {
		recallLastDir( name ) ;
		int ret = super.showOpenDialog(parent);
		if ( ret == JFileChooser.APPROVE_OPTION ) {
			File selected = super.getSelectedFile();
			setLastDir(name, selected);
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#showSaveDialog(java.awt.Component)
	 */
	public int showSaveDialog(String name, Component parent) throws HeadlessException {
		recallLastDir( name ) ;
		int ret = super.showSaveDialog(parent);
		if ( ret == JFileChooser.APPROVE_OPTION ) {
			File selected = super.getSelectedFile();
			setLastDir(name, selected);
		}
		return ret;
	}

}
