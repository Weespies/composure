/**
 * 
 */
package uk.lug.gui.archetype;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uk.lug.MutableList;
import uk.lug.gui.CachedImageLoader;
import uk.lug.gui.JMemoryFileChooser;
import uk.lug.serenity.npc.gui.ArchetypesCellRenderer;
import uk.lug.serenity.npc.managers.ArchetypesManager;
import uk.lug.serenity.npc.random.archetype.Archetype;

/**
 * @author Luggy
 * Panel for controlling the archetypes library.
 */
public class ArchetypeManagerPanel extends JPanel implements ChangeListener {
	private static final String FILEDIALOG_NAME = "archetypeExport";
	private static final Icon ADD_ICON = CachedImageLoader.getCachedIcon("images/add.png");
	private static final Icon DELETE_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	private static final Icon SAVE_ICON = CachedImageLoader.getCachedIcon("images/disk_blue.png");
	private static final Icon REVERT_ICON = CachedImageLoader.getCachedIcon("images/disk_blue_revert.png");
	private static final Icon IMPORT_ICON = CachedImageLoader.getCachedIcon("images/import.png");
	private static final Icon EXPORT_ICON = CachedImageLoader.getCachedIcon("images/export.png");
	
	private JList archetypeList;
	private ArchetypeControl archetypeControl=null;
	private MutableList<Archetype> listData;
	private JScrollPane scroller;
	private JPanel rightPanel;
	private JButton addButton;
	private JButton deleteButton;
	private JButton saveButton;
	private JButton revertButton;
	private JSplitPane splitPane;
	private JPanel leftPanel;
	private JLabel selectLeftLabel;
	private JMenu archetypesMenu;
	
	/**
	 * @return the archetypesMenu
	 */
	public JMenu getArchetypesMenu() {
		return archetypesMenu;
	}

	private JMenuItem saveItem;
	private JMenuItem revertItem;
	private JMenuItem importItem;
	private JMenuItem exportItem;
	
	private Action exportArchetypeAction = new AbstractAction("Export", EXPORT_ICON) {
		{
			putValue(Action.SHORT_DESCRIPTION, "Export an archetype.");
		}
		public void actionPerformed(ActionEvent event) {
			doExportSelected();
		}
	};
	
	private Action importArchetypeAction = new AbstractAction("Import", IMPORT_ICON) {
		{
			putValue(Action.SHORT_DESCRIPTION, "Export an archetype.");
		}
		public void actionPerformed(ActionEvent event) {
			doImport();
		}
	};
	
	private Action newArchetypeAction = new AbstractAction("New",ADD_ICON) {
		{
			putValue(Action.SHORT_DESCRIPTION,"Create a new archtype and start editing it.");
		}
		public void actionPerformed(ActionEvent event) {
			makeNewAchetype();
		}
	};
	
	private Action deleteArchetypeAction = new AbstractAction("Delete",DELETE_ICON) {
		{
			putValue(Action.SHORT_DESCRIPTION,"Delete the currently selected archtype.");
		}
		public void actionPerformed(ActionEvent event) {
			deleteSelectedAchetype();
		}
	};
	
	private Action saveListAction = new AbstractAction("Save",SAVE_ICON ) {
		{
			putValue(Action.SHORT_DESCRIPTION,"Save archetypes to local data file.");
		}
		public void actionPerformed(ActionEvent event) {
			saveList();
		}
	};
	
	private Action revertListAction = new AbstractAction("Revert",REVERT_ICON ) {
		{
			putValue(Action.SHORT_DESCRIPTION,"Revert archtype list to last saved.");
		}
		public void actionPerformed(ActionEvent event) {
			revertList();
		}
	};
	
	/**
	 * 
	 */
	public ArchetypeManagerPanel() {
		super();
		saveListAction.setEnabled(true);
		build();
	}

