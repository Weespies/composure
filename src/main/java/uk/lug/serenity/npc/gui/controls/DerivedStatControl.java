/**
 * 
 */
package uk.lug.serenity.npc.gui.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.lug.serenity.npc.model.stats.DerivedStat;
import uk.lug.serenity.npc.model.stats.NamedStat;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Control for displaying a derived stat.</p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class DerivedStatControl extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 1L;
	private DerivedStat derivedStat ;
	private JLabel nameLabel;
	private JTextField valueField;
	private Font bold = this.getFont().deriveFont(Font.BOLD);
	private boolean showAsDice =true;
	
	
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

		if ( showAsDice ) {
			valueField.setText( derivedStat.getDice() );
		} else {
			valueField.setText( Integer.toString( derivedStat.getValue() ) );
		}
	}

	public DerivedStatControl(DerivedStat orgStat ) {
		createGUI();
		setDerivedStat( orgStat );
		orgStat.addChangeListener( this );
	}
	
	private void createGUI() {
		setLayout( new BorderLayout() );
				
		nameLabel = new JLabel("Name");
		valueField = new JTextField(7);		
		nameLabel.setName("name.label");
		valueField.setName("value.field");
		
		JPanel spinPanel = new JPanel( new BorderLayout() );
		spinPanel.add(valueField, BorderLayout.CENTER);
				
		add(spinPanel, BorderLayout.EAST);
		add( nameLabel, BorderLayout.CENTER );
		
		nameLabel.setBorder( new EmptyBorder(0,2,0,2));
		valueField.setHorizontalAlignment( SwingConstants.CENTER );
		valueField.setFont(bold);
		valueField.setEditable( false );
		
		Dimension nameSize = new Dimension( 72,nameLabel.getHeight() );
		nameLabel.setPreferredSize( nameSize );
	}
	
	/**
	 * @return Returns the namedStat.
	 */
	public DerivedStat getDerivedStat() {
		return derivedStat;
	}

	/**
	 * @param nStat The namedStat to populate this gui with
	 */
	public void setDerivedStat(DerivedStat nStat) {
		this.derivedStat = nStat;
		nameLabel.setText( derivedStat.getName() );
		if ( showAsDice ) {
			valueField.setText( derivedStat.getDice() );
		} else {
			valueField.setText( Integer.toString( derivedStat.getValue() ) );
		}
	}
	
	@Override
	public void setBackground(Color c) {
		super.setBackground( c);
		if ( valueField!=null ) {
			valueField.setBackground( c );
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent arg0) {
		nameLabel.setText( derivedStat.getName() );
		if ( showAsDice ) {
			valueField.setText( derivedStat.getDice() );
		} else {
			valueField.setText( Integer.toString( derivedStat.getValue() ) );
		}		
	}
}
