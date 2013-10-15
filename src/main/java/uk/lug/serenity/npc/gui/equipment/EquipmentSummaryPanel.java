/**
 * 
 */
package uk.lug.serenity.npc.gui.equipment;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.model.Person;

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
public class EquipmentSummaryPanel extends JPanel implements DataModelListener<Person>, ListDataListener {
	private static final long serialVersionUID = 1L;

	private JLabel detailsLabel;
	
	private DataModel<Person> model;
	
	/**
	 * C'tor
	 * @param dmodel
	 */
	public EquipmentSummaryPanel( DataModel<Person> dmodel ) {
		model = dmodel;
		model.addDataModelListener( this );
		makeGUI();
	}
	
	/**
	 * Build GUI
	 */
	private void makeGUI() {
		detailsLabel = new JLabel("No data yet");
		add( detailsLabel, BorderLayout.NORTH );
		detailsLabel.setHorizontalAlignment( SwingConstants.CENTER );
	}
	
	/**
	 * Count items and weight for all equipment types.
	 * @return
	 */
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<html> Carrying <b>");
		sb.append( getEquipmentCount() );
		sb.append( "</b> weighing <b>" );
		sb.append( getEquipmentWeight() );
		sb.append( "</b>lbs</html>");
		return sb.toString();
	}
	
	/**
	 * Count how many items the player is carrying.
	 */
	public int getEquipmentCount() {
		int ret = 0;
		Person p = model.getData();
		if ( p!=null ) {
			ret = p.getEquipmentCount();
		}
		return ret;
	}
	
	/**
	 * Count how much weight the player is carrying.
	 */
	public double getEquipmentWeight() {
		double ret = 0;
		Person p = model.getData();
		if ( p!=null ) {
			ret = p.getCarriedWeight();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		if ( oldData!=null ) {
			oldData.getEquipment().removeListDataListener( this );
			oldData.getArmor().removeListDataListener( this );
			oldData.getExplosives().removeListDataListener( this );
			oldData.getMeleeWeapons().removeListDataListener( this );
			oldData.getRangedWeapons().removeListDataListener( this );
		}		
		newData.getEquipment().addListDataListener( this );
		newData.getArmor().addListDataListener( this );
		newData.getExplosives().addListDataListener( this );
		newData.getMeleeWeapons().addListDataListener( this );
		newData.getRangedWeapons().addListDataListener( this );
	}

	/**
	 * Redo the details label
	 */
	private void refreshDetails() {
		detailsLabel.setText( getSummary() );
		detailsLabel.repaint();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	public void intervalAdded(ListDataEvent e) {
		refreshDetails();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	public void intervalRemoved(ListDataEvent e) {
		refreshDetails();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	public void contentsChanged(ListDataEvent e) {
		refreshDetails();		
	}
	
}
