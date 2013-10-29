package uk.lug.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.j256.ormlite.dao.Dao;

import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.dao.records.PersonRecord;
import uk.lug.gui.ToolbarPanel;
import uk.lug.gui.pending.PendingPanel;
import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.gui.generator.GeneratorDialog;
import uk.lug.util.SwingHelper;

import static uk.lug.gui.util.CachedImageLoader.*;

public class PersonTablePanel extends JPanel {
	private PersonRecordTableModel tableModel;
	private PendingPanel tablePanel;
	private List<PersonRecord> personList;
	private JTable table;
	private ToolbarPanel toolbar;

	public PersonTablePanel() {
		super();
		buildUI();
	}

	private void buildUI() {
		setLayout(new BorderLayout());
		tablePanel = new PendingPanel("Reading characters...") {

			@Override
			protected boolean performPending() {
				try {
					personList = DatabaseSchema.getPersonDao().readAll();
					return true;
				} catch (Throwable t) {
					t.printStackTrace();
					return false;
				}

			}

			@Override
			protected void buildPostPendingUI() {
				tableModel = new PersonRecordTableModel(personList);
				table = new JTable(tableModel);
				table.setRowSelectionAllowed(true);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {
						SwingHelper.runOutsideEventThread(new Runnable() {

							public void run() {
								respondToSelection();
							}
						});

					}
				});
				setLayout(new BorderLayout());
				add(new JScrollPane(table), BorderLayout.CENTER);
			}
		};
		add(tablePanel, BorderLayout.CENTER);
		buildToolbar();
		add(toolbar, BorderLayout.NORTH);
	}

	private void respondToSelection() {
		int[] rows = table.getSelectedRows();
		deleteAction.setEnabled(rows.length > 0);
	}

	private void buildToolbar() {
		toolbar = new ToolbarPanel();
		toolbar.addActionButton(addAction);
		toolbar.addActionButton(deleteAction);
		deleteAction.setEnabled(false);
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
			}
		}).start();

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
			for( PersonRecord record : records ) {
				tableModel.removeRow(record);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error deleting from database.\n"+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
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
