package uk.lug.gui.fight;

import javax.swing.DefaultCellEditor;

import uk.lug.gui.JNumberField;

public class InitiativeCellEditor extends DefaultCellEditor {
	private Integer value=0;
	private Integer maximum;
	private Integer minimum;

	public InitiativeCellEditor(Integer value,Integer minimum, Integer maximum) {
		super(new JNumberField(2));
		this.minimum = minimum;
		this.maximum = maximum;
		this.value=value;
		((JNumberField)super.getComponent()).setAllowNegatives(false);
	}
	
	@Override
	public Object getCellEditorValue() {
		return value;
	}

}
