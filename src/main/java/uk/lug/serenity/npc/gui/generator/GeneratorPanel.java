/**
 * 
 */
package uk.lug.serenity.npc.gui.generator;

import static uk.lug.gui.util.CachedImageLoader.*;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.dao.records.PersonRecord;
import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.gui.ToolbarPanel;
import uk.lug.gui.archetype.skills.JMemoryFileChooser;
import uk.lug.gui.archetype.skills.JMemoryFrame;
import uk.lug.serenity.npc.gui.CharacterPanel;
import uk.lug.serenity.npc.gui.RandomPassengerPanel;
import uk.lug.serenity.npc.gui.SummaryFrame;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.random.Generator;
import uk.lug.util.SwingHelper;

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
public class GeneratorPanel extends JPanel implements DataModelListener<Person> {
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	public static final String FILE_EXTENSION = ".serenitynpc";
	public static final String LAST_SAVE_KEY = "lastSaveDir";
	private static final String FILECHOOSER_DATAFILE = "composureNPC";
	private static final String FILECHOOSER_ASCII_OUT = "composureNPCASCII";
	protected static final Cursor CURSOR_WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	protected static final Cursor CURSOR_NORMAL = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	protected static final String ASCII_FILE_EXTENSION = ".txt";

	private ToolbarPanel menuBar;
	// private JMenuItem randomPassengerItem;
	private JCheckBoxMenuItem previewWindowItem;
	private CharacterPanel charPanel;
	private File lastSaveFile = null;
	private String lastSaveName = null;
	private DataModel<Person> model;
	private SummaryFrame summaryWindow = null;
	private PersonRecord personRecord = null;
	private List<GeneratorListener> generatorListeners = new ArrayList<GeneratorListener>();
	private JCheckBox pcheckBox;
	private JTextField tagField;
	private JPanel southPanel;

	public Person getPerson() {
		return model.getData();
	}

	public void addGeneratorListener(GeneratorListener gl) {
		generatorListeners.add(gl);
	}

	public void removeGeneratorListener(GeneratorListener gl) {
		generatorListeners.remove(gl);
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
	// private Action archetypesManagerAction = new
	// AbstractAction("Manage Archetypes", ARCHETYPES_MANAGER_ICON) {
	// public void actionPerformed(ActionEvent ae) {
	// doShowArchtypesManager();
	// }
	// };

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

			doSave();
		}
	};

	/**
	 * Action invoked to save a character.
	 */
	private Action close = new AbstractAction("Cancel", DELETE_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {

			doCancel();
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
	private Action previewWindowAction = new AbstractAction("Preview Window") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doShowPreviewWindow();
		}
	};

	/**
	 * Construct a generator window.
	 * 
	 * @param windowTitle
	 */
	public GeneratorPanel() {
		super();

		model = new DataModel<Person>();
		model.addDataModelListener(this);
		buildUI();
		model.setData(Generator.getRandomPerson());
	}

	protected void doCancel() {
		for (GeneratorListener gl : generatorListeners) {
			gl.closed();
		}
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
		// if (archetypeFrame == null) {
		// archetypeFrame = new JMemoryFrame("Archetype control");
		// } else {
		// archetypeFrame.setVisible(true);
		// }
		//
		// archetypeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//
		// final ArchetypeManagerPanel archetypesManager = new
		// ArchetypeManagerPanel();
		// archetypeFrame.add(archetypesManager);
		// archetypeFrame.addWindowListener(new WindowAdapter() {
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// *
		// java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
		// * )
		// */
		// @Override
		// public void windowClosing(WindowEvent e) {
		// e.getWindow().setVisible(false);
		// }
		// });
		//
		// JMenuBar menuBar = new JMenuBar();
		// menuBar.add(archetypesManager.getArchetypesMenu());
		// archetypeFrame.setJMenuBar(menuBar);
		// archetypeFrame.addWindowListener(new WindowAdapter() {
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// *
		// java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
		// * )
		// */
		// @Override
		// public void windowClosing(WindowEvent e) {
		// super.windowClosing(e);
		// archetypesManager.saveList();
		// refreshArchetypes();
		// }
		// });
		// archetypeFrame.setSize(640, 480);
		// archetypeFrame.setVisible(true);

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
	protected void doSave() {
		Person person = model.getData();
		if (personRecord == null) {
			personRecord = personRecord.createFrom(person);
		} else if (!StringUtils.equals(personRecord.getName(), person.getName())) {
			int ret = JOptionPane.showOptionDialog(this, "An character with that name already exists.", "Duplicate name", JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "Save as new", "Overwrite" }, "Overwrite");
			if (ret == JOptionPane.CLOSED_OPTION) {
				return;
			} else if (ret == 0) {
				personRecord.setId(null);
			}
		}
		personRecord.setIsPlayer(pcheckBox.isSelected());
		personRecord.setTags(tagField.getText());
		try {
			DatabaseSchema.getPersonDao().save(personRecord);
			for (GeneratorListener gl : generatorListeners) {
				gl.saved(personRecord);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error writing to database.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

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
	private void buildUI() {
		setLayout(new BorderLayout());
		charPanel = new CharacterPanel(model);
		charPanel.setName("characterPanel");
		charPanel.setDiceAction(diceAction);
		charPanel.setClearAction(clearAction);
		add(charPanel, BorderLayout.CENTER);
		initMenu();
		add(menuBar, BorderLayout.NORTH);
		buildSouthPanel();
		add(southPanel,BorderLayout.SOUTH);
	}

	private void buildSouthPanel() {
		tagField = new JTextField(20);
		pcheckBox = new JCheckBox("is player");
		southPanel = new JPanel(new GridBagLayout());
		southPanel.add(pcheckBox, new GridBagConstraints(0, 0, 1, 1, .3d, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				SwingHelper.DEFAULT_INSETS, 0, 0));
		southPanel.add(new JLabel("Tags"), new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,10,2,2),0,0));
		southPanel.add( tagField, new GridBagConstraints(2,0,1,1,.7d,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,SwingHelper.DEFAULT_INSETS,0,0));
	}

	/**
	 * BUild menu
	 */
	private void initMenu() {
		menuBar = new ToolbarPanel();
		menuBar.setBorder(BorderFactory.createEtchedBorder());
		menuBar.addActionButton(saveAction);
		menuBar.addActionButton(outputAsciiAction);
		menuBar.addActionButton(previewWindowAction);
	}

	/**
	 * Register a keystroke to an action.
	 * 
	 * @param stroke
	 * @param action
	 */
	private void registerAction(KeyStroke stroke, Action action) {
		// getRootPane().getInputMap().put(stroke,
		// action.getValue(Action.NAME));
		// getRootPane().getActionMap().put(action.getValue(Action.NAME),
		// action);
	}

	public static void main(String[] args) {
		try {
			DatabaseSchema.init();
			JFrame win = new JFrame("Composure - Serenity NPC Generator");
			GeneratorPanel gp = new GeneratorPanel();
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			win.setSize(640, 480);
			win.getContentPane().add(gp);
			win.setVisible(true);
			List<PersonRecord> peopleRecords = DatabaseSchema.getPersonDao().readAll();
			System.out.println("People stored in db : " + peopleRecords.size());
			for (PersonRecord pr : peopleRecords) {
				System.out.println(pr.getName() + " [" + pr.getArchetype() + "]");
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
