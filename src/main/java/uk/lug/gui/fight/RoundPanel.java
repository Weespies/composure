package uk.lug.gui.fight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import uk.lug.MutableList;
import uk.lug.control.PeopleAddedListener;
import uk.lug.dao.records.PersonRecord;
import uk.lug.fight.RoundRow;
import uk.lug.gui.ToolbarPanel;
import uk.lug.gui.util.CachedImageLoader;
import uk.lug.util.SwingHelper;

public class RoundPanel extends JPanel implements PeopleAddedListener {
	private RoundTrackerTableModel tableModel;
	private JTable table;
	private ToolbarPanel toolbar;
	private static final String STORE_LIST_KEY = "commonPlayerList";
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private MutableList<RoundRow> rowList;
	private File tableFile = new File(System.getProperty("user.home") + File.separator + "composure" + File.separator + "roundTable.txt");
	private DataPanel dataPanel;
	private JSplitPane splitPane;

	public RoundPanel() {
		rowList = new MutableList<RoundRow>();
		buildUI();
	}

	private void buildUI() {
		setLayout(new BorderLayout());
		tableModel = new RoundTrackerTableModel(rowList);
		table = new JTable(tableModel);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				respondToRowSelected();
			}
		});
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				// TODO Auto-generated method stub
				JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				label.setOpaque(true);
				RoundRow roundRow = tableModel.getRowObject(row);
				label.setForeground(Color.BLACK);
				label.setBackground(Color.WHITE);	
				if ( tableModel.isPlayer(row) ) {
					SwingHelper.boldLabel(label);
				} else {
					SwingHelper.plainLabel(label);
					int damage = roundRow.getStun()+roundRow.getWounds();
					int life = roundRow.getLifeTotal();
					if ( damage>=life ){
						label.setForeground(Color.RED);
						label.setBackground(Color.LIGHT_GRAY);
					} else if ( damage>=(int)(life/2) ) {
						label.setForeground(Color.RED);
						label.setBackground(Color.WHITE);	
					} 
				}
				
				return label;
			}
		});
		JScrollPane scroll = new JScrollPane(table);
		add(scroll, BorderLayout.CENTER);
		buildToolbar();
		add(toolbar, BorderLayout.NORTH);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(scroll);
		add(splitPane, BorderLayout.CENTER);
		if (tableFile.exists()) {
			loadTableFile();
		} else {
			loadRememberedRows();
			saveTableFile();
		}
		dataPanel = new DataPanel();
		splitPane.setRightComponent(dataPanel);
	}

	private void loadTableFile() {
		try {
			List<String> lines = FileUtils.readLines(tableFile);
			tableModel.deserialize(lines);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Unable to reload table file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Unable to reload table file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveTableFile() {
		try {
			List<String> lines = tableModel.saveRows();
			FileUtils.writeLines(tableFile, lines);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to reload table file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadRememberedRows() {
		String stored = prefs.get(STORE_LIST_KEY, null);
		if (stored == null) {
			return;
		}
		for (String details : StringUtils.split(stored, ",")) {
			String player = StringUtils.substringBetween(details, "(", "|");
			String name = StringUtils.substringBetween(details, "|", "}");
			PlayerInfo info = PlayerInfo.create(player, name);
			addPlayerInfo(info);
		}
	}

	protected void respondToRowSelected() {
		int[] rows = table.getSelectedRows();
		removeRowAction.setEnabled(rows.length == 1);
		increaseActionCount.setEnabled(rows.length==1);
		clearActionCount.setEnabled(rows.length==1);
		if (rows.length == 1) {
			int row = rows[0];
			RoundRow item = tableModel.getRowObject(row);
			if (StringUtils.isEmpty(item.getPlayer())) {
				dataPanel.set(item);
			} else {
				dataPanel.clear();
			}
		}
	}

	private void buildToolbar() {
		toolbar = new ToolbarPanel();
		toolbar.addActionButton(initiativeRollAction);
		toolbar.addActionButton(sortAction);
		toolbar.addActionButton(removeRowAction);
		toolbar.addActionButton(addPlayerAction);
		toolbar.addActionButton(increaseActionCount);
		toolbar.addActionButton(clearActionCount);
		removeRowAction.setEnabled(false);

	}

	private Action initiativeRollAction = new AbstractAction("Roll Initiative", CachedImageLoader.DICE_SMALL) {

		public void actionPerformed(ActionEvent e) {
			doRollInitiative();
			saveTableFile();
		}
	};

	private Action increaseActionCount = new AbstractAction("Actions Count+", CachedImageLoader.ADD_ICON) {

		public void actionPerformed(ActionEvent e) {
			doIncreaseActionCount();
		}
	};

	private Action clearActionCount = new AbstractAction("Clear Actions", CachedImageLoader.DELETE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doClearActionCount();
		}
	};

	private Action removeRowAction = new AbstractAction("Remove Row", CachedImageLoader.DUDE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doRemoveRow();
			saveTableFile();
		}
	};

	private Action addPlayerAction = new AbstractAction("Add Palyer", CachedImageLoader.DUDE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doAddPlayer();
			saveTableFile();
		}
	};

	private Action sortAction = new AbstractAction("Sort", CachedImageLoader.UP_PLUS_ICON) {

		public void actionPerformed(ActionEvent e) {
			Collections.sort(rowList, comparator);
			tableModel.fireTableDataChanged();
			saveTableFile();
		}
	};
	private Comparator<RoundRow> comparator = new Comparator<RoundRow>() {

		public int compare(RoundRow o1, RoundRow o2) {
			Integer i1 = o1.getInitiativeRoll();
			Integer i2 = o2.getInitiativeRoll();
			i1 = i1 == null ? 0 : i1;
			i2 = i2 == null ? 0 : i2;
			if (i1 > i2) {
				return -1;
			} else if (i2 > i1) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	public void peopleAdded(final List<PersonRecord> records) {
		final List<RoundRow> rows = new ArrayList<RoundRow>();
		new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				for (PersonRecord record : records) {
					try {
						if (!hasRowWithName(record.getPerson().getName())) {
							rows.add(RoundRow.fromRecord(record));
						}

					} catch (Throwable t) {
						t.printStackTrace();
					}
				}

				return null;
			}

			@Override
			protected void done() {
				for (RoundRow row : rows) {
					tableModel.addNewRowObject(row);
				}
				saveTableFile();
			}

		}.run();

	}

	protected void doIncreaseActionCount() {
		if (table.getSelectedRows().length != 1) {
			return;
		}
		int row = table.getSelectedRows()[0];
		RoundRow roundRow = tableModel.getRowObject(row);
		roundRow.setActions(roundRow.getActions() + 1);
		tableModel.fireTableRowsUpdated(row, row);
	}

	protected void doClearActionCount() {
		if (table.getSelectedRows().length != 1) {
			return;
		}

		int row = table.getSelectedRows()[0];
		RoundRow roundRow = tableModel.getRowObject(row);
		roundRow.setActions(0);
		tableModel.fireTableRowsUpdated(row, row);
	}

	protected void doSort() {
		// TODO Auto-generated method stub

	}

	protected void doRemoveRow() {
		if (table.getSelectedRows().length != 1) {
			return;
		}
		final RoundRow row = tableModel.getRowObject(table.getSelectedRows()[0]);
		StringBuilder sb = new StringBuilder();
		sb.append("Remove row ");
		sb.append(row.getName());
		sb.append(" ?");
		int ret = JOptionPane.showConfirmDialog(this, sb.toString(), "Confirm", JOptionPane.YES_NO_OPTION);
		if (ret != JOptionPane.YES_OPTION) {
			return;
		}
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				tableModel.removeRow(row);
			}
		});
	}

	protected void doAddPlayer() {
		PlayerInfo info = new PlayerInfo();
		PlayerEntryDialog dialog = new PlayerEntryDialog(this, "Add player row", info);
		dialog.setVisible(true);
		if (!dialog.isOk()) {
			return;
		}
		addPlayerInfo(info);

	}

	private void addPlayerInfo(PlayerInfo info) {
		final RoundRow row = RoundRow.createForPlayer(info);
		System.out.println("Adding : " + info);
		try {
			SwingHelper.runNowInEventThread(new Runnable() {

				public void run() {
					tableModel.addNewRowObject(row);
				}
			});
			saveNamedList();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doRollInitiative() {
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				for (RoundRow row : rowList) {
					if (StringUtils.isEmpty(row.getPlayer())) {
						int value = row.getInitiativeStats().roll();
						row.setInitiativeRoll(value);
					}
				}
				return null;
			}

			protected void done() {
				Collections.sort(rowList, comparator);
				tableModel.fireTableDataChanged();
			};

		}.run();
	}

	protected boolean hasRowWithName(String name) {
		for (RoundRow rr : rowList) {
			if (StringUtils.equals(rr.getName(), name)) {
				return true;
			}
		}
		return false;
	}

	public void saveNamedList() {
		List<PlayerInfo> toSave = new ArrayList<PlayerInfo>();
		for (RoundRow row : rowList) {
			if (!StringUtils.isEmpty(row.getPlayer())) {
				PlayerInfo info = new PlayerInfo();
				info.setName(row.getName());
				info.setPlayer(row.getPlayer());
				toSave.add(info);
			}
		}
		if (toSave.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < toSave.size(); i++) {
			sb.append(toSave.get(i));
			if (i < toSave.size() - 1) {
				sb.append(",");
			}
		}
		prefs.put(STORE_LIST_KEY, sb.toString());
	}

}