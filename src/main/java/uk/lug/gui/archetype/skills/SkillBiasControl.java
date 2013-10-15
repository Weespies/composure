/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.lug.gui.CachedImageLoader;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.serenity.npc.random.archetype.skills.GeneralSkillBias;
import uk.lug.util.RandomFactory;

/**
 * @author Luggy
 *
 */
public class SkillBiasControl extends JPanel {
	private static final Icon WEIGHTED_GROUP_ICON = CachedImageLoader.getCachedIcon( "images/weighted_group_16.png");
	private static final Icon EXCLUSIVE_GROUP_ICON = CachedImageLoader.getCachedIcon( "images/exclusive_group_16.png");
	private static final Icon ADD_SKILL_ICON = CachedImageLoader.getCachedIcon( "images/add.png");
	
	private Archetype dataModel;
	private HashMap<GeneralSkill, Weighting> generalBias;
	private JScrollPane leftScroll;
	private JScrollPane rightScroll;
	private GeneralSkillBiasControl generalControl = null;
	private ChildSkillList childControl = null;
	private JPanel rightPanel;
	private JToolBar childActionBar;
	private JButton makeExclusiveButton;
	private JButton makeWeightedButton;
	private GeneralSkillBias selectedBias=null;
	private JButton newSkillButton;
	
	
	
	private Action newSkillAction = new AbstractAction("Specialty", ADD_SKILL_ICON) {
		{
			this.putValue(Action.SHORT_DESCRIPTION,"Make a new specialty.");
			this.putValue(Action.NAME, "Specialty");
		}
		public void actionPerformed(ActionEvent ae) {
			makeNewSpecialty();
		}
	};
	
	
	private Action makeWeightedGroupAction= new AbstractAction("Weighted", WEIGHTED_GROUP_ICON) {
		{
			this.putValue(Action.SHORT_DESCRIPTION,"Make a new weighted group.");
			this.putValue(Action.NAME, "Weighted");
		}
		public void actionPerformed(ActionEvent ae) {
			makeWeightedGroup();
		}
	};
	
	private Action makeExclusiveGroupAction= new AbstractAction("Exclusive", EXCLUSIVE_GROUP_ICON) {
		{
			this.putValue(Action.SHORT_DESCRIPTION,"Make a new exclusive group.");
		}
		public void actionPerformed(ActionEvent ae) {
			makeExclusiveGroup();	
		}
	};
	
	
	/**
	 * @param dataModel
	 */
	public SkillBiasControl(Archetype archetype) {
		super();
		this.dataModel = archetype;
		build();
	}
	
	/**
	 * 
	 */
	protected void makeNewSpecialty() {
		String name = JOptionPane.showInputDialog(this,"New specialty name");
		if ( name==null ) {
			return;
		}
		
		selectedBias.addNewChildSkill( name );
		childControl.refreshList();		
	}

	/**
	 * 
	 */
	private void build() {
		setLayout( new GridBagLayout() );
		buildGeneralControl();
	}
	
	/**
	 * 
	 */
	private void buildGeneralControl() {
		generalBias =  new HashMap<GeneralSkill, Weighting>();
		for ( GeneralSkill key : dataModel.getGeneralSkillBiases().keySet() ) {
			generalBias.put( key, dataModel.getGeneralSkillBiases().get(key).getWeighting() );
		}
		generalControl = new GeneralSkillBiasControl( generalBias );
		leftScroll = new JScrollPane( generalControl );
		improveMouseWheelScroll( leftScroll,15);
		leftScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add( leftScroll, new GridBagConstraints(0,0,1,1,1.0,1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		generalControl.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				performGeneralSkillSelection();
			}});
	} 
	
	/**
	 * Respond to selections in the general skill window
	 */
	protected void performGeneralSkillSelection() {
		GeneralSkill skill = generalControl.getSelectedSkill();
		if ( skill==null ) {
			return;
		}
		selectedBias = dataModel.getGeneralSkillBiases().get( skill );
		
		if ( childControl==null ) {
			buildChildControl(selectedBias);
		} else {		
			childControl = new ChildSkillList(selectedBias);
			rightScroll.getViewport().setView( childControl );
			super.revalidate();
		}
		makeExclusiveGroupAction.setEnabled( selectedBias.getChildSkills().size()>0 );
		makeWeightedGroupAction.setEnabled( selectedBias.getChildSkills().size()>0 );
		revalidate();
	}
	
	private void improveMouseWheelScroll(JScrollPane pane, int increment ) {
		pane.getVerticalScrollBar().setUnitIncrement(
				pane.getVerticalScrollBar().getUnitIncrement(1) + increment);//experiment with the 10
	}
	
	/**
	 * @param bias
	 */
	private void buildChildControl(GeneralSkillBias bias) {
		rightPanel = new JPanel( new GridBagLayout() );
		buildToolBar();
		rightPanel.add( childActionBar, new GridBagConstraints(0,0,1,1,1.0,0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(2,2,2,2),0,0));
		childControl = new ChildSkillList(bias);
		rightScroll = new JScrollPane( childControl );
		improveMouseWheelScroll( rightScroll, 15);
		rightScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rightScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		rightPanel.add( rightScroll, new GridBagConstraints(0,1,1,1,1.0,1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE,2));
		add( rightPanel,new GridBagConstraints( 1,0, 1,1, 0,1.0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		rightPanel.setMinimumSize( new Dimension(270,800));
		
		super.revalidate();
		repaint();
	}
	
	/**
	 * 
	 */
	private void buildToolBar() {
		childActionBar = new JToolBar();
		childActionBar.setName("ChildActionBar");
		childActionBar.setFloatable(false);
		makeExclusiveButton = new JButton( makeExclusiveGroupAction );
		makeWeightedButton = new JButton( makeWeightedGroupAction );
		newSkillButton = new JButton( newSkillAction );
		makeExclusiveButton.setName("makeExclusiveButton");
		makeWeightedButton.setName("makeWeightedButton");
		childActionBar.add( makeExclusiveButton );
		childActionBar.add( makeWeightedButton );
		childActionBar.add( newSkillButton );
		
	}
	/**
	 * Builds an exclusive child group.
	 */
	private void makeExclusiveGroup() {
		String name = JOptionPane.showInputDialog(this,"Exclusive Group Name");
		if ( name==null ) {
			return;
		}
		selectedBias.createExclusiveGroup( name, new ArrayList<String>());
		childControl.refreshList();
	}
	
	/**
	 * 
	 */
	protected void makeWeightedGroup() {
		String name = JOptionPane.showInputDialog(this,"Weighted Group Name");
		if ( name==null ) {
			return;
		}
		selectedBias.createWeightingGroup(name,new ArrayList<String>() );
		childControl.refreshList();		
	}

	/**
	 * @param dataModel2
	 */
	public void setArchetype(Archetype dataModel2) {
		this.dataModel=dataModel2;
		generalBias =  new HashMap<GeneralSkill, Weighting>();
		for ( GeneralSkill key : dataModel.getGeneralSkillBiases().keySet() ) {
			generalBias.put( key, dataModel.getGeneralSkillBiases().get(key).getWeighting() );
		}
		generalControl.setDataModel( generalBias );
		if ( childControl!=null ) {
			buildChildControl(selectedBias);
		}
	}
	
	
}
