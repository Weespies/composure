/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.lang.StringUtils;

import uk.lug.gui.util.CachedImageLoader;
import uk.lug.gui.util.ColorUtils;
import uk.lug.serenity.npc.gui.controls.ValuePanel;
import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.model.skills.SkillData;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Shows child skills , allows adjusting and addition of new skills.</p>
 * 
 * @author Luggy
 *
 */
public class ChildSkillsPanel extends JPanel implements SkillChangeListener, PropertyChangeListener { 
	public static final String ADD_NEW_SKILL_BUTTON = "addNewSkillButton";
	private static final long serialVersionUID = 1L;
	private static final Icon ADD_ICON = CachedImageLoader.getCachedIcon("images/add.png");
	private SkillData dataModel;
	private HashMap<String, ValuePanel> childPanels = null;
	private GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1,
			1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0);
	
	private JToolBar toolbar;
	private JButton addButton;
	private Font normal = super.getFont().deriveFont(Font.PLAIN);
	private Font bold = super.getFont().deriveFont(Font.BOLD);
	
	public static final String PANEL_NAME="childSkillPanel";
	
	private Action newChildAction = new AbstractAction("Add Skill", ADD_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent evt) {
			doNewChildSkill();
		}
	};
	private int lastAvailablePoints;
	
	
	/**
	 * Create a new SkillDataPanel . At this point we have no data.
	 */
	public ChildSkillsPanel() {
		
		createGUI();
	}

	/**
	 * @return the skillData
	 */
	public SkillData getSkillData() {
		return dataModel;
	}

	/**
	 * @param skillData the skillData to set
	 */
	public void setSkillData(SkillData skillData) {
		boolean replace = dataModel!=null && StringUtils.equals( dataModel.getName(),skillData.getName() );
		if ( dataModel!=null ) {
			dataModel.removeSkillSkillChangeListener( this );
		}
		dataModel = skillData;
		dataModel.addSkillChangeListener( this );
		if ( !replace ) {
			addChildPanels();
		} else {
			refreshChildPanels();
		}
	}

	/**
	 * Construct the user interface
	 */
	private void createGUI() {
		toolbar = new JToolBar();
		toolbar.setFloatable( false );
		setLayout( new GridBagLayout() );
		addButton = new JButton( newChildAction );
		addButton.setMargin( new Insets(0,0,0,0));
		addButton.setName( ADD_NEW_SKILL_BUTTON );
		toolbar.add( addButton );
		
		gbc.gridy=0;
		add(toolbar, gbc);
		
		add(new JLabel(""), new GridBagConstraints(0, 500, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		newChildAction.setEnabled( false );
	}
	
	/**
	 * Adds the childpanels.
	 */
	private void addChildPanels() {
		if ( childPanels!=null ) {
			//We already have panels, so clear them.
			String[] panelNames = childPanels.keySet().toArray( new String[0]);
			for ( String s : panelNames ) {
				ValuePanel vpanel = childPanels.get(s);
				remove( vpanel );
			}	
		} else {
			childPanels = new HashMap<String, ValuePanel>();
		}
		gbc.gridy=1;//IMPORTANT
		
		String[] names = dataModel.getChildren().toArray( new String[0] );
		Arrays.sort(names);
		for ( String name : names ) {
			int points = dataModel.getChildPoints(name);
			points = ( dataModel.getPoints()<6 ? 0 : points+6 );
			int max = dataModel.getMaxForChild(name,0);
			int min = dataModel.getMinForChild(name,0);
			ValuePanel vpanel = new ValuePanel( name, points,max,min,2);
			vpanel.setName( PANEL_NAME+"."+name );
			vpanel.addPropertyChangeListener( this );
			vpanel.setShowAsDice( true );
			add( vpanel, gbc );
			gbc.gridy++;
			childPanels.put( name, vpanel );
		}
		revalidate();
		repaintChildPanels();
	}

	/**
	 * Refresh the child panels, assuming that the skills have
	 * been reloaded and not a different skill selected.
	 */
	private void refreshChildPanels() {
		String[] names = dataModel.getChildren().toArray( new String[0] );
		for ( String name : names ) {
			int points = dataModel.getChildPoints(name);
			points = ( dataModel.getPoints()<6 ? 0 : points+6 );
			int max = dataModel.getMaxForChild(name, 0);
			int min = dataModel.getMinForChild(name,0);
			ValuePanel vpanel = childPanels.get(name);
			if( vpanel==null ) {
				throw new IllegalStateException("No child skills panel for "+name);
			}
			vpanel.setValues(max, min, points);
		}
		repaintChildPanels();
	}

	/**
	 * Repaint the font and background color of the child panels
	 */
	private void repaintChildPanels() {
		String[] names = dataModel.getChildren().toArray( new String[0] );
		for ( String name : names ) {
			ValuePanel vpanel = childPanels.get(name);
			if ( dataModel.getChildPoints(name)>0 ) {
				vpanel.setFont( bold );
				vpanel.setBackground( ColorUtils.lighten(super.getBackground(),0.1f) );
			} else {
				vpanel.setBackground( super.getBackground() );
				vpanel.setFont( normal );
			}
			vpanel.repaint();
		}
		resizeValues();
	}
	
	/**
	 * Resize the value panels value labels so that they're all the same
	 * size.
	 */
	private void resizeValues() {
		Graphics g= this.getGraphics();
		int width = 16;
		for ( ValuePanel panel : childPanels.values() ) {
			String txt=panel.getValueLabelText();
			if( txt==null ) {
				return;
			}
			
			FontMetrics fm = g.getFontMetrics( panel.getFont() );
			int w = fm.stringWidth( txt );
			if ( w>width ) {
				width=w;
			}
		}
		for ( ValuePanel panel : childPanels.values() ) {
			panel.resizeValueLabel( new Dimension(width,19));
		}
	}

	/* (non-Javadoc)
	 * @see lug.serenity.event.SkillChangeListener#SkillChanged(lug.serenity.event.SkillChangeEvent)
	 */
	public void SkillChanged(SkillChangeEvent evt) {
		if ( evt.getType()==SkillChangeEvent.CHILD_POINTS_CHANGED ) {
			repaintChildPanels();
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		int pts = ((Integer)evt.getNewValue()).intValue();
		if ( childPanels.containsKey( name ) ) {
			dataModel.setChildPoints( name, pts-6 );
		}
	}

	/**
	 * @param pts
	 */
	public void setPointsAvailable(int pts) {
		lastAvailablePoints = pts;
		if ( childPanels==null || childPanels.size()==0) {
			return;
		}
		
		String[] panelNames = childPanels.keySet().toArray( new String[0]);
		if ( dataModel.getPoints()==6 ) {		
			//Child points can be spent
			for ( String s : panelNames ) {
				if ( dataModel.containsChild( s ) ) {
					ValuePanel vpanel = childPanels.get(s);
					int val = dataModel.getChildPoints( s )+6;
					int max = dataModel.getMaxForChild( s , pts );
					int min = dataModel.getMinForChild( s , pts);
					vpanel.setValues( max, min, val );
				}
			}	
		} else {
			//Child points cannot be spent
			for ( String s : panelNames ) {
				ValuePanel vpanel = childPanels.get(s);
				vpanel.setMinValue(0);
				vpanel.setValue(0);
				vpanel.setMaxValue(0);
			}
		}
	}

	/**
	 * 
	 */
	public void blank() {
		String[] panelNames = childPanels.keySet().toArray( new String[0]);
		for ( String s : panelNames ) {
			ValuePanel vpanel = childPanels.get(s);
			remove(vpanel);
		}	
	}
	

	
	/**
	 * Add a new child ( at 0 points) to the current skill. 
	 */
	protected void doNewChildSkill() {
		String skillname = JOptionPane.showInputDialog(this, "New Speciality Skill Name");
		if ( StringUtils.isEmpty(skillname) ) {
			//cancelled.
			return;
		}
		String msg = "Are you sure you wish to create a new specialty skill called \n\""+skillname+"\" ? ";
		int ret = JOptionPane.showConfirmDialog( this, msg, "New skill ?" , JOptionPane.OK_CANCEL_OPTION);
		if ( ret == JOptionPane.CANCEL_OPTION ) {
			return;
		}
		getSkillData().addChild( skillname );
		addChildPanels();
		setPointsAvailable( lastAvailablePoints );
	}

	/**
	 * Enable or disable the new skill action. 
	 * @param enabled
	 */
	public void setNewSkillActionEnabled(boolean enabled) {
		newChildAction.setEnabled( enabled );
	}

}