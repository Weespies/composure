package uk.lug.gui.fight;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import uk.lug.data.AbstractListBackedTableModel;

public class DataRowTableModel extends AbstractListBackedTableModel<DataRow> {
	private static final String DAMAGE = "Damage";
	private static final String ROLL = "Roll";
	private static final String ITEM = "Item";
	private static final String[] COLUMNS = new String[] { ITEM, ROLL, DAMAGE };

	public DataRowTableModel(List<DataRow> dataList) {
		super(dataList);		
	}

	@Override
	protected String[] getColumnNames() {
		return COLUMNS;
	}

	@Override
	protected Object getValueFor(DataRow rowObject, String columnName) {
		if (StringUtils.equals(columnName,ITEM) ){
			return rowObject.getName();
		} else if (StringUtils.equals(columnName,ROLL) ){
			return rowObject.getRoll();
		}  else if (StringUtils.equals(columnName,DAMAGE) ){
			return rowObject.getDamage();
		}  else {
			throw new IllegalArgumentException("Unrecognised column:"+columnName);
		}
	}

}
