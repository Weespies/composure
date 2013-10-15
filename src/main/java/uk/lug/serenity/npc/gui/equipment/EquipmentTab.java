/**
 * 
 */
package uk.lug.serenity.npc.gui.equipment;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import uk.lug.MutableList;
import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.managers.EquipmentManager;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.equipment.Equipment;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class EquipmentTab extends JPanel implements DataModelListener<Person> {
	public static final String EQUIPMENT_SELECTOR = "equipmentSelector";
	private static final long serialVersionUID = 1L;
	private EquipmentSelector<Equipment> selector;
	private DataModel<Person> model;
	private MutableList<Equipment> equipmentCatalog;
	private EquipmentSummaryPanel summaryPanel;
	
	/**
	 * Create a new equipment manager tab.
	 * @param dataModel
	 */
	public EquipmentTab( DataModel<Person> dataModel ) {
		model= dataModel;
		model.addDataModelListener( this );
		equipmentCatalog = new MutableList<Equipment>();
		equipmentCatalog.addAll( EquipmentManager.getEquipmentList() );
		build();
		
	}

	/**
	 * Build user interface
	 */
	private void build() {
		setLayout( new BorderLayout() );
		selector = new EquipmentSelector<Equipment>( new MutableList<Equipment>() , equipmentCatalog );
		add( selector, BorderLayout.CENTER );
		summaryPanel = new EquipmentSummaryPanel( model );
		add( summaryPanel, BorderLayout.NORTH );
		selector.setName(EQUIPMENT_SELECTOR);
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		selector.setSelectedList( newData.getEquipment() );
	}
}
