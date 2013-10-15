/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.gui.controls.ObjectSelectorDialog;
import uk.lug.serenity.npc.managers.ArchetypesManager;
import uk.lug.serenity.npc.managers.TraitsManager;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.traits.Trait;
import uk.lug.serenity.npc.model.traits.TraitData;
import uk.lug.serenity.npc.model.traits.TraitType;
import uk.lug.serenity.npc.random.Generator;
import uk.lug.util.RandomFactory;

/**
 * $Id$
 * 
 * @version $Revision$
 * @author $Author$
 * <p>
 * Root panel for all Trait controls, 
 * holding the TraitListPanels for assets and complications.
 * </p>
 */
/**
 * @author Luggy
 */
public class TraitControlPanel extends JPanel implements PropertyChangeListener, DataModelListener<Person> {
	public static final String ADD_ASSET_BUTTON = "newAssetButton";
	public static final String ADD_COMPLICATION_BUTTON = "newComplicationButton";
	public static final String ASSETS_PANEL = "assetsPanel";
	public static final String COMPLICATIONS_PANEL = "complicationsPanel";
	private static final long serialVersionUID = 1L;
	private Color COLOR_ASSET = Color.BLUE;
	private Color COLOR_COMPLICATION = Color.RED;
	private Icon ICON_ASSET = TraitType.ASSET.getIcon();
	private Icon ICON_COMPLICATION = TraitType.COMPLICATION.getIcon();
	private TraitListPanel assetListPanel;
	private TraitListPanel complicationsPanel;
	private JPanel traitPanel;
	private JButton newAssetButton;
	private JButton newComplicationButton;
	private DataModel<Person> model;
	private Person dataModel;
	private ObjectSelectorDialog selectorDialog;
	
