/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.dao.records.PersonRecord;
import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.gui.archetype.skills.JMemoryFileChooser;
import uk.lug.gui.archetype.skills.JMemoryFrame;
import uk.lug.gui.npc.archetype.ArchetypeManagerPanel;
import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.random.Generator;

/**
 * $Id$
 * 
 * @version $Revision$
 * @author $Author$
 *         <p>
 */
/**
 * @author Luggy
 */
public class GeneratorWindow extends JMemoryFrame implements DataModelListener<Person> {
	private static final long serialVersionUID = 1L;

	public static final String FILE_EXTENSION = ".serenitynpc";

	public static final String LAST_SAVE_KEY = "lastSaveDir";

	private static final String FILECHOOSER_DATAFILE = "composureNPC";

	private static final String FILECHOOSER_ASCII_OUT = "composureNPCASCII";

	private static final Icon DICE_ICON = CachedImageLoader.getCachedIcon("images/die_48.png");

	private static final Icon CLEAR_CHARACTER_ICON = CachedImageLoader.getCachedIcon("images/clearcharacter.png");

	private static final Icon LOAD_ICON = CachedImageLoader.getCachedIcon("images/document-open.png");

	private static final Icon SAVE_ICON = CachedImageLoader.getCachedIcon("images/document-save.png");

	private static final Icon SAVE_AS_ICON = CachedImageLoader.getCachedIcon("images/document-save-as.png");

	private static final Icon SAVE_ASCII = CachedImageLoader.getCachedIcon("images/document_text.png");

	protected static final Cursor CURSOR_WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

	protected static final Cursor CURSOR_NORMAL = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	protected static final String ASCII_FILE_EXTENSION = ".txt";

	private static final Icon ARCHETYPES_MANAGER_ICON = CachedImageLoader.getCachedIcon("images/configure.png");

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenuItem loadItem;

	private JMenuItem saveItem;

	private JMenuItem saveAsItem;

	private JMenuItem outputAsciiItem;

	private JMenuItem randomPassengerItem;

	private JCheckBoxMenuItem previewWindowItem;

	private CharacterPanel charPanel;

	private KeyStroke saveAsStroke = KeyStroke.getKeyStroke("control shift S");

	private KeyStroke saveStroke = KeyStroke.getKeyStroke("control S");

	private KeyStroke loadStroke = KeyStroke.getKeyStroke("control O");

	private KeyStroke randomStroke = KeyStroke.getKeyStroke("control R");

	private KeyStroke asciiStroke = KeyStroke.getKeyStroke("control A");

	private File lastSaveFile = null;

	private String lastSaveName = null;

	private DataModel<Person> model;

	private SummaryFrame summaryWindow = null;

	public Person getPerson() {
		return model.getData();
	}

	/**
	 * Filefilter for the JFileChooser to use.
	 */
	private FileFilter characterFileFilter = new FileFilter() {

		@Override
		public boolean accept(File arg0) {
			return (arg0.isFile() && arg0.getName().toLowerCase().endsWith(FILE_EXTENSION)) || (arg0.isDirectory());
		}

		@Override
		public String getDescription() {
			return "NPC File";
		}
	};

	/**
	 * Filefilter for the JFileChooser to use for ascii output
	 */
	private FileFilter asciiFileFilter = new FileFilter() {
		@Override
		public boolean accept(File arg0) {
			return (arg0.isFile() && arg0.getName().toLowerCase().endsWith(ASCII_FILE_EXTENSION)) || (arg0.isDirectory());
		}

		@Override
		public String getDescription() {
			return "ASCII File";
		}
	};

