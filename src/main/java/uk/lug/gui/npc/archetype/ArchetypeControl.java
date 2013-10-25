/**
 * 
 */
package uk.lug.gui.npc.archetype;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.RandomStringUtils;

import uk.lug.gui.npc.archetype.skills.SkillBiasControl;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.util.RandomFactory;

/**
 * @author Luggy
 *
 */
public class ArchetypeControl extends JPanel {
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwyxz";
	private ArchetypeDetailsPanel detailsControl;
	private StatBiasControl statControl;
	private SkillBiasControl skillControl;
	private JTabbedPane tabPane;
	private Archetype dataModel;
	private JPanel detailsPanel;
	
	
	public interface ComponentNames {
		public static final String TAB_PANE = "ArchetypeControl.tabPane";
	}
	
	/**
	 * @param archetype
	 */
	public ArchetypeControl(Archetype archetype) {
		super();
		this.dataModel = archetype;
		build();
	}
	
	/**
	 * 
	 */
	private void build() {
		setLayout( new BorderLayout() ) ;
		tabPane = new JTabbedPane();
		tabPane.setName( ComponentNames.TAB_PANE);
		add( tabPane, BorderLayout.CENTER );
		
		buildDetailsPanel();
		
		skillControl = new SkillBiasControl( dataModel );
		
		tabPane.add("Details & Stats", detailsPanel );
		tabPane.add("Skills", skillControl ) ;
		
	}
	
	private void buildDetailsPanel() {
		detailsPanel = new JPanel(new GridBagLayout() );
		detailsControl = new ArchetypeDetailsPanel( dataModel );
		statControl = new StatBiasControl( dataModel.getStatBiases() );
		
		detailsPanel.add( detailsControl, new GridBagConstraints(0,0,1,1, 1.0,0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(4,2,4,2),0,0));
		detailsPanel.add( statControl, new GridBagConstraints(0,1,1,1, 1.0,1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0,2,0,2),0,0));
		detailsControl.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Details") );
		statControl.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Stats") );
		
	}
	
	/**
	 * @return the dataModel
	 */
	public Archetype getArchetype() {
		return dataModel;
	}

	/**
	 * @param dataModel the dataModel to set
	 */
	public void setArchetype(Archetype dataModel) {
		this.dataModel = dataModel;
		detailsControl.setArchetype( this.dataModel );
		statControl.setArchetype( this.dataModel );
		skillControl.setArchetype( this.dataModel );
	}
	
	/**
	 * Add a listener that gets fired whenever the name field changes.
	 * @param listener
	 */
	public void addNameChangeListener(ChangeListener listener) {
		detailsControl.addNameChangeListener( listener );
	}
	
	/**
	 * Remove a listener that gets fired whenever the name field changes.
	 * @param listener
	 */
	public void removeNameChangeListener(ChangeListener listener) {
		detailsControl.removeNameChangeListener( listener );	
	}
}