	private final Action newAssetAction = new AbstractAction
	("Add Asset", ICON_ASSET) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doNewAsset();
		}
	};

	private final Action newComplicationAction = new AbstractAction
	("Add Complication", ICON_COMPLICATION) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			doNewComplication();
		}
	};

	/**
	 * @return the dataModel
	 */
	public Person getPerson() {
		return dataModel;
	}

	/**
	 * Call popup box to ask for asset to add.
	 */
	protected void doNewAsset() {
		TraitData[] choices = TraitsManager.getChoosableAssets(dataModel);
		selectorDialog = new ObjectSelectorDialog(choices,
				choices[0], true);
		Component parent = this.getParent();
		while (parent != null && !(parent instanceof Frame)) {
			parent = parent.getParent();
		}
		Frame frame = (parent != null ? null : (Frame) parent);
		if (selectorDialog.showDialog(frame, "Choose Asset")) {
			TraitData traitData = (TraitData)selectorDialog.getSelection();
			Trait trait = TraitsManager.getAssetNamed( traitData.getName() );
			
			// Focus
			String focus = null;
			Random random = RandomFactory.getRandom();
			if (trait.getFocusType() == Trait.FOCUS_LIST) {
				focus = trait.getFocusList()[random.nextInt(trait
						.getFocusList().length)];
			}
			if (trait.getFocusType() == Trait.SKILL_LIST) {
				focus = trait.getSkillsList()[random.nextInt(trait
						.getSkillsList().length)];
			}

			if (focus != null) {
				traitData.setFocus(focus);
			}

			dataModel.getAssets().add(traitData);
			assetListPanel.setPerson(dataModel);
			assetListPanel.revalidate();
			assetListPanel.repaint();
			
			dataModel.setCurrentPoints(dataModel.getCurrentStatPoints()
					- (traitData.isMajor() ? 4 : 2));
		}
	}

	/**
	 * Call popup box to ask for asset to add.
	 */
	protected void doNewComplication() {
		TraitData[] choices = TraitsManager.getChoosableComplications(dataModel);
		selectorDialog = new ObjectSelectorDialog(choices,
				choices[0], true);
		Component parent = this.getParent();
		while (parent != null && !(parent instanceof Frame)) {
			parent = parent.getParent();
		}
		Frame frame = (parent != null ? null : (Frame) parent);
		if (selectorDialog.showDialog(frame, "Choose Complication")) {
			TraitData traitData = (TraitData)selectorDialog.getSelection();
			Trait trait = TraitsManager.getTraitNamed( traitData.getTraitName() );
			
			// Focus
			String focus = null;
			Random random = RandomFactory.getRandom();
			if (trait.getFocusType() == Trait.FOCUS_LIST) {
				focus = trait.getFocusList()[random.nextInt(trait
						.getFocusList().length)];
			}
			if (trait.getFocusType() == Trait.SKILL_LIST) {
				focus = trait.getSkillsList()[random.nextInt(trait
						.getSkillsList().length)];
			}

			TraitData tdata = new TraitData();
			tdata.setType( trait.getType() );
			tdata.setName(trait.getName());
			tdata.setMajor(traitData.isMajor());
			if (focus != null) {
				tdata.setFocus(focus);
			}

			dataModel.getComplications().add(tdata);
			dataModel.setCurrentPoints(dataModel.getCurrentStatPoints()
					+ (traitData.isMajor()? 4 : 2));
			
		}
	}

	/**
	 * @param dataModel
	 *            the dataModel to set
	 */
	public void setPerson(Person dataModel) {
		if (this.dataModel != null) {
			dataModel.removePropertyChangeListener(this);
		}
		this.dataModel = dataModel;
		this.dataModel.addPropertyChangeListener(this);
		assetListPanel.setPerson(dataModel);
		complicationsPanel.setPerson(dataModel);
		assetListPanel.repaint();
		complicationsPanel.repaint();
		enableAddActions();
	}
	
	public void enableAddActions() {
		newAssetAction.setEnabled( model.getData().canAddNewAsset() );
		newComplicationAction.setEnabled( model.getData().canAddNewComplication() );
	}

	

	/**
	 * Set the trait control panel gui up.
	 */
	public TraitControlPanel(DataModel<Person> dataModel) {
		model=dataModel;
		model.addDataModelListener( this );		
		
		// Trait list panels
		traitPanel = new JPanel();
		traitPanel.setLayout( new BoxLayout( traitPanel, BoxLayout.Y_AXIS ));
		assetListPanel = new TraitListPanel(true);
		assetListPanel.setName(ASSETS_PANEL);
		assetListPanel.addSwapListener( new SwapListener<TraitData>() {

			public void swapFor(TraitData oldObject) {
				doSwapAsset( oldObject );
			}});
		complicationsPanel = new TraitListPanel(false);
		complicationsPanel.setName(COMPLICATIONS_PANEL);
		complicationsPanel.addSwapListener( new SwapListener<TraitData>() {

			public void swapFor(TraitData oldObject) {
				doSwapComplication( oldObject );
			}});
		
		LineBorder lb1 = new LineBorder(Color.BLUE, 1);
		LineBorder lb2 = new LineBorder(Color.RED, 1);
		TitledBorder tb1 = new TitledBorder(lb1, "Assets");
		TitledBorder tb2 = new TitledBorder(lb2, "Complications");
		tb1.setTitleFont(this.getFont().deriveFont(
				this.getFont().getSize2D() * 1.25f).deriveFont(Font.BOLD));
		tb2.setTitleFont(this.getFont().deriveFont(
				this.getFont().getSize2D() * 1.25f).deriveFont(Font.BOLD));
		tb1.setTitleColor(COLOR_ASSET);
		tb2.setTitleColor(COLOR_COMPLICATION);
		assetListPanel.setBorder(tb1);
		complicationsPanel.setBorder(tb2);

		traitPanel.add( assetListPanel );
		traitPanel.add( complicationsPanel );
		setLayout(new GridBagLayout());
		add(traitPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));

		// Buttons
		newAssetButton = new JButton(newAssetAction);
		newAssetButton.setName( ADD_ASSET_BUTTON );
		newComplicationButton = new JButton(newComplicationAction);
		newComplicationButton.setName( ADD_COMPLICATION_BUTTON );
		add(newAssetButton, new GridBagConstraints(0, 0, 1, 1, 0.5, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		add(newComplicationButton, new GridBagConstraints(1, 0, 1, 1, 0.5, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));

	}

	/**
	 * @param oldTrait
	 */
	protected void doSwapComplication(TraitData oldTrait) {
		TraitData[] choices = TraitsManager.getSwappableComplications(dataModel, oldTrait );
		selectorDialog = new ObjectSelectorDialog(choices,
				choices[0], true);
		Component parent = this.getParent();
		while (parent != null && !(parent instanceof Frame)) {
			parent = parent.getParent();
		}
		Frame frame = (parent != null ? null : (Frame) parent);
		if (selectorDialog.showDialog(frame, "Swap Complication")) {
			TraitData newTrait = (TraitData)selectorDialog.getSelection();
			Trait trait = TraitsManager.getComplicationNamed( newTrait.getName() );
			
			// Focus
			String focus = null;
			Random random = RandomFactory.getRandom();
			if (trait.getFocusType() == Trait.FOCUS_LIST) {
				focus = trait.getFocusList()[random.nextInt(trait
						.getFocusList().length)];
			}
			if (trait.getFocusType() == Trait.SKILL_LIST) {
				focus = trait.getSkillsList()[random.nextInt(trait
						.getSkillsList().length)];
			}

			if (focus != null) {
				newTrait.setFocus(focus);
			}

			dataModel.getComplications().replace( oldTrait, newTrait );
			assetListPanel.setPerson(dataModel);
			assetListPanel.revalidate();
			assetListPanel.repaint();
			
			
			int costDifference = -oldTrait.getCost()+newTrait.getCost();
			
			dataModel.setCurrentPoints(dataModel.getCurrentStatPoints()
					+ costDifference);
		}
	}

	/**
	 * Being up a dialog to swap 1 trait for another.
	 * @param oldTrait
	 */
	protected void doSwapAsset(TraitData oldTrait) {
		TraitData[] choices = TraitsManager.getSwappableAssets(dataModel, oldTrait );
		selectorDialog = new ObjectSelectorDialog(choices,
				choices[0], true);
		Component parent = this.getParent();
		while (parent != null && !(parent instanceof Frame)) {
			parent = parent.getParent();
		}
		Frame frame = (parent != null ? null : (Frame) parent);
		if (selectorDialog.showDialog(frame, "Swap Asset")) {
			TraitData newTrait = (TraitData)selectorDialog.getSelection();
			Trait trait = TraitsManager.getAssetNamed( newTrait.getName() );
			
			// Focus
			String focus = null;
			Random random = RandomFactory.getRandom();
			if (trait.getFocusType() == Trait.FOCUS_LIST) {
				focus = trait.getFocusList()[random.nextInt(trait
						.getFocusList().length)];
			}
			if (trait.getFocusType() == Trait.SKILL_LIST) {
				focus = trait.getSkillsList()[random.nextInt(trait
						.getSkillsList().length)];
			}

			if (focus != null) {
				newTrait.setFocus(focus);
			}

			dataModel.getAssets().replace( oldTrait, newTrait );
			assetListPanel.setPerson(dataModel);
			assetListPanel.revalidate();
			assetListPanel.repaint();
			
			
			int costDifference = oldTrait.getCost()-newTrait.getCost();
			
			dataModel.setCurrentPoints(dataModel.getCurrentStatPoints()
					+ costDifference);
		}
	}

	/**
	 * @param newData
	 */
	protected void doPersonChanged(@SuppressWarnings("unused")
			Person newData) {
		
	}

	public static void main(String[] args) {
		try {
			JFrame win = new JFrame("Testing Traits Control");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			DataModel<Person> model = new DataModel<Person>(); 
			TraitControlPanel tcp = new TraitControlPanel( model );
			win.add(tcp);
			win.pack();
			win.setVisible(true);
			
			for ( int i=0;i<10;i++ ) {
				Person p = new Person();
				do {
					p.clearExceptName();
					Generator gen = new Generator(p, ArchetypesManager.getRandom());
					gen.randomizeTraits();
					gen.randomizeStats();
					gen.randomizeSkills();
					p.setCurrentPoints(2);
				} while (p.getAssets().size() < 2);
				model.setData( p );
				win.pack();
				Thread.sleep(100);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		if ( arg0.getPropertyName().equals( Person.PROPERTY_STAT_POINTS ) ) {
			enableAddActions();
		}		
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		setPerson( newData );
	}

	/**
	 * @return the osd
	 */
	public ObjectSelectorDialog getSelectorDialog() {
		return selectorDialog;
	}
}
