package uk.lug.gui.fight;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;

import uk.lug.MutableList;
import uk.lug.control.PeopleAddedListener;
import uk.lug.dao.records.PersonRecord;
import uk.lug.fight.RoundRow;
import uk.lug.gui.ToolbarPanel;
import uk.lug.gui.util.CachedImageLoader;

public class RoundPanel extends JPanel implements PeopleAddedListener {
	private RoundTrackerTableModel tableModel;
	private JTable table;
	private ToolbarPanel toolbar;

	private MutableList<RoundRow> rowList;

	public RoundPanel() {
		rowList = new MutableList<RoundRow>();
		buildUI();
	}

	private void buildUI() {
		setLayout(new BorderLayout());
		tableModel = new RoundTrackerTableModel(rowList);
		table = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(table);
		add(scroll, BorderLayout.CENTER);
		buildToolbar();
		add(toolbar, BorderLayout.NORTH);
	}

	private void buildToolbar() {
		toolbar = new ToolbarPanel();
		toolbar.addActionButton(initiativeRollAction);
	}

	private Action initiativeRollAction = new AbstractAction("Roll Initiative", CachedImageLoader.DICE_SMALL) {

		public void actionPerformed(ActionEvent e) {
			doRollInitiative();
		}
	};

	public void peopleAdded(final List<PersonRecord> records) {
		final List<RoundRow> rows = new ArrayList<RoundRow>();
		new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				for (PersonRecord record : records) {
					try {
						if (!hasRowWithName(record.getPerson().getName()) ) {
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
			}

		}.run();

	}

	protected void doRollInitiative() {
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				for ( RoundRow row : rowList ) {
					if ( StringUtils.isEmpty(row.getPlayer())) {
						int value = row.getInitiativeStats().roll();
						row.setInitiativeRoll(value);
					}
				}
				return null;
			}
		
			protected void done() {
				tableModel.fireTableDataChanged();
			};
		
		}.run();
	}

	protected boolean hasRowWithName(String name) {
		for (RoundRow rr : rowList) {
			if (StringUtils.equals(rr.getName(), name )) {
				return true;
			}
		}
		return false;
	}
}