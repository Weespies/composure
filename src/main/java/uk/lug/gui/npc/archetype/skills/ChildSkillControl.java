/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.lug.serenity.npc.gui.controls.JButtonRow;
import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkill;

/**
 * @author Luggy
 *
 */
public class ChildSkillControl extends JPanel {
	public static final String ROW_COMPONENT_NAME = "childBiasRow";
	private List<ChildSkillListItem> listItems;
	private GeneralSkill parentSkill;
	private DefaultListModel model;
	private JList childList;
	private List<JButtonRow> biasRows;
	private JPanel biasPanel;
	private int selected=-1;
	private Color unselectedBackground;
	
	
	
	/**
	 * @param parentSkill
	 */
	public ChildSkillControl(GeneralSkill parentSkill) {
		super();
		this.parentSkill = parentSkill;
		List<String> childSkillList = new ArrayList<String>( SkillsManager.getChildrenFor(parentSkill) );
		listItems = new ArrayList<ChildSkillListItem>();
		model = new DefaultListModel();
		for ( String str : childSkillList ) {
			WeightedChildSkill wcs = new WeightedChildSkill(str);
			listItems.add( new ChildSkillListItem( wcs ) );
			model.addElement( new ChildSkillListItem(wcs) );
		}
		build();
	}

	/**
	 * 
	 */
	private void build() {
		setLayout( new GridBagLayout() );
		childList = new JList(model);
		biasRows = new ArrayList<JButtonRow>();
		
		add( childList, new GridBagConstraints(0,0,1,1,0.5,1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0 ) );
		childList.setCellRenderer( new ChildSkillItemLabel() );
		childList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		biasPanel = new JPanel( new GridLayout(model.size(),1));
		unselectedBackground = UIManager.getLookAndFeelDefaults().getColor("List.background");
		for ( int i=0; i<model.size();i++) {
			final int idx = i;
			JButtonRow row = new JButtonRow(1,7);
			biasRows.add( row );
			biasPanel.add( row );
			row.setValue( listItems.get(i).getWeighting().getValue());
			row.setName( ROW_COMPONENT_NAME+(i));
			row.setBackground( unselectedBackground );
			row.addChangeListener( new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					doAdjustWeighting(idx);
				}});
		}
		
		add( biasPanel, new GridBagConstraints(1,0,1,1,0.5,1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,20),0,0 ) );

		childList.addListSelectionListener( new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				setListSelection();
			}});
		
	}
	
	/**
	 * @param idx
	 */
	protected void setListSelection() {
		int idx = childList.getSelectedIndex();
		if ( selected!=-1 ) {
			biasRows.get(selected).setBackground( childList.getBackground() );
			biasRows.get(selected).repaint();
		}
		selected = idx ;
		biasRows.get(selected).setBackground( childList.getSelectionBackground() );
		biasRows.get(selected).repaint();
	}

	/**
	 * @param idx
	 */
	protected void doAdjustWeighting(int idx) {
		int value = biasRows.get(idx).getValue();
		listItems.get(idx).getWeighting().setValue(value);
	}

	public GeneralSkill getParentSkill() {
		return parentSkill;
	}

	/**
	 * @return list of ChildSkillListItems
	 */
	public List<ChildSkillListItem> getListItems() {
		return listItems;
	}
}
