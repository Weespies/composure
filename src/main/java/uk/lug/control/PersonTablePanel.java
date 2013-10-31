package uk.lug.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.dao.records.PersonRecord;
import uk.lug.gui.ToolbarPanel;
import uk.lug.gui.pending.PendingPanel;
import uk.lug.gui.pending.PleaseWaitPanel;
import uk.lug.serenity.npc.gui.generator.GeneratorDialog;
import uk.lug.util.SwingHelper;

import static uk.lug.gui.util.CachedImageLoader.*;

public class PersonTablePanel extends JPanel {
	private PersonRecordTableModel tableModel;
	private List<PersonRecord> personList;
	private JTable table;
	private ToolbarPanel toolbar;
	private List<PeopleAddedListener> addFightListeners = new ArrayList<PeopleAddedListener>();

	public PersonTablePanel() {
		super();
		buildUI();
	}

	public void addFightListeners(PeopleAddedListener pal) {
		addFightListeners.add(pal);
	}

	public void removeFightListeners(PeopleAddedListener pal) {
		addFightListeners.remove(pal);
	}

	private void buildUI() {
		setLayout(new BorderLayout());
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				personList = new ArrayList<PersonRecord>();
				personList.addAll(DatabaseSchema.getPersonDao().readAll());
				return null;
			}

			protected void done() {
				buildTable();
			};

		}.run();
		buildToolbar();
		add(toolbar, BorderLayout.NORTH);
	}

	protected void buildTable() {
		tableModel = new PersonRecordTableModel(personList);
		table = new JTable(tableModel);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				respondToSelection();
			}
		});
		add(new JScrollPane(table), BorderLayout.CENTER);
		revalidate();
	}

	private void respondToSelection() {
		int[] rows = table.getSelectedRows();
		deleteAction.setEnabled(rows.length > 0);
		editAction.setEnabled(rows.length == 1);
		addToFightAction.setEnabled(rows.length > 0);
	}

	private void buildToolbar() {
		toolbar = new ToolbarPanel();
		toolbar.addActionButton(addAction);
		toolbar.addActionButton(deleteAction);
		toolbar.addActionButton(editAction);
		toolbar.addActionButton(addToFightAction);
		editAction.setEnabled(false);
		deleteAction.setEnabled(false);
		addToFightAction.setEnabled(false);
	}

	private Action addAction = new AbstractAction("Add", ADD_ICON) {

		public void actionPerformed(ActionEvent e) {
			doNew();
		}
	};

	private Action deleteAction = new AbstractAction("Delete", DELETE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doDelete();
		}
	};

	private Action editAction = new AbstractAction("Edit", CONFIGURE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doEdit();
		}
	};

	private Action addToFightAction = new AbstractAction("Add To Fight", GLOVES_ICON) {

		public void actionPerformed(ActionEvent e) {
			SwingHelper.runOutsideEventThread(new Runnable() {

				public void run() {
					doAddToFight();
				}
			});

		}
	};

	protected void doNew() {
		final GeneratorDialog dialog = new GeneratorDialog(this, "New Character");
		dialog.setVisible(true);
		new Thread(new Runnable() {

			public void run() {
				while (dialog.isVisible()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!dialog.isOk()) {
					return;
				}
				tableModel.addNewRowObject(dialog.getPersonRecord());
				table.getSelectionModel().clearSelection();
			}
		}).start();

	}

	protected void doAddToFight() {
		int[] rows = table.getSelectedRows();
		final List<PersonRecord> toAdd = new ArrayList<PersonRecord>(rows.length);
		for (int row : rows) {
			toAdd.add(tableModel.getRowObject(row));
		}
		for (final PeopleAddedListener pal : addFightListeners) {
			SwingHelper.runInEventThread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub

				}
			});
			pal.peopleAdded(toAdd);

		}
	}

	protected void doEdit() {
		final int row = table.getSelectedRow();
		PersonRecord personRecord = tableModel.getRowObject(row);
		final GeneratorDialog dialog = new GeneratorDialog(this, "Edit", personRecord);
		dialog.setVisible(true);
		new Thread(new Runnable() {

			public void run() {
				while (dialog.isVisible()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!dialog.isOk()) {
					return;
				}
				tableModel.fireTableRowsUpdated(row, row);
			}
		}).start();
	}

	protected void doConfigure() {
		// TODO Auto-generated method stub

	}

	protected void showFightPanel() {
		// TODO Auto-generated method stub

	}

	protected void doDelete() {
		StringBuilder sb = new StringBuilder();
		PersonRecord[] records = getSelectedRecords();
		sb.append("Do wish to delete");
		if (records.length == 1) {
			sb.append(records[0].getName());
		} else {
			sb.append(" these ");
			sb.append(records.length);
			sb.append(" characters");
		}
		sb.append(" ?");
		int reply = JOptionPane.showConfirmDialog(this, sb.toString(), "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (reply != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			DatabaseSchema.getPersonDao().delete(records);
			for (PersonRecord record : records) {
				tableModel.removeRow(record);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error deleting from database.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private PersonRecord[] getSelectedRecords() {
		int[] rows = table.getSelectedRows();
		PersonRecord[] ret = new PersonRecord[rows.length];
		for (int i = 0; i < rows.length; i++) {
			ret[i] = tableModel.getRowObject(rows[i]);
		}
		return ret;
	}
}
