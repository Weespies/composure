/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.gui.equipment.ArmorTab;
import uk.lug.serenity.npc.gui.equipment.EquipmentTab;
import uk.lug.serenity.npc.gui.equipment.ExplosivesTab;
import uk.lug.serenity.npc.gui.equipment.MeleeTab;
import uk.lug.serenity.npc.gui.equipment.RangedTab;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.random.Generator;
import uk.lug.serenity.npc.random.archetype.Archetype;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Primary panel for holding character information.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class CharacterPanel extends JPanel implements DataModelListener<Person>{
	public static final String EXPLOSIVES_TAB = "explosivesTab";
	public static final String RANGED_TAB = "rangedTab";
	public static final String MELEE_TAB = "meleeTab";
	public static final String ARMOR_TAB = "armorTab";
	public static final String EQUIPMENT_TAB = "equipmentTab";
	public static final String SKILL_PANEL = "skillPanel";
	public static final String TRAITS_PANEL = "traitsPanel";
	public static final String STATS_PANEL = "statsPanel";
	public static final String TAB_PANE = "tabPane";
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabPane;
	private BioPanel bioPanel;
	private AllStatsPanel statsPanel;
	private TraitControlPanel traitsPanel;
	private SkillSheetPanel skillPanel;
	private JPanel statHoldingPanel;
	private JPanel traitHoldingPanel;
	private InfoEditorPanel infoEditor;
	private EquipmentTab equipmentTab;
	private ArmorTab armorTab;
	private MeleeTab meleeTab;
	private RangedTab rangedTab;
	private ExplosivesTab explosivesTab;
	private DataModel<Person> model;
	
	public CharacterPanel( DataModel<Person> dataModel) {
		model = dataModel;
		model.addDataModelListener(this);
		createGUI();
	}
	
	/**
	 * Construct the gui.
	 */
	private void createGUI() {
		setLayout( new GridBagLayout() ) ;
		bioPanel = new BioPanel( model );
		bioPanel.setName("bioPanel");
		add(bioPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		tabPane = new JTabbedPane();
		tabPane.setName(TAB_PANE);
		add(tabPane, new GridBagConstraints(0, 1, 2, 2, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		statHoldingPanel = new JPanel( new GridBagLayout() );
		statsPanel = new AllStatsPanel( model );
		statHoldingPanel.add( statsPanel, new GridBagConstraints(0,0,1,0,1.0,0,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		statHoldingPanel.add( new JLabel(""), new GridBagConstraints(0,1,1,0,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		
		tabPane.add( statHoldingPanel, "Stats" );
		
		traitsPanel = new TraitControlPanel( model );
		traitHoldingPanel = new JPanel( new GridBagLayout() );
		traitHoldingPanel.add( traitsPanel, new GridBagConstraints(0,0,1,0,1.0,0,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		traitHoldingPanel.add( new JLabel(""), new GridBagConstraints(0,1,1,0,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		
		tabPane.add( traitHoldingPanel, "Traits");
		skillPanel = new SkillSheetPanel(model);
		tabPane.add( skillPanel, "Skills");
		
		//Equipment
		equipmentTab = new EquipmentTab(model);
		armorTab = new ArmorTab(model);
		meleeTab = new MeleeTab(model);
		rangedTab = new RangedTab(model);
		explosivesTab = new ExplosivesTab( model );
		tabPane.add( equipmentTab, "Equipment");
		tabPane.add( armorTab, "Armor");
		tabPane.add( meleeTab, "Melee");
		tabPane.add( rangedTab, "Ranged");
		tabPane.add( explosivesTab, "Explosives");
		
		infoEditor = new InfoEditorPanel(model);
		tabPane.add( infoEditor,"Info");
		
		statsPanel.setName(STATS_PANEL);
		traitsPanel.setName(TRAITS_PANEL);
		skillPanel.setName(SKILL_PANEL);
		equipmentTab.setName(EQUIPMENT_TAB);
		armorTab.setName(ARMOR_TAB);
		meleeTab.setName(MELEE_TAB);
		rangedTab.setName(RANGED_TAB);
		explosivesTab.setName(EXPLOSIVES_TAB);
	}
	
	/**
	 * Sets the action for the randomize button.
	 * @param diceAction
	 */
	public void setDiceAction( Action diceAction ) {
		bioPanel.setDiceAction( diceAction );
	}
	
	/**
	 * Randomize character based on current level and current profile.
	 */
	public void randomize() {
		Runnable run = new Runnable() {
			public void run() {
				bioPanel.enableDiceAction( false );
				int level = model.getData().getLevel();
				Archetype archetype = bioPanel.getSelectedArchetype();
				Person newPerson = Generator.getRandomPerson( archetype, level );
				model.setData( newPerson);
				bioPanel.enableDiceAction( true );		
			}
		};
		Thread thread = new Thread( run ) ;
		thread.start();
		
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		//Strange, we don't actually need to do anything.
	}

	/**
	 * Pass an action which the user performs to clear the character
	 * into the Biopanel, which attaches it to the GUI component.
	 * @param clearAction action which clears the character
	 */
	public void setClearAction(Action clearAction) {
		bioPanel.setClearAction( clearAction );
	}

	/**
	 * Clears the character of everything except their name.
	 */
	public void clearCharacter() {
		Runnable run = new Runnable() {
			public void run() {
				bioPanel.enableDiceAction( false );
				
				//Remember details of previous character
				String fname = model.getData().getFemaleName();
				String mname = model.getData().getMaleName();
				boolean male = model.getData().isMale();
				int level = model.getData().getLevel();
				
				Person emptyPerson = Generator.getEmptyPerson( level);
				//Apply old details
				emptyPerson.setMaleName( mname );
				emptyPerson.setFemaleName( fname );
				emptyPerson.setMale( male );
				model.setData( emptyPerson );
								
				bioPanel.enableDiceAction( true );		
			}
		};
		Thread thread = new Thread( run ) ;
		thread.start();
	}

	/**
	 * 
	 */
	public void refreshArchetypes() {
		bioPanel.refreshArchetypes();
	}

}
