package uk.lug.gui.fight;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.StringUtils;

import uk.lug.MutableList;
import uk.lug.data.AbstractListBackedTableModel;
import uk.lug.fight.InititativeDice;
import uk.lug.fight.RoundRow;
import uk.lug.util.SwingHelper;

public class RoundTrackerTableModel extends AbstractListBackedTableModel<RoundRow> {
	private static final String WOUND = "Wound";
	private static final String STUN = "Stun";
	private static final String LIFE_POINTS = "LifePoints";
	private static final String INITIATIVE = "Initiative";
	private static final String ID = "Id";
	private static final String NAME = "Name";
	private static final String[] COLUMNS = { ID, NAME, INITIATIVE, LIFE_POINTS, STUN, WOUND };

	public List<String> saveRows() throws IOException {
		List<String> ret= new ArrayList<String>(dataList.size());
		for (RoundRow row : dataList ) {
			ret.add(row.toString());
		}
		return ret;
	}
	
	public void deserialize(List<String> lines) throws IOException, ClassNotFoundException {
		List<RoundRow> inputList = new ArrayList<RoundRow>(lines.size());
		for (String str : lines ){
			RoundRow row = new RoundRow();
			row.setFromString(str);
			inputList.add(row);
		}
		super.replaceAll(inputList);
	}
	
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
			if (!StringUtils.isEmpty(rowObject.getIdentifier())) {
				return rowObject.getIdentifier();
			} else if (!StringUtils.isEmpty(rowObject.getPlayer())) {
				return rowObject.getPlayer();
			} else {
				return rowObject.getName();
			}
		} else if (StringUtils.equals(columnName, INITIATIVE)) {
			return initiativeString(rowObject);
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

	private Object initiativeString(RoundRow rowObject) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isEmpty(rowObject.getPlayer())) {
			sb.append(rowObject.getInitiativeRoll() == null ? "" : rowObject.getInitiativeRoll());
			sb.append("   [");
			sb.append(rowObject.getInitiativeStats()==null ? "X" : rowObject.getInitiativeStats().toString());
			sb.append("]");
		} else{
			sb.append(rowObject.getInitiativeRoll() == null ? "" : rowObject.getInitiativeRoll());
		}
		return sb.toString();
	}

	public void addRow(RoundRow row) {
		super.addNewRowObject(row);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (COLUMNS[column].equals(INITIATIVE)) {
			return isPlayer(row);
		} else if (COLUMNS[column].equals(STUN)) {
			return isPlayer(row);
		} else if (COLUMNS[column].equals(WOUND)) {
			return isPlayer(row);
		}
		return false;
	}

	private boolean isPlayer(int row) {
		return !StringUtils.isEmpty(super.getRowObject(row).getPlayer());
	}

	@Override
	protected void setValue(Object newValue, RoundRow rowObject, String columnName) {
		if (StringUtils.equals(INITIATIVE, columnName)) {
			Integer i = getIntegerValue(newValue);
			if (i == null) {
				return;
			}
			rowObject.setInitiativeRoll(i);
		} else if (StringUtils.equals(STUN, columnName)) {
			Integer i = getIntegerValue(newValue);
			if (i == null) {
				return;
			}
			rowObject.setStun(i);
		} else if (StringUtils.equals(WOUND, columnName)) {
			Integer i = getIntegerValue(newValue);
			if (i == null) {
				return;
			}
			rowObject.setStun(i);
		}

	}

	private Integer getIntegerValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			return Integer.parseInt((String) value);
		}
		return null;
	}
}
