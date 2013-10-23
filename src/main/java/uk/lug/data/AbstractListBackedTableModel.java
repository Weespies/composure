package uk.lug.data;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;

import uk.lug.util.SwingHelper;

public abstract class AbstractListBackedTableModel<T> extends DefaultTableModel {
	private static final long serialVersionUID = -5241080266888639705L;
	protected List<T> dataList;
	protected static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MMM/yyyy");
	protected static final SimpleDateFormat SDTF = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");

	/**
	 * @return an unmodifable copy of the data list.
	 */
	public List<T> getDataCopy() {
		return Collections.unmodifiableList(dataList);
	}

	public void replaceAll(final List<T> refreshedList) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				dataList.clear();
				dataList.addAll(refreshedList);
				fireTableDataChanged();
			}
		});
	}

	public void addNewRowObject(final T rowObject) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				dataList.add(rowObject);
				int row = dataList.size();
				fireTableRowsInserted(row - 1, row);
			}
		});
	}

	public AbstractListBackedTableModel(List<T> dataList) {
		super();
		this.dataList = dataList;
	}

	public void removeRow(final T rowObject) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				int row = dataList.indexOf(rowObject);
				if (row == -1) {
					throw new IllegalArgumentException("Unable to find object in table's list.");
				}
				dataList.remove(row);
				fireTableRowsDeleted(row, row);
			}
		});
	}

	@Override
	public int getRowCount() {
		return (dataList == null ? 0 : dataList.size());
	}

	protected abstract String[] getColumnNames();

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public String getColumnName(int column) {
		return getColumnNames()[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object value = getValueFor(dataList.get(row), getColumnNames()[column]);
		return value;
	}

	protected abstract Object getValueFor(T rowObject, String columnName);

	public T getRowObject(int row) {
		return dataList.get(row);
	}

	public void changeListData(final List<T> replacementData) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				dataList.clear();
				dataList.addAll(replacementData);
				fireTableDataChanged();
			}
		});
	}

	public void updateRow(final int row) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				fireTableRowsUpdated(row, row);
			}
		});
	}

	/**
	 * Updates the displayed object. Note that the passed in object is compared
	 * to the list objects by it's equals() method.
	 * 
	 * @param object
	 */
	public void updateRowFor(final T object) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				for (int i = 0; i < getRowCount(); i++) {
					if (dataList.get(i).equals(object)) {
						updateRow(i);
					}
				}
			}
		});
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return isColumnEditable(dataList.get(row), getColumnNames()[column]);
	}

	/**
	 * Return true if the given object's is editable for the given column name.
	 * By default returns false.
	 * 
	 * @param t
	 * @param string
	 * @return
	 */
	protected boolean isColumnEditable(T rowData, String columnName) {
		return false;
	}

	@Override
	public void setValueAt(final Object aValue, final int row, final int column) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				setValue(aValue, dataList.get(row), getColumnNames()[column]);
			}
		});

	}

	/**
	 * Set the value of an object by its column name.
	 * 
	 * @param newValue
	 *            the new value
	 * @param rowObject
	 *            the object to set the value on
	 * @param columnName
	 *            the column name for the value
	 */
	protected void setValue(Object newValue, T rowObject, String columnName) {
		// By default do nothing
	}

	public int getColumnFor(String columnName) {
		for (int i = 0; i < getColumnNames().length; i++) {
			if (StringUtils.equals(columnName, getColumnNames()[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException("no column named : " + columnName);
	}

	public void sortRows(final Comparator<T> comparator) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				Collections.sort(dataList, comparator);
				fireTableDataChanged();
			}
		});
	}

	public void clear() {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				int size = dataList.size();
				dataList.clear();
				fireTableRowsDeleted(0, size);
			}
		});
	}

	@Override
	public void fireTableDataChanged() {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				doFireTableDataChanged();
			}
		});

	}

	protected void doFireTableDataChanged() {
		super.fireTableDataChanged();
	}

	@Override
	public void fireTableStructureChanged() {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				doFireTableStructureChanged();
			}
		});
	}

	protected void doFireTableStructureChanged() {
		super.fireTableDataChanged();
	}

	@Override
	public void fireTableRowsInserted(final int firstRow, final int lastRow) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				doFireTableRowsInserted(firstRow, lastRow);
			}
		});

	}

	protected void doFireTableRowsInserted(int firstRow, int lastRow) {
		super.fireTableRowsInserted(firstRow, lastRow);
	}

	@Override
	public void fireTableRowsUpdated(final int firstRow, final int lastRow) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				doFireTableRowsUpdated(firstRow, lastRow);
			}
		});
	}

	public void doFireTableRowsUpdated(int firstRow, int lastRow) {
		super.fireTableRowsUpdated(firstRow, lastRow);
	}

	@Override
	public void fireTableRowsDeleted(final int firstRow, final int lastRow) {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				doFireTableRowsDeleted(firstRow, lastRow);
			}
		});
	}

	public void doFireTableRowsDeleted(int firstRow, int lastRow) {
		super.fireTableRowsDeleted(firstRow, lastRow);
	}


	@Override
	public void fireTableCellUpdated(final int row,final  int column) {
		// TODO Auto-generated method stub
		SwingHelper.runInEventThread(new Runnable() {
			
			public void run() {
				doFireTableCellUpdated(row, column);
			}
		});
	}
	
	public void doFireTableCellUpdated(int row, int column) {
		super.fireTableCellUpdated(row, column);
	}
	


	@Override
	public void fireTableChanged(final TableModelEvent e) {
		SwingHelper.runInEventThread(new Runnable() {
			
			public void run() {
				doFireTableChanged(e);				
			}
		});
	}

	public void doFireTableChanged(TableModelEvent e) {
		super.fireTableChanged(e);
	}

}
