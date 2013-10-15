package uk.lug.gui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

/**
 * Extension of JFrame, which remembers it's size and posisition
 * based upon it's title.*/
public class JMemoryFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	
	/**
	 * Empty constructor.*/
	public JMemoryFrame() {
		super();
		addListener();
	}

	/**
	 * Creates a new JMemoryFrame.
	 * @param windowTitle title to show in the JFrame's titlebar.  This
	 * is used as the storage key for the window.*/
	public JMemoryFrame(String windowTitle) {
		super(windowTitle);
		addListener();
	}

	/**
	 * Creates a new JMemory Frame.
	 * @param gc Graphics Configuration.*/
	public JMemoryFrame(GraphicsConfiguration gc) {
		super(gc);
		addListener();
	}


	/**
	 * Creates a new JMemory Frame.
	 * @param windowTitle title to display in the window
	 * @param gc Graphics Configuration.*/
	public JMemoryFrame(String windowTitle,GraphicsConfiguration gc) {
		super(windowTitle,gc);
		addListener();
	}

	/**
	 * Add the listener to this window.*/
	private void addListener() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				store();
			}
		});
	}

	/**
	 * Restore window to stored location & size from the user node.
	 * If no settings are stored, java.awt.Window.pack() is used to
	 * determine the size.*/
	public void recall() {
		String title = getStorageName();
		if (prefs==null) {
			prefs = Preferences.userNodeForPackage(this.getClass());
		}
		//Recall position
		int xpos = prefs.getInt(title+"_x",0);
		int ypos = prefs.getInt(title+"_y",0);
		if ( (xpos!=-1) && (ypos!=-1) ) {
			super.setLocation(xpos,ypos);
		} else {
			super.setLocation(0,0);
		}
		//Recall size
		int xsize = prefs.getInt(title+"_width",-1);
		int ysize = prefs.getInt(title+"_height",-1);
		if ( (xsize!=-1) && (ysize!=-1) ) {
			setSize(xsize,ysize);
			validate();
		} else {
			super.pack();
		}
		//state
		int state = prefs.getInt( title + "_state",-1);
		if ( state!=-1 ) {
			setState( state );
		}
		//extended state 
		int extState = prefs.getInt( title+"_extState" ,-1);
		if ( extState !=-1 ) {
			java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
			if ( toolkit.isFrameStateSupported(extState) ) {
				setExtendedState( extState );
			}
		}
		
		show();
	}

	/**
	 * Restore window to stored location & size from the user node.
	 * If no settings are stored, the dimensions supplied will be used.
	 * @param w Width of JMemoryFrame if no previous size is known.
	 * @param h Height of JMemoryFrame if no previous size of known.*/
	public void recall(int w,int h) {
		String title = super.getTitle();
		if (prefs==null) {
			prefs = Preferences.userNodeForPackage(this.getClass());
		}
		//Recall position
		int xpos = prefs.getInt(title+"_x",0);
		int ypos = prefs.getInt(title+"_y",0);
		if ( (xpos!=-1) && (ypos!=-1) ) {
			super.setLocation(xpos,ypos);
		} else {
			super.setLocation(0,0);
		}
		//Recall size
		int xsize = prefs.getInt(title+"_width",w);
		int ysize = prefs.getInt(title+"_height",h);
		if ( (xsize!=-1) && (ysize!=-1) ) {
			setSize(xsize,ysize);
			validate();
		} else {
			super.pack();
		}
		show();
	}

	/**
	 * Restore window to stored location & size from the user node.
	 * If no settings are stored, the supplied value is used.
	 * @param d Dimension representing the size of the JMemoryFrame if
	 * no previous value is known.*/
	public void recall(Dimension d) {
		String title = getStorageName();
		if (prefs==null) {
			prefs = Preferences.userNodeForPackage(this.getClass());
		}
		//Recall position
		int xpos = prefs.getInt(title+"_x",0);
		int ypos = prefs.getInt(title+"_y",0);
		if ( (xpos!=-1) && (ypos!=-1) ) {
			super.setLocation(xpos,ypos);
		} else {
			super.setLocation(0,0);
		}
		//Recall size
		int xsize = prefs.getInt(title+"_width",-1);
		int ysize = prefs.getInt(title+"_height",-1);
		if ( (xsize!=-1) && (ysize!=-1) ) {
			setSize(xsize,ysize);
			validate();
		} else {
			setSize(d);
		}
		show();
	}

	/**
	 * Stores the location and size of this JFrame in the user node .*/
	public void store() {
		String title = getStorageName();
		if (prefs==null) {
			prefs = Preferences.userNodeForPackage(this.getClass());
		}
		prefs.putInt(title+"_x",getX());
		prefs.putInt(title+"_y",getY());
		prefs.putInt(title+"_width",getWidth());
		prefs.putInt(title+"_height",getHeight());
		prefs.putInt(title+"_state",getState());
		prefs.putInt(title+"_extState", getExtendedState() );
	}

	/**
	 * Overridden pack command.  Applies the pack method from JFrame
	 * if no prior size information is available.*/
	@Override
	public void pack() {
		int xsize = prefs.getInt(getStorageName() + "_width",-1);
		int ysize = prefs.getInt(getStorageName()+"_height",-1);
		if ( (xsize==-1) || (ysize==-1) ) {
			super.pack();
		} else {
			setSize(xsize,ysize);
		}
		
	}
	
	/**
	 * @return the name used to store this windows coordinates.
	 */
	public String getStorageName() {
		return ( super.getName()==null ? super.getTitle() : super.getName() );
	}

	/**
	 * Overrides the show command to display the JFrame in the correct position
	 * (i.e. the prior position if it exists.*/
	@Override
	public void show() {
		if (prefs==null) {
			prefs = Preferences.userNodeForPackage(this.getClass());
		}
		int xpos = prefs.getInt(getStorageName()+"_x",0);
		int ypos = prefs.getInt(getStorageName()+"_y",0);
		setLocation(xpos,ypos);
		super.show();
	}

	/**
	 * Overrides the dispose method to ensure that the size and position
	 * of the window are saved.*/
	@Override
	public void dispose() {
		store();
		super.dispose();
	}

}