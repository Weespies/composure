/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.lug.gui.CachedImageLoader;
import uk.lug.gui.gridbag.GridBagException;
import uk.lug.gui.gridbag.GridBagLayoutXML;
import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.model.skills.SkillData;
import uk.lug.serenity.npc.model.stats.StepStat;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Show text and a value which can be brought up or down by two buttons.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class GeneralSkillPanel extends JPanel implements SkillChangeListener {
	public static final String PLUS_BUTTON = "plusButton";
	public static final String MINUS_BUTTON = "minusButton";
	private static final long serialVersionUID = 1L;
	private static final Icon PLUS_ICON = CachedImageLoader.getCachedIcon("images/plus_button.png");
	private static final Icon MINUS_ICON = CachedImageLoader.getCachedIcon("images/minus_button.png");
	private boolean showAsDice = true;
	private SkillData skillData;
	private boolean skillHasChildren=false;
	private JLabel textLabel;
	private JLabel valueLabel;
	private JButton plusButton;
	private JButton minusButton;
	private boolean allowRaised=true;
	private Color SELECTED_BACKGROUND = Color.BLACK;
	private Color UNSELECTED_BACKGROUND = super.getBackground();
	private Color SELECTED_FOREGROUND = Color.WHITE;
	private Color UNSELECTED_FOREGROUND = super.getForeground();
	private Font normalFont ;
	private Font boldFont;
	private boolean panelSelected=false;
	
	/**
	 * @return the panelSelected
	 */
	private boolean isPanelSelected() {
		return panelSelected;
	}



	/**
	 * @param panelSelected the panelSelected to set
	 */
	void setPanelSelected(boolean selected) {
		this.panelSelected = selected;
		setColors();
	}



	public GeneralSkillPanel( SkillData skillModel) {
		if ( skillModel==null ) {
			throw new IllegalArgumentException("SkillData cannot be null.");
		}
		skillData = skillModel;
		setOpaque(true);
		
		try {
			makeGUI();
		} catch (GridBagException e) {
			e.printStackTrace();
			System.exit(0);
		}

		checkActions();
		skillData.addSkillChangeListener( this );
	}
	

	
	/**
	 * Return the text to show in the left list for a general skill.
	 * @param data SkillData object.
	 */
	private String getPanelTitle() {
		StringBuilder sb = new StringBuilder(128);
		sb.append( skillData.getName() );
		skillHasChildren = skillData.countChildrenWithPoints()>0 ;
		if ( skillHasChildren ) { 
			sb.append(" (");
			sb.append( skillData.countChildrenWithPoints() );
			sb.append(")");
		}
		return sb.toString();
	}
	
	private Action plusAction = new AbstractAction("", PLUS_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			doPlus();
		}
	};
	
	private Action minusAction = new AbstractAction("", MINUS_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			doMinus();
		}
	};
	
	/**
	 * Reduce the skill points, if valid.
	 */
	protected void doMinus() {
		skillData.lowerPoints();
	}

	/**
	 * 
	 */
	protected void doPlus() {
		skillData.raisePoints();
	}

	/**
	 * Construct the user interface.
	 * @throws GridBagException 
	 *
	 */
	private void makeGUI() throws GridBagException {
		InputStream instream = this.getClass().getClassLoader()
		.getResourceAsStream("layout/ValuePanel.xml");
		
		GridBagLayoutXML gridbag = new GridBagLayoutXML(instream); 
		setLayout(gridbag);
		
		textLabel = new JLabel( getPanelTitle() );
		valueLabel = new JLabel( getValueText() );
		plusButton = new JButton( plusAction );
		minusButton = new JButton( minusAction );
		plusButton.setName( PLUS_BUTTON );
		minusButton.setName( MINUS_BUTTON );
		
		normalFont = textLabel.getFont().deriveFont(Font.PLAIN);
		boldFont = textLabel.getFont().deriveFont(Font.BOLD);
		textLabel.setFont( skillHasChildren ? boldFont : normalFont );
		
		add( textLabel, gridbag.getConstraints("text.label"));
		add( valueLabel, gridbag.getConstraints("value.label"));
		add( plusButton, gridbag.getConstraints("plus.button"));
		add( minusButton, gridbag.getConstraints("minus.button"));
		plusButton.setMargin( new Insets(0,0,0,0 ) );
		minusButton.setMargin( new Insets(0,0,0,0) );
		setColors();
		checkActions();
	}
	
	/**
	 * Refresh the data on the user interface. 
	 */
	void refreshGUI() {
		textLabel.setText( getPanelTitle() );
		textLabel.setFont( skillHasChildren ? boldFont : normalFont );
		valueLabel.setText( getValueText() );
		checkActions();
	}
	
	/**
	 * Enable or disable the actions as required
	 */
	private void checkActions() {
		plusAction.setEnabled( skillData.canRaisePoints() && allowRaised );
		minusAction.setEnabled( skillData.canLowerPoints() );
	}
	
	private void setColors() {
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				setForeground( isPanelSelected() ? SELECTED_FOREGROUND : UNSELECTED_FOREGROUND );
				setBackground( isPanelSelected() ? SELECTED_BACKGROUND : UNSELECTED_BACKGROUND );
				valueLabel.setForeground( isPanelSelected() ? SELECTED_FOREGROUND : UNSELECTED_FOREGROUND );
				repaint();		
			}
		});
		
	}

	/**
	 * @return
	 */
	private String getValueText() {
		if ( !showAsDice ) {
			return Integer.toString( skillData.getPoints() );
		}
		return StepStat.getDiceFor( skillData.getPoints() );
	}


	/**
	 * @return the showAsDice
	 */
	public boolean isShowAsDice() {
		return showAsDice;
	}

	/**
	 * @param showAsDice the showAsDice to set
	 */
	public void setShowAsDice(boolean showAsDice) {
		this.showAsDice = showAsDice;
		valueLabel.setText( getValueText() );
	}
	
	/**
	 * Change foreground color of the text label
	 */
	@Override
	public void setForeground( Color c ) {
		if ( textLabel!=null ) {
			textLabel.setForeground( c );
		}
	}
	
	/**
	 * Change the skill model being displayed.
	 * @param newData the skillData to set
	 */
	public void setSkillData(SkillData newData) {
		if ( this.skillData!=null ) {
			this.skillData.removeSkillSkillChangeListener( this );
		}
		this.skillData = newData;
		this.skillData.addSkillChangeListener( this );
		refreshGUI();
	}



	/* (non-Javadoc)
	 * @see lug.serenity.npc.model.event.SkillChangeListener#SkillChanged(lug.serenity.npc.model.event.SkillChangeEvent)
	 */
	public void SkillChanged(SkillChangeEvent evt) {
		refreshGUI();
	}



	/**
	 * Locks the raising of the general points in a skill.   This would be desirable, for example,
	 * there were no skills points available to spend.
	 * @param allowRaised true if the skills points can be raised if it is valid to do so.
	 */
	public void setAllowRaised(boolean allowRaised) {
		this.allowRaised = allowRaised;
		checkActions();
	}
	
	/**
	 * @return true if a stop has been put on the raise action (say, because there are not skill points left).
	 */
	public boolean isAllowRaised() {
		return isAllowRaised();
	}



	/**
	 * @return the skillData
	 */
	public SkillData getSkillData() {
		return skillData;
	}




}

