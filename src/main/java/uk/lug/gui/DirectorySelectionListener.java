package uk.lug.gui;
import java.io.File;

/**
 * 
 */

/**
 * Copyright 3 Jun 2007
 * @author Paul Loveridge
 * <p>Listener to be fired when the current selected directory changes.</p>
 * 
 */
public interface DirectorySelectionListener {
	/**
	 * Fired whenever the selected directory is changed.  Both
	 * old and new selections can be null to indication initial selection
	 * or de-selections respectively.
	 * @param oldSelection
	 * @param newSelection
	 */
	public void directorySelected(File oldSelection, File newSelection);
}
