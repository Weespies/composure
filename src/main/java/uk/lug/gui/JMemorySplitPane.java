/**
 * 
 */
package uk.lug.gui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Extension to JSplitPane that can remember it's divider position.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class JMemorySplitPane extends JSplitPane {
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

	/**
	 * 
	 */
	public JMemorySplitPane() {
		super();
		initDividerListener();
	}

	/**
	 * @param newOrientation
	 * @param newContinuousLayout
	 * @param newLeftComponent
	 * @param newRightComponent
	 */
	public JMemorySplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
		initDividerListener();
	}

	/**
	 * @param newOrientation
	 * @param newContinuousLayout
	 */
	public JMemorySplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout);
		initDividerListener();
	}

	/**
	 * @param newOrientation
	 * @param newLeftComponent
	 * @param newRightComponent
	 */
	public JMemorySplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
		initDividerListener();
	}

	/**
	 * @param newOrientation
	 */
	public JMemorySplitPane(int newOrientation) {
		super(newOrientation);
		initDividerListener();
	}
	
	private void initDividerListener() {
		(((BasicSplitPaneUI)this.getUI()).getDivider()).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				storeDividerPosition();
			}
		});
	}

	/**
	 * Store the current divider position in the user preferences node.
	 */
	public void storeDividerPosition() {
		int pos = getDividerLocation();
		prefs.putInt( getPreferenceKey() , getLastDividerLocation() );
		System.out.println("Storing location : "+ (pos) ) ;
	}
	
	/**
	 * Read the divider position out of the preferences user node and apply it.
	 */
	public void recallDividerPosition() {
		final int pos = prefs.getInt( getPreferenceKey() , -1 ) ;
		System.out.println("Recalling location : "+ (pos) ) ;
		if ( pos!=-1 ) {
			Runnable run = new Runnable() {
				public void run() {
					while (!visibleYet()) {
						Thread.yield();
					}
					setDividerLocation( pos );
				}
			};
			Thread thread = new Thread( run , "Waiting");
			thread.start();
		} else {
			storeDividerPosition();
		}
	}
	
	private boolean visibleYet() {
		return isVisible();
	}
	
	/**
	 * Get the key used to store divider location in the preferences node.
	 * @return currently stored positions tring.
	 */
	public String getPreferenceKey() {
		StringBuilder sb = new StringBuilder();
		sb.append( "JMSP_" );
		sb.append( getName() );
		return sb.toString();
	}
	
	@Override
	public void setName( String s ) {
		super.setName( s ) ;
		recallDividerPosition();
	}
	
}
