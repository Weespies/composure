/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.serenity.npc.managers.TraitsManager;
import uk.lug.serenity.npc.model.skills.Skill;
import uk.lug.serenity.npc.model.traits.Trait;
import uk.lug.serenity.npc.model.traits.TraitData;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Panel for displaying and editing trait data.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class TraitPanel extends JPanel {
	public static final String SWAP_BUTTON = "SwapButton";
	private static final long serialVersionUID = 1L;
	private Color COLOR_ASSET = Color.BLUE;
	private Color COLOR_COMPLICATION = Color.RED;
	
	private JLabel nameLabel;
	private TraitData data;
	private Trait orgTrait;
	private boolean isAsset;
	private JComboBox focusCombo;
	private JTextField focusField;
	private JButton swapButton;
	private Action swapAction=null;
	
	private Action swapActionSurrogate = new AbstractAction() {

		public void actionPerformed(ActionEvent e) {
			if ( swapAction!=null ) {
				e.setSource( data );
				swapAction.actionPerformed( e );
			}
		}};
	
	public TraitPanel(  ) {		
		createGUI();
	}

	/**
	 * 
	 */
	private void createGUI() {
		setLayout( new GridBagLayout() );
		nameLabel=new JLabel("Name");
		nameLabel.setHorizontalTextPosition( SwingConstants.RIGHT );
		focusCombo = new JComboBox();
		focusField = new JTextField(10);
		swapButton = new JButton( swapActionSurrogate ) ;
		swapButton.setMargin( new Insets(0,0,0,0));
		swapButton.setName(SWAP_BUTTON);
		add(swapButton, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 2, 0, 2), 0, 0));
		add(nameLabel, new GridBagConstraints(1, 0, 1, 1, 0.5, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 2, 0, 2), 0, 0));
		add(focusCombo,new GridBagConstraints(2, 0, 1, 1, 0.5, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 2, 0, 2), 0, 0));
		add(focusField, new GridBagConstraints(3, 0, 1, 1, 0.5, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 2, 0, 2), 0, 0));
		focusField.getDocument().addDocumentListener( new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				applyFocus();
			}

			public void removeUpdate(DocumentEvent e) {
				applyFocus();
			}

			public void changedUpdate(DocumentEvent e) {
				applyFocus();				
			}});
		focusCombo.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				applyFocus();
			}});
	}
	
	/**
	 * Apply focus to the trait.
	 */
	protected void applyFocus() {
		switch ( orgTrait.getFocusType() ) {
		case Trait.FOCUS_ANY : 
			data.setFocus( focusField.getText() );
			break;
		case Trait.FOCUS_LIST :
			String focus1 = (String)focusCombo.getSelectedItem();
			data.setFocus( focus1 );
			break;
		case Trait.SKILL_LIST:
			String focus2 = (String)focusCombo.getSelectedItem();
			data.setFocus( focus2 );
			break;
		case Trait.SKILL_ANY:
			String focus3 = (String)focusCombo.getSelectedItem();
			data.setFocus( focus3 );
			break;
		default:
			focusCombo.setVisible(false);
			focusField.setVisible(false);
		}
	}

	public void setTraitData(TraitData trait, boolean asAsset) {
		isAsset = asAsset;
		data = trait;
		orgTrait = TraitsManager.getTraitNamed( data.getName() );
		assert( orgTrait!=null );
		StringBuilder sb = new StringBuilder();
		sb.append( data.getName() );
		sb.append( " (");
		sb.append( ( data.isMajor() ? "Major" : "Minor" ) );
		sb.append( ")");
		nameLabel.setText( sb.toString() );
		nameLabel.setForeground( isAsset ? COLOR_ASSET : COLOR_COMPLICATION );
		swapButton.setIcon( trait.getType().getIcon() );
		
		switch ( orgTrait.getFocusType() ) {
		case Trait.FOCUS_ANY : 
			focusField.setVisible(true);
			focusField.setText( data.getFocus() );
			focusCombo.setVisible(false);
			break;
		case Trait.FOCUS_LIST :
			focusField.setVisible(false);
			focusField.setText("");
			focusCombo.setVisible(true);
			focusCombo.removeAllItems();
			for ( String s : orgTrait.getFocusList() ) {
				focusCombo.addItem(s);
			}
			if ( data.getFocus()!=null ) {
				focusCombo.setSelectedItem( data.getFocus() );
			}			
			break;
		case Trait.SKILL_LIST:
			focusField.setVisible(false);
			focusField.setText("");
			focusCombo.setVisible(true);
			focusCombo.removeAllItems();
			for ( String s : orgTrait.getSkillsList() ) {
				focusCombo.addItem(s);
			}
			if ( data.getSkill()!=null ) {
				focusCombo.setSelectedItem( data.getSkill() );
			}
			break;
		case Trait.SKILL_ANY:
			focusField.setVisible(false);
			focusField.setText("");
			focusCombo.setVisible(true);
			focusCombo.removeAllItems();
			for ( Skill s : SkillsManager.getSkills()) {
				focusCombo.addItem(s.getName() );
			}
			if ( data.getSkill()!=null ) {
				focusCombo.setSelectedItem( data.getSkill() );
			}
			break;
		default:
			focusCombo.setVisible(false);
			focusField.setVisible(false);
		}
		revalidate();
	}

	
	public static void main(String[] args) {
		try {
			JFrame win = new JFrame("Traits test window");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			TraitPanel tp = new TraitPanel();
			
			win.add(tp);
			win.setSize(640,50);
			win.setVisible(true);
			TraitData tdata = TraitData.createMinorTrait("Talented",true);
			tp.setTraitData( tdata, true );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @return the swapAction
	 */
	public Action getSwapAction() {
		return swapAction;
	}

	/**
	 * @param swapAction the swapAction to set
	 */
	public void setSwapAction(Action swapAction) {
		this.swapAction = swapAction;
	}
}
