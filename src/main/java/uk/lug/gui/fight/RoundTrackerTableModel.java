package uk.lug.gui.fight;

import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.StringUtils;

import uk.lug.MutableList;
import uk.lug.data.AbstractListBackedTableModel;
import uk.lug.fight.RoundRow;
import uk.lug.util.SwingHelper;

public class RoundTrackerTableModel extends AbstractListBackedTableModel<RoundRow> {
	private static final String WOUND = "Wound";
	private static final String STUN = "Stun";
	private static final String LIFE_POINTS = "LifePoints";
	private static final String INITIATIVE = "Initiative";
	private static final String ID = "Id";
	private static final String NAME = "Name";
	private static final String[] COLUMNS = { NAME, ID, INITIATIVE, LIFE_POINTS, STUN, WOUND };

	public RoundTrackerTableModel(MutableList<RoundRow> dataList) {
		super(dataList);
		dataList.addListDataListener(new ListDataListener() {

			public void intervalAdded(ListDataEvent e) {
				final int row1 = e.getIndex0();
				final int row2 = e.getIndex1();
				SwingHelper.runInEventThread(new Runnable() {

					public void run() {
						fireRowsAdded(row1, row2);
					}
				});
			}

			public void intervalRemoved(ListDataEvent e) {
				final int row1 = e.getIndex0();
				final int row2 = e.getIndex1();
				SwingHelper.runInEventThread(new Runnable() {

					public void run() {
						fireRowsRemoved(row1, row2);
					}
				});
			}

			public void contentsChanged(ListDataEvent e) {
				final int row1 = e.getIndex0();
				final int row2 = e.getIndex1();
				SwingHelper.runInEventThread(new Runnable() {

					public void run() {
						fireRowsUpdated(row1, row2);
					}
				});
			}
		});
	}

	private void fireRowsAdded(int row1, int row2) {
		int r1 = row1 < row2 ? row1 : row2;
		int r2 = row2 > row1 ? row2 : row1;
		super.fireTableRowsInserted(r1, r2);
	}

	private void fireRowsRemoved(int row1, int row2) {
		int r1 = row1 < row2 ? row1 : row2;
		int r2 = row2 > row1 ? row2 : row1;
		super.fireTableRowsDeleted(r1, r2);
	}

	private void fireRowsUpdated(int row1, int row2) {
		int r1 = row1 < row2 ? row1 : row2;
		int r2 = row2 > row1 ? row2 : row1;
		super.fireTableRowsUpdated(r1, r2);
	}

	@Override
	protected String[] getColumnNames() {
		return COLUMNS;
	}

	@Override
	protected Object getValueFor(RoundRow rowObject, String columnName) {
		if (StringUtils.equals(columnName, NAME)) {
			return rowObject.getName();
		} else if (StringUtils.equals(columnName, ID)) {
			return StringUtils.isEmpty(rowObject.getIdentifier()) ? rowObject.getName() : rowObject.getIdentifier();
		} else if (StringUtils.equals(columnName, INITIATIVE)) {
			StringBuilder sb = new StringBuilder();
			sb.append(rowObject.getInitiativeRoll());
			sb.append("   [");
			sb.append(rowObject.getInitiativeStats().toString());
			sb.append("]");
			if (rowObject.getInitiativeRoll() != null) {
				
			}
			return sb.toString();
		} else if (StringUtils.equals(columnName, LIFE_POINTS)) {
			return rowObject.getLifeTotal();
		} else if (StringUtils.equals(columnName, STUN)) {
			return rowObject.getStun();
		} else if (StringUtils.equals(columnName, WOUND)) {
			return rowObject.getWounds();
		} else {
			throw new IllegalArgumentException("Unrecognised table column " + columnName);
		}
	}

	public void addRow(RoundRow row) {
		super.addNewRowObject(row);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		if ( COLUMNS[column].equals(INITIATIVE)) {
			return true;
		}
		return false;
	}
}
