/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import uk.lug.gui.util.CachedImageLoader;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * 
 */
/**
 * Panel for holding the tree of directories, as used in a directory picker
 * control.
 * 
 * @author luggy
 * 
 */
public class JDirectoryTree extends JPanel implements ChangeListener, TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private static final Icon NEW_DIRECTORY_ICON = CachedImageLoader.getCachedIcon("images/document-open.png");

	private DefaultTreeModel directoryModel;
	private DefaultMutableTreeNode rootNode;
	// Only used if not in windows.
	private DefaultMutableTreeNode unixRootNode;

	private JScrollPane scrollPane;
	private JTree directoryTree;
	private boolean unixStyleSystem = false;
	private Expander treeExpander = null;
	private DefaultMutableTreeNode lastExpandedNode = null;
	private File lastSelection = null;
	private List<DirectorySelectionListener> listeners;
	private boolean allowMakeDirectoryAction = true;;

	/**
	 * 
	 */
	public JDirectoryTree() {
		super();
		listeners = new ArrayList<DirectorySelectionListener>(3);
		createInitialTree();
		buildGUI();
	}

	/**
	 * Add a listener to be notified whenever the directory selection changes.
	 * 
	 * @param listener
	 */
	public void addSelectionListener(DirectorySelectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the notification list for directory selection
	 * changes.
	 * 
	 * @param listener
	 */
	public void removeSelectionListener(DirectorySelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Construct the user interface
	 */
	private void buildGUI() {
		setLayout(new BorderLayout());
		directoryTree = new JTree(directoryModel);
		directoryTree.setCellRenderer(new FileSystemCellRenderer());
		directoryTree.setRootVisible(false);
		directoryTree.setShowsRootHandles(true);
		scrollPane = new JScrollPane(directoryTree);
		add(scrollPane, BorderLayout.CENTER);
		if (unixStyleSystem) {
			directoryTree.expandPath(new TreePath(directoryModel.getPathToRoot(unixRootNode)));
		}

		// Expansion listener
		directoryTree.addTreeExpansionListener(new TreeExpansionListener() {

			public void treeCollapsed(TreeExpansionEvent event) {
			}

			public void treeExpanded(TreeExpansionEvent event) {
				expandFileTree(event.getPath());
			}
		});
		directoryTree.getSelectionModel().setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);

		// Selection listener
		directoryTree.addTreeSelectionListener(this);
	}

	private void expandFileTree(TreePath path) {
		while (treeExpander != null) {
			Thread.yield();
		}
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		treeExpander = new Expander(parentNode, this);
		Thread thread = new Thread(treeExpander);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		thread.start();
	}

	/**
	 * Create the initial tree, consisting of all root drives.
	 */
	private void createInitialTree() {
		rootNode = new DefaultMutableTreeNode("My Computer");

		directoryModel = new DefaultTreeModel(rootNode);
		directoryModel.setAsksAllowsChildren(true);
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			createInitialFlatTree();
		} else if (File.listRoots().length == 1) {
			createInitialUnixStyleTree();
		} else {
			throw new IllegalStateException("No root files in file system.");
		}
	}

	/**
	 * Construct the initial tree where File.listRoots() only returns 1 item.
	 * Usually returned in Unix style systems.
	 */
	private void createInitialUnixStyleTree() {
		unixStyleSystem = true;

		// Construct the unix root
		File fileRoot = File.listRoots()[0];
		unixRootNode = new DefaultMutableTreeNode(fileRoot);
		unixRootNode.setAllowsChildren(true);
		directoryModel.insertNodeInto(unixRootNode, rootNode, 0);

		for (File subdir : fileRoot.listFiles()) {
			if (subdir.isDirectory()) {
				int idx = unixRootNode.getChildCount();
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(subdir);
				subnode.setAllowsChildren(true);

				directoryModel.insertNodeInto(subnode, unixRootNode, idx);
			}
		}
	}

	/**
	 * Runnable class for handling the expansion of a directory in a nice way.
	 * 
	 * @author Luggy
	 * 
	 */
	private class Expander implements Runnable {
		private DefaultMutableTreeNode parentNode;
		private File parentFile;
		private DefaultMutableTreeNode tempNode;
		private ChangeListener finishListener;

		/**
		 * Create an expander to async expand a tree node.
		 * 
		 * @param expandingNode
		 */
		private Expander(DefaultMutableTreeNode expandingNode, ChangeListener callback) {

			finishListener = callback;
			if (!(expandingNode.getUserObject() instanceof File)) {
				throw new IllegalArgumentException("Object inside of parent node is not of type java.io.File .");
			}
			parentFile = (File) expandingNode.getUserObject();
			this.parentNode = expandingNode;
			tempNode = new DefaultMutableTreeNode("Please wait");
			tempNode.setAllowsChildren(false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			clearParentNode();
			directoryModel.insertNodeInto(tempNode, parentNode, 0);
			List<DefaultMutableTreeNode> newNodes = getSubnodes();
			clearParentNode();
			setNewNodes(newNodes);
			ChangeEvent event = new ChangeEvent(JDirectoryTree.this);
			lastExpandedNode = parentNode;
			finishListener.stateChanged(event);
		}

		/**
		 * Remove the temporary nodes
		 * 
		 * @param subnodes
		 */
		private void setNewNodes(List<DefaultMutableTreeNode> subnodes) {
			for (int i = 0; i < subnodes.size(); i++) {
				directoryModel.insertNodeInto(subnodes.get(i), parentNode, i);
			}
		}

		/**
		 * Get a list of all subdirectories under the parent and return them as
		 * inside a list of tree nodes.
		 * 
		 * @return
		 */
		private List<DefaultMutableTreeNode> getSubnodes() {
			ArrayList<DefaultMutableTreeNode> ret = new ArrayList<DefaultMutableTreeNode>();
			if (parentFile != null && parentFile.listFiles()!=null ) {
				for (File subdir : parentFile.listFiles()) {
					if (subdir.isDirectory()) {
						DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(subdir);
						subnode.setAllowsChildren(true);
						ret.add(subnode);
					}

				}
			}

			// Sort them
			if (unixStyleSystem) {
				Collections.sort(ret, UNIX_COMPARATOR);
			} else {
				Collections.sort(ret, WINDOWS_COMPARATOR);
			}
			return ret;
		}

		/**
		 * Remove all children (if any) from the parent node.
		 */
		private void clearParentNode() {
			for (int i = 0; i < parentNode.getChildCount(); i++) {
				directoryModel.removeNodeFromParent((DefaultMutableTreeNode) parentNode.getChildAt(i));
			}
		}

	}

	/**
	 * Case insensitive filename comparator
	 */
	private static final Comparator<DefaultMutableTreeNode> WINDOWS_COMPARATOR = new Comparator<DefaultMutableTreeNode>() {
		public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
			return ((File) o1.getUserObject()).getAbsolutePath().toLowerCase().compareTo(((File) o2.getUserObject()).getAbsolutePath().toLowerCase());
		}
	};

	/**
	 * Case sensitive filename comparator
	 */
	private static final Comparator<DefaultMutableTreeNode> UNIX_COMPARATOR = new Comparator<DefaultMutableTreeNode>() {
		public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
			return ((File) o1.getUserObject()).getAbsolutePath().compareTo(((File) o2.getUserObject()).getAbsolutePath());
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == this) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		treeExpander = null;
	}

	/**
	 * Construct the initial tree as a flat tree with . This is used where the
	 * root File.listRoots() returns more than 1 entry.
	 */
	private void createInitialFlatTree() {
		unixStyleSystem = false;
		for (File lowFile : File.listRoots()) {
			DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(lowFile);
			fileNode.setAllowsChildren(true);
			directoryModel.insertNodeInto(fileNode, rootNode, rootNode.getChildCount());
		}
	}

	/**
	 * Set the currently selected directory.
	 * 
	 * @param dir
	 */
	public void setSelectedPath(File dir) {
		while (treeExpander != null) {
			Thread.yield();
		}

		File parentDir = dir.getAbsoluteFile();
		while (parentDir != null && parentDir.isFile()) {
			parentDir = parentDir.getParentFile();
		}
		if (parentDir == null) {
			throw new IllegalArgumentException("Cannot resolve File to a directory.");
		}

		ArrayList<File> pathList = new ArrayList<File>();
		while (parentDir != null) {
			pathList.add(parentDir);
			parentDir = parentDir.getParentFile();
		}
		Collections.reverse(pathList);
		expandOutTo(pathList);
		DefaultMutableTreeNode selectNode = getNodeFor(rootNode, dir);
		if (selectNode != null) {
			directoryTree.setSelectionPath(new TreePath(directoryModel.getPathToRoot(selectNode)));
		}
	}

	/**
	 * Expand out a list of directories, and select the last one.
	 * 
	 * @param dirList
	 *            list of directories, with each entry being a child of the
	 *            previous entry.
	 */
	private void expandOutTo(List<File> dirList) {
		DefaultMutableTreeNode bestNode = rootNode;
		File dir;
		for (int i = 0; i < dirList.size() - 1; i++) {
			dir = dirList.get(i);
			DefaultMutableTreeNode node = getNodeFor(bestNode, dir);
			if (node != null) {

				bestNode = node;
				TreePath path = new TreePath(directoryModel.getPathToRoot(bestNode));
				directoryTree.expandPath(path);
				while (lastExpandedNode != bestNode) {
					Thread.yield();
				}
			} else {

			}
		}
	}

	/**
	 * Find the point on the tree containg a given directory, searching
	 * recursively
	 * 
	 * @param targetDir
	 * @return
	 */
	private DefaultMutableTreeNode getNodeFor(DefaultMutableTreeNode startNode, File targetDir) {
		if (targetDir.isFile()) {
			return null;
		}
		DefaultMutableTreeNode childNode;
		for (int i = 0; i < startNode.getChildCount(); i++) {
			childNode = (DefaultMutableTreeNode) startNode.getChildAt(i);
			// Is node we're looking for ?
			if (childNode.getUserObject().equals(targetDir)) {
				return childNode;
			}

			// Check subnodes
			if (childNode.getChildCount() > 0 && (childNode.getUserObject() instanceof File)) {
				DefaultMutableTreeNode foundNode = getNodeFor(childNode, targetDir);
				if (foundNode != null) {
					return foundNode;
				}
			}
		}
		return null;
	}

	public File getSelectedDirectory() {
		if (directoryTree.getSelectionPath() == null) {
			return null;
		}
		DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) directoryTree.getSelectionPath().getLastPathComponent();
		if (lastNode.getUserObject() == null || !(lastNode.getUserObject() instanceof File)) {
			return null;
		}
		return (File) (lastNode.getUserObject());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event
	 * .TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = directoryTree.getSelectionPath();
		File newSelection = null;
		if (path != null) {
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (selNode.getUserObject() instanceof File) {
				newSelection = (File) selNode.getUserObject();
			}
		}

		for (DirectorySelectionListener dsl : listeners) {
			dsl.directorySelected(lastSelection, newSelection);
		}
		lastSelection = newSelection;

		makeDirectoryAction.setEnabled(allowMakeDirectoryAction && lastSelection != null);
	}

	private Action makeDirectoryAction = new AbstractAction("Make New Directory", NEW_DIRECTORY_ICON) {

		public void actionPerformed(ActionEvent e) {
			String newDir = JOptionPane.showInputDialog(scrollPane, "Name of new directory");
			if (newDir == null) {
				return;
				// abort;
			}
			makeNewDir(newDir);
		}

	};

	private TreePath getPathToRoot(DefaultMutableTreeNode node) {
		return new TreePath(directoryModel.getPathToRoot(node));
	}

	private DefaultMutableTreeNode getSelectedNode() {
		if (directoryTree.getSelectionPath() == null) {
			return null;
		}
		return (DefaultMutableTreeNode) directoryTree.getSelectionPath().getLastPathComponent();
	}

	/**
	 * Construct a new directory at the currently selection.
	 * 
	 * @param newDir
	 */
	protected void makeNewDir(String newDir) {
		DefaultMutableTreeNode parentNode = getSelectedNode();
		File parentDir = getSelectedDirectory();
		File newDirectory = new File(parentDir.getAbsolutePath() + File.separator + newDir);
		if (newDirectory.mkdir()) {
			DefaultMutableTreeNode newNode = null;
			if (directoryTree.isExpanded(getPathToRoot(parentNode))) {
				newNode = new DefaultMutableTreeNode(newDirectory);
				directoryModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());

			} else {
				// Expand parent directory
				directoryTree.expandPath(new TreePath(directoryModel.getPathToRoot(parentNode)));
				while (lastExpandedNode != parentNode) {
					Thread.yield();
				}
				newNode = getNodeFor(parentNode, newDirectory);
			}

			if (newNode != null) {
				directoryTree.setSelectionPath(getPathToRoot(newNode));
				directoryTree.repaint();
			}
		} else {
			JOptionPane.showMessageDialog(scrollPane, "Error creating new directory.", "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * @return the action that can be invoked to create a new directory under
	 *         the current selection.
	 */
	public Action getMakeDirectoryAction() {
		return makeDirectoryAction;
	}

	/**
	 * @return the true if the action that makes new directories is enabled.
	 */
	public boolean isAllowMakeDirectoryAction() {
		return allowMakeDirectoryAction;
	}

	/**
	 * @param allowMakeDirectoryAction
	 *            true to enable the new directory action.
	 */
	public void setAllowMakeDirectoryAction(boolean allowMakeDirectoryAction) {
		this.allowMakeDirectoryAction = allowMakeDirectoryAction;
	}
}