	/**
	 * Action invoked to randomize a character.
	 */
	private Action diceAction = new AbstractAction("", DICE_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			charPanel.randomize();
			lastSaveFile = null;
			lastSaveName = null;
		}
	};

	/**
	 * Action invoked to clear a character down to blank
	 */
	private Action clearAction = new AbstractAction("", CLEAR_CHARACTER_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			charPanel.clearCharacter();
			lastSaveFile = null;
			lastSaveName = null;
		}
	};

	/**
	 * Action invoked to show the archtypes manager.
	 */
	private Action archetypesManagerAction = new AbstractAction("Manage Archetypes", ARCHETYPES_MANAGER_ICON) {
		public void actionPerformed(ActionEvent ae) {
			doShowArchtypesManager();
		}
	};

	/**
	 * Action invoked to load a character.
	 */
	private Action loadAction = new AbstractAction("Open Character", LOAD_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doLoadCharacter();
		}
	};

	/**
	 * Action invoked to save a character.
	 */
	private Action saveAction = new AbstractAction("Save Character", SAVE_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {

			doSaveCharacterAsLast();
		}
	};

	/**
	 * Action invoked to save a character always asking for a filename.
	 */
	private Action saveAsAction = new AbstractAction("Save Character As", SAVE_AS_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doSaveCharacterAs();
		}
	};

	/**
	 * Action invoked to export an ASCII version of this character.
	 */
	private Action outputAsciiAction = new AbstractAction("Output as ASCII", SAVE_ASCII) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doOutputASCII();
		}
	};

	/**
	 * Action invoked to show the random passenger generator.
	 */
	private Action randomPassengerAction = new AbstractAction("Random Passenger Generator") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doShowRandomPassengerGenerator();
		}
	};

	/**
	 * Action invoked to show the random passenger generator.
	 */
	private Action previewWindowAction = new AbstractAction("Preview Window") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doShowPreviewWindow();
		}
	};

	private JMenuItem archetypesManagerItem;

	private JMemoryFrame archetypeFrame;

	/**
	 * Construct a generator window.
	 * 
	 * @param windowTitle
	 */
	public GeneratorWindow(String windowTitle) {
		super(windowTitle);

		model = new DataModel<Person>();
		model.addDataModelListener(this);
		createGUI();
		model.setData(Generator.getRandomPerson());
	}

	/**
	 * Show/hide the character preview window.
	 */
	protected void doShowPreviewWindow() {
		if (summaryWindow == null) {
			// First time !
			summaryWindow = new SummaryFrame(model);
			summaryWindow.setVisible(true);
			summaryWindow.addWindowListener(new WindowListener() {

				public void windowActivated(WindowEvent arg0) {
				}

				public void windowClosed(WindowEvent arg0) {
					previewWindowItem.setSelected(false);
				}

				public void windowClosing(WindowEvent arg0) {
				}

				public void windowDeactivated(WindowEvent arg0) {
				}

				public void windowDeiconified(WindowEvent arg0) {
				}

				public void windowIconified(WindowEvent arg0) {
				}

				public void windowOpened(WindowEvent arg0) {
					previewWindowItem.setSelected(true);
				}
			});
		}
		summaryWindow.setVisible(previewWindowItem.isSelected());

	}

	protected void doShowArchtypesManager() {
		if (archetypeFrame == null) {
			archetypeFrame = new JMemoryFrame("Archetype control");
		} else {
			archetypeFrame.setVisible(true);
		}

		archetypeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		final ArchetypeManagerPanel archetypesManager = new ArchetypeManagerPanel();
		archetypeFrame.add(archetypesManager);
		archetypeFrame.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
			}
		});

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(archetypesManager.getArchetypesMenu());
		archetypeFrame.setJMenuBar(menuBar);
		archetypeFrame.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				archetypesManager.saveList();
				refreshArchetypes();
			}
		});
		archetypeFrame.setSize(640, 480);
		archetypeFrame.setVisible(true);

	}

	/**
	 * 
	 */
	protected void refreshArchetypes() {
		charPanel.refreshArchetypes();
	}

	/**
	 * Perform the ascii output.
	 */
	protected void doOutputASCII() {
		// Set up the dialog
		JMemoryFileChooser chooser = new JMemoryFileChooser();
		chooser.setFileFilter(asciiFileFilter);
		chooser.setSelectedFile(new File(characterName() + ASCII_FILE_EXTENSION));

		// Call dialog
		if (chooser.showSaveDialog(FILECHOOSER_ASCII_OUT, charPanel) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File targetFile = chooser.getSelectedFile();

		// Save it
		try {
			String txt = model.getData().getAsText().toString();
			FileOutputStream fos = new FileOutputStream(targetFile, false);
			PrintWriter out = new PrintWriter(fos);
			out.println(txt);
			out.flush();

			out.close();
			fos.close();
		} catch (IOException e) {
			StringBuilder msg = new StringBuilder();
			msg.append("Unable to save file.\nSpecific Error : ");
			msg.append(e.getMessage());
			JOptionPane.showMessageDialog(charPanel, msg.toString(), "IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 * Output the character as an ascii sequence to the given file.
	 */
	private void writeAscii(File asciiFile) throws IOException {
		String txt = model.getData().getAsText().toString();
		FileOutputStream fos = new FileOutputStream(asciiFile, false);
		PrintWriter out = new PrintWriter(fos);
		out.println(txt);
		out.flush();

		out.close();
		fos.close();
	}

	/**
	 * Alter a copy of file object so that the filename's extension is changed.
	 * 
	 * @param f
	 *            File to copy and alter.
	 * @param newExtension
	 *            New file extension.
	 * @return The new file.
	 */
	private static final File replaceExtension(File f, String newExtension) {
		String ret = f.getAbsolutePath();
		int i = StringUtils.lastIndexOf(ret, ".");
		StringBuilder sb = new StringBuilder(256);
		sb.append(ret.substring(0, i));
		if (!newExtension.startsWith(".")) {
			sb.append(".");
		}
		sb.append(newExtension);
		return new File(sb.toString());
	}

	/**
	 * Resaves the file under the last saved filename , or if no last save has
	 * been made, show a file requester.
	 */
	protected void doSaveCharacterAsLast() {
		boolean notify = (lastSaveFile != null);
		if (lastSaveFile == null || !StringUtils.equals(lastSaveName, characterName())) {
			// Set up the dialog
			JMemoryFileChooser chooser = new JMemoryFileChooser();
			chooser.setFileFilter(characterFileFilter);
			chooser.setSelectedFile(new File(model.getData().getName() + FILE_EXTENSION));

			// Call dialog
			if (chooser.showSaveDialog(FILECHOOSER_DATAFILE, charPanel) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			lastSaveFile = chooser.getSelectedFile();
			lastSaveName = characterName();
		}

		// Save it
		try {
			doSaveXML(lastSaveFile);
			if (notify) {
				JOptionPane.showMessageDialog(charPanel, model.getData().getName() + " Saved", "Done", JOptionPane.INFORMATION_MESSAGE);
			}
			File asciiFile = replaceExtension(lastSaveFile, ".txt");
			writeAscii(asciiFile);

		} catch (IOException e) {
			StringBuilder msg = new StringBuilder();
			msg.append("Unable to save file.\nSpecific Error : ");
			msg.append(e.getMessage());
			JOptionPane.showMessageDialog(charPanel, msg.toString(), "IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	/**
	 * Ask where to save a file and then save it.
	 */
	protected void doSaveCharacterAs() {
		// Set up the dialog
		JMemoryFileChooser chooser = new JMemoryFileChooser();
		chooser.setFileFilter(characterFileFilter);
		chooser.setSelectedFile(new File(model.getData().getName() + FILE_EXTENSION));

		// Call dialog
		if (chooser.showSaveDialog(FILECHOOSER_DATAFILE, charPanel) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File targetFile = chooser.getSelectedFile();

		// Make ascii filename
		File targetAsciiFile = replaceExtension(targetFile, ".txt");

		// Save it
		try {
			// Save as serenity npc
			doSaveXML(targetFile);
			lastSaveFile = targetFile;
			lastSaveName = model.getData().getName();
			writeAscii(targetAsciiFile);
			PersonRecord record = PersonRecord.createFrom(model.getData());
			try {
				DatabaseSchema.getPersonDao().save(record);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			StringBuilder msg = new StringBuilder();
			msg.append("Unable to save file.\nSpecific Error : ");
			msg.append(e.getMessage());
			JOptionPane.showMessageDialog(charPanel, msg.toString(), "IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 * Save the current character to the given file.
	 * 
	 * @param selectedFile
	 */
	private void doSaveXML(File selectedFile) throws IOException {
		Element xml = model.getData().getXML();
		XMLOutputter output = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
		FileOutputStream fos = new FileOutputStream(selectedFile, false);
		try {
			output.output(xml, fos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fos.close();

	}

	/**
	 * Ask where to get a file and then load it.
	 */
	protected void doLoadCharacter() {
		// Set up the dialog
		JMemoryFileChooser chooser = new JMemoryFileChooser();
		chooser.setFileFilter(characterFileFilter);
		// Call dialog
		if (chooser.showOpenDialog(FILECHOOSER_DATAFILE, charPanel) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File targetFile = chooser.getSelectedFile();

		// Save it
		try {
			doLoad(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuilder msg = new StringBuilder();
			msg.append("Unable to save file.\nSpecific Error : ");
			msg.append(e.getMessage());
			JOptionPane.showMessageDialog(charPanel, msg.toString(), "IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 * @param targetFile
	 * @throws IOException
	 * @throws JDOMException
	 */
	private void doLoad(File targetFile) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(targetFile);
		Element root = doc.getRootElement();
		Person person = new Person();
		person.setXML(root);
		model.setData(person);
		charPanel.repaint();
	}

	/**
	 * Build window user interface
	 */
	private void createGUI() {
		charPanel = new CharacterPanel(model);
		charPanel.setName("characterPanel");
		charPanel.setDiceAction(diceAction);
		charPanel.setClearAction(clearAction);
		getContentPane().add(charPanel);
		initMenu();
		this.setJMenuBar(menuBar);
	}

	/**
	 * BUild menu
	 */
	private void initMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		loadItem = new JMenuItem(loadAction);
		saveItem = new JMenuItem(saveAction);
		archetypesManagerItem = new JMenuItem(archetypesManagerAction);
		saveAsItem = new JMenuItem(saveAsAction);
		outputAsciiItem = new JMenuItem(outputAsciiAction);
		randomPassengerItem = new JMenuItem(randomPassengerAction);
		previewWindowItem = new JCheckBoxMenuItem(previewWindowAction);
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(outputAsciiItem);
		fileMenu.add(previewWindowItem);
		fileMenu.add(randomPassengerItem);
		fileMenu.add(archetypesManagerItem);
		menuBar.add(fileMenu);
		loadItem.setAccelerator(loadStroke);
		saveAsItem.setAccelerator(saveAsStroke);
		saveItem.setAccelerator(saveStroke);
		outputAsciiItem.setAccelerator(asciiStroke);
		fileMenu.setMnemonic('f');
		saveItem.setMnemonic('s');
		saveAsItem.setMnemonic('v');
		loadItem.setMnemonic('o');
		outputAsciiItem.setMnemonic('a');

		registerAction(saveStroke, saveAction);
		registerAction(saveAsStroke, saveAsAction);
		registerAction(loadStroke, loadAction);
		registerAction(randomStroke, diceAction);
		registerAction(asciiStroke, outputAsciiAction);
	}

	/**
	 * Register a keystroke to an action.
	 * 
	 * @param stroke
	 * @param action
	 */
	private void registerAction(KeyStroke stroke, Action action) {
		getRootPane().getInputMap().put(stroke, action.getValue(Action.NAME));
		getRootPane().getActionMap().put(action.getValue(Action.NAME), action);
	}

	public static void main(String[] args) {
		try {
			DatabaseSchema.init();
			GeneratorWindow win = new GeneratorWindow("Composure - Serenity NPC Generator");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			win.setSize(640, 480);
			win.setVisible(true);
			List<PersonRecord> peopleRecords = DatabaseSchema.getPersonDao().readAll();
			System.out.println("People stored in db : "+peopleRecords.size());
			for( PersonRecord pr: peopleRecords ) {
				System.out.println(pr.getName()+" ["+pr.getArchetype()+"]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the name of the current character
	 */
	public String characterName() {
		return model.getData().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object,
	 * java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
	}

	/**
	 * Show random passenger generator window
	 */
	protected void doShowRandomPassengerGenerator() {
		JMemoryFrame rpgFrame = new JMemoryFrame("Random Passenger Generator");
		rpgFrame.recall();
		rpgFrame.add(new RandomPassengerPanel());
		rpgFrame.setVisible(true);
	}
}