	/**
	 * 
	 */
	protected void doExportSelected() {
		Archetype selected = listData.get( archetypeList.getSelectedIndex() );
		if ( selected==null ) {
			return;
		}
		// Set up the dialog
		JMemoryFileChooser chooser = new JMemoryFileChooser();
		chooser.setFileFilter(characterFileFilter);
		chooser.setSelectedFile( new File(selected.getName()+".xml" ) );
		chooser.setDialogTitle("Export Archetype");
		chooser.setApproveButtonText("Export");

		// Call dialog
		if (chooser.showSaveDialog(FILEDIALOG_NAME, this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File targetFile = chooser.getSelectedFile();
		// Save it
		try {
			performSave( selected, targetFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "An error occured during export.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	protected void doImport() {
		// Set up the dialog
		JMemoryFileChooser chooser = new JMemoryFileChooser();
		chooser.setFileFilter(characterFileFilter);
		chooser.setDialogTitle("Import Archetype");
		chooser.setApproveButtonText("Import");
		
		// Call dialog
		if (chooser.showOpenDialog(FILEDIALOG_NAME, this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File importFile = chooser.getSelectedFile();
		try {
			performImport( importFile);
		} catch (JDOMException e) {
			JOptionPane.showMessageDialog(this, "That import does not appear to be correctly formatted.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "There was an I/O error importing that archetype.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * @param importFile
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private void performImport(File importFile) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc  = builder.build( importFile );
		Element root = doc.getRootElement();
		Archetype imported = new Archetype();
		imported.setXML( root );
		listData.add( imported );
	}

	/**
	 * @param selected
	 * @param targetFile
	 * @throws IOException 
	 */
	private void performSave(Archetype selected, File targetFile) throws IOException {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		FileWriter writer = new FileWriter( targetFile, false );
		outputter.output( selected.getXML(), writer );
		writer.flush();
		writer.close();
	}

	/**
	 * Revert the list back to its saved state.
	 */
	protected void revertList() {
		int ret = JOptionPane.showConfirmDialog( this, "Revert list losing unsaved changes?","Confirm",JOptionPane.OK_CANCEL_OPTION );
		if ( ret==JOptionPane.CANCEL_OPTION ) {
			return;
		}
		archetypeControl.removeNameChangeListener( this );
		rightPanel.removeAll();
		archetypeControl=null;
		rightPanel.add( selectLeftLabel );
		rightPanel.repaint();
		listData.clear();
		listData.addAll( ArchetypesManager.getArchetypes() );
	}

	/**
	 * Save the archetypes list to the datafile.
	 */
	public void saveList() {
		ArchetypesManager.setArchtypesList( listData );
		try {
			ArchetypesManager.writeArchetypes();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,e.getMessage(),"IO Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Delete selected archetype
	 */
	private void deleteSelectedAchetype() {
		int idx = archetypeList.getSelectedIndex();
		if ( idx==-1) {
			return;
		}
		StringBuilder msg = new StringBuilder(256);
		msg.append("Delete archetype \"");
		msg.append( listData.get(idx).getName() );
		msg.append("\" ?");
		int ret = JOptionPane.showConfirmDialog(this, msg.toString(), "Confirm", JOptionPane.OK_CANCEL_OPTION);
		if ( ret==JOptionPane.CANCEL_OPTION ) {
			return;
		}
		listData.remove( idx );
		saveListAction.setEnabled(true);
	}

	/**
	 * Create a new archetype.
	 */
	private void makeNewAchetype() {
		Archetype archetype = new Archetype();
		archetype.setName("New Archetype");
		listData.add( archetype );
		archetypeList.setSelectedIndex( listData.size()-1 );
		saveListAction.setEnabled(true);
	}

	/**
	 * 
	 */
	private void build() {
		buildRightPanel();
		buildLeftPanel();
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent( leftPanel );
		splitPane.setRightComponent( rightPanel );
		setLayout( new BorderLayout() );
		add(splitPane);
		splitPane.setDividerLocation(0.25d);
		initMenu();
		
	}
	
	/**
	 * Create the menu structure
	 */
	private void initMenu() {
		archetypesMenu = new JMenu("Achetypes");
		saveItem = new JMenuItem( saveListAction );
		revertItem = new JMenuItem( revertListAction );
		importItem = new JMenuItem(importArchetypeAction);
		exportItem = new JMenuItem(exportArchetypeAction);
		archetypesMenu.add( saveItem );
		archetypesMenu.add( revertItem );
		archetypesMenu.add( importItem);
		archetypesMenu.add( exportItem );
	}

	private void buildLeftPanel() {
		leftPanel = new JPanel();
		leftPanel.setLayout( new GridBagLayout() );
		listData = new MutableList<Archetype>();
		listData.addAll( ArchetypesManager.getInstance().getArchetypes() );
		archetypeList = new JList( listData );
		archetypeList.setCellRenderer( new ArchetypesCellRenderer() );
		archetypeList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		scroller = new JScrollPane( archetypeList );
		Insets buttonInsets = new Insets(0,2,5,2);
		leftPanel.add( scroller, new GridBagConstraints(0,2, 2,1, 0.25,1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0,2,0,2),0,0));
		
		addButton = new JButton( newArchetypeAction );
		deleteButton = new JButton( deleteArchetypeAction );
		saveButton = new JButton( saveListAction );
		revertButton = new JButton( revertListAction );
		
		leftPanel.add( addButton, new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				buttonInsets,0,0));
		leftPanel.add( deleteButton, new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				buttonInsets,0,0));
		
		addButton.setMargin( new Insets(0,2,0,2) );
		deleteButton.setMargin( new Insets(0,2,0,2) );
		saveButton.setMargin( new Insets(0,2,0,2) );
		revertButton.setMargin( new Insets(0,2,0,2) );
		deleteArchetypeAction.setEnabled(false);
		//leftPanel.setBorder( BorderFactory.createTitledBorder("Archetypes") );
		exportArchetypeAction.setEnabled(false);
		archetypeList.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				respondToListClick();
			}
		});
	}

	/**
	 * Respond to selections within the left list.
	 * @throws InvocationTargetException 
	 * @throws InterruptedException 
	 */
	private void respondToListClick() {
		this.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		enableOrDisableActions();
		int idx = archetypeList.getSelectedIndex();
		if ( idx==-1 || idx>=listData.size() ) {
			//no selection
			if ( archetypeControl==null ) {
				return;
			}
			archetypeControl.removeNameChangeListener( this );
			rightPanel.removeAll();
			archetypeControl=null;
			rightPanel.add( selectLeftLabel );
			rightPanel.repaint();
			return;
		}
		if ( archetypeControl==null ) {
			archetypeControl = new ArchetypeControl( listData.get(idx) );
			setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
			rightPanel.removeAll();
			rightPanel.add( archetypeControl, BorderLayout.CENTER );
			rightPanel.repaint();
			rightPanel.revalidate();
			setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );
			archetypeControl.addNameChangeListener( this );
		} else {
			archetypeControl.setArchetype( listData.get(idx) );
			archetypeControl.repaint();
		}
		this.setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * 
	 */
	private void enableOrDisableActions() {
		int idx = archetypeList.getSelectedIndex();
		if ( archetypeList.getSelectedIndex()==-1 ) {
			deleteArchetypeAction.setEnabled(false);
			return;
		}
		if ( StringUtils.equalsIgnoreCase("generic",listData.get(idx).getName()) ) {
			deleteArchetypeAction.setEnabled(false);
			return;
		}
		exportArchetypeAction.setEnabled(true);
		deleteArchetypeAction.setEnabled(true);
	}
	
	private void buildRightPanel() {
		rightPanel = new JPanel( new BorderLayout() );
		selectLeftLabel = new JLabel("<html>Select archtype to edit<br>or click the add button.</html>" );
		rightPanel.add( selectLeftLabel );
		selectLeftLabel.setHorizontalAlignment( SwingConstants.CENTER );
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		archetypeList.repaint();
	}
	

	/**
	 * Filefilter for the JFileChooser to use.
	 */
	private FileFilter characterFileFilter = new FileFilter() {

		@Override
		public boolean accept(File arg0) {
			return (arg0.isFile() && arg0.getName().toLowerCase().endsWith(
					".xml"))
					|| (arg0.isDirectory());
		}

		@Override
		public String getDescription() {
			return "NPC File";
		}
	};	
}

