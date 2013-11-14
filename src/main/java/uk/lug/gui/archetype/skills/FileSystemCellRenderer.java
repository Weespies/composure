/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>Renderer for displaying directories in tree structure.</p>
 * <p>File objects are rendered with .getName() as the text.  Non file objects
 * have the toString() method used.</p>
 * <p>By default directory names are rendered in the standard JLabel for and non-directory
 * names are rendered in an italic version of that font.</p>
 */
/**
 * @author luggy
 *
 */
public class FileSystemCellRenderer extends JLabel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private static Icon DIRECTORY_ICON=UIManager.getLookAndFeelDefaults().getIcon("FileView.directoryIcon");
	private static Icon FILE_ICON=UIManager.getLookAndFeelDefaults().getIcon("FileView.fileIcon");
	private static Icon DRIVE_ICON=UIManager.getLookAndFeelDefaults().getIcon("FileView.hardDriveIcon");
	
	private Font directoryFont;
	private Font nonDirectoryFont;
	
	/**
	 * Construct the cell renderer
	 */
	FileSystemCellRenderer() {
		super();
		directoryFont = super.getFont();
		nonDirectoryFont = directoryFont.deriveFont( Font.ITALIC +Font.BOLD);
		setOpaque(false);
	}

	/**
	 * @return the Font used for rendering File/Directory names
	 */
	public Font getDirectoryFont() {
		return directoryFont;
	}

	/**
	 * @param directoryFont the font used for rendering File object names.
	 */
	public void setDirectoryFont(Font directoryFont) {
		this.directoryFont = directoryFont;
	}

	/**
	 * @return the font used for rendering objects that are not of type java.io.File names.
	 */
	public Font getNonDirectoryFont() {
		return nonDirectoryFont;
	}

	/**
	 * @param nonDirectoryFont the font used for rendering objects that are not of type java.io.File names.
	 */
	public void setNonDirectoryFont(Font nonDirectoryFont) {
		this.nonDirectoryFont = nonDirectoryFont;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		if (!( value instanceof DefaultMutableTreeNode ) ) {
			setIcon(null);
			setText("Not a tree node");
		} else {
			Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
			if ( userObject instanceof File ) {			
				File userFile = (File)userObject;
				String txt = userFile.getName();
				setFont( directoryFont );
				if ( txt==null || txt.length()==0 ) {
					//Its a root drive
					txt = userFile.getPath();
					setIcon(DRIVE_ICON );
				} else {
					setIcon( ((File)userObject).isDirectory() ? DIRECTORY_ICON : FILE_ICON );		
				}
				setText( txt );
			} else {
				setIcon( null ) ;
				setFont( nonDirectoryFont );
				setText( value.toString() );
			}
		}
		setOpaque(selected);
		setBackground(selected ? UIManager.getColor("Tree.selectionBackground"): tree.getBackground());
		setForeground(selected ? UIManager.getColor("Tree.selectionForeground"): tree.getForeground());
		return this;
	}

}
