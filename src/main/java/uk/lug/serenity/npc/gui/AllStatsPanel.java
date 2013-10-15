/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.gui.controls.DerivedStatControl;
import uk.lug.serenity.npc.model.Person;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class AllStatsPanel extends JPanel implements DataModelListener<Person> {
	public static final String MAIN_STATS_PANEL = "mainStatsPanel";
	private static final long serialVersionUID = 1L;
	private Person dataModel;
	private MainStatsPanel mainStatsPanel;
	private JPanel derivedPanel ;
	private DerivedStatControl initiativeControl;
	private DerivedStatControl lifeControl;
	private DataModel<Person> model;
	
	public AllStatsPanel(DataModel<Person> dataModel) {
		model=dataModel;;
		model.addDataModelListener( this );
		createGUI();
	}

	/**
	 * 
	 */
	private void createGUI() {
		setLayout( new GridBagLayout() ) ;
		mainStatsPanel = new MainStatsPanel();
		mainStatsPanel.setName(MAIN_STATS_PANEL);
		mainStatsPanel.setBorder(new TitledBorder(new LineBorder(new Color(50,
				50, 50), 1), "Main Attributes"));
		derivedPanel= new JPanel();
		derivedPanel.setBorder(new TitledBorder(new LineBorder(new Color(50,
				50, 50), 1), "Derived Attributes"));
		derivedPanel.setLayout( new GridLayout(2,1) );
		
		add(mainStatsPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.75,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		add(derivedPanel, new GridBagConstraints(0,1, 2, 1, 1.0, 0.25,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return dataModel;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.dataModel = person;
		mainStatsPanel.setPerson( person );
		initiativeControl = new DerivedStatControl( dataModel.getInitiative() )  ;
		lifeControl = new DerivedStatControl( dataModel.getLife() ) ;
		lifeControl.setShowAsDice( false );
		//APply gui
		derivedPanel.removeAll();
		derivedPanel.add( initiativeControl);
		derivedPanel.add( lifeControl );		
	}

	/**
	 * @param newModel
	 */
	public void setDataModel(DataModel<Person> newModel) {
		if ( model!=null ) {
			model.removeDataModelListener( this );
		}
		model = newModel;
		model.addDataModelListener( this );
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		setPerson( newData );
	}
}


