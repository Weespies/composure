/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.lug.gui.npc.archetype.AbstractBiasControl;
import uk.lug.gui.npc.archetype.BiasLabel;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.serenity.npc.random.archetype.skills.GeneralSkillBias;
import uk.lug.util.RandomFactory;

/**
 * @author Luggy
 *
 */
public class GeneralSkillBiasControl extends AbstractBiasControl<GeneralSkill> {
	private static final int AVERAGE_COUNTING = 100;
	private List<ListSelectionListener> listeners;
	private GeneralSkill selectedSkill=null;
	
	public void setDataModel(HashMap<GeneralSkill,Weighting> dataModel) {
		this.dataModel = dataModel;
		for ( GeneralSkill gskill : GeneralSkill.values() ) {
			biasLabels.get(gskill).setWeighting( this.dataModel.get(gskill));
		}
		setLabelCodes();
	}
	
	/**
	 * @param biases
	 */
	public GeneralSkillBiasControl(HashMap<GeneralSkill, Weighting> biases) {
		super(biases);
		setLabelCodes();
		addMouseListener( new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				if ( e.getButton()==MouseEvent.BUTTON1) {
					performClickResponse(e.getPoint());
				}
			}
		});
	}

	/**
	 * 
	 */
	private void setLabelCodes() {
		for ( GeneralSkill gskill : GeneralSkill.values() ) {
			biasLabels.get(gskill).setDataCode( gskill.getCode() );
		}
	}

	/**
	 * Respond to a left click within this GUI.
	 * @param point
	 */
	protected void performClickResponse(Point point) {
		Component clickComponent = getComponentAt(point);
		if ( !(clickComponent instanceof BiasLabel) ) {
			return;
		}
		String skillCode = ((BiasLabel)clickComponent).getDataCode();
		int index = super.getLabelIndex( point );
		if ( index==-1 ) {
			return;
		}
		if ( index==selectedIndex ) {
			return;
		}
		if (selectedIndex!=-1) {			
			super.setColorAtRow(selectedIndex,super.getBackground()) ;
		}
		super.setSelectedIndex(index);
		
		selectedSkill = GeneralSkill.getForCode(skillCode);
		
		fireListeners();
	}

	/**
	 * 
	 */
	private void fireListeners() {
		ListSelectionEvent event = new ListSelectionEvent(this, selectedIndex, selectedIndex, false);
		if ( listeners!=null ) {
			for ( ListSelectionListener listener : listeners ) {
				listener.valueChanged( event );
			}
		}
	}
	

	private void applyDataModel( Archetype arche ) {
		for ( GeneralSkill gskill : dataModel.keySet() ) {
			if ( arche.getGeneralSkillBiases().containsKey(gskill) ) {
				arche.getGeneralSkillBiases().get(gskill).setWeighting( dataModel.get(gskill) ) ;
			} else {
				GeneralSkillBias bias = new GeneralSkillBias( gskill );
				bias.setWeighting( dataModel.get(gskill) );
				arche.getGeneralSkillBiases().put( gskill, bias );
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see lug.gui.archetype.AbstractBiasControl#generateAverageResults()
	 */
	@Override
	protected void generateAverageResults() {
		long t1 = System.currentTimeMillis();
		
		Archetype arche = new Archetype();
		applyDataModel( arche );
		int total = TEST_SKILL_POINTS[ levelCombo.getSelectedIndex() ];
		int[] values = new int[ GeneralSkill.length() ];
		for ( int i=0; i<values.length; i++ ) {
			values[i]=0;
		}
		for ( int i=0;i<AVERAGE_COUNTING;i++ ) {
			int[] cycleRet = getSkillPoints(arche, total);
			for ( int j=0; j<values.length; j++ ) {
				values[j]=values[j]+cycleRet[j];
			}
		}
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			int i = skill.getIndex();
			values[i]=Math.round( (values[i]/(AVERAGE_COUNTING)) );
			values[i]=2*((int)(values[i]/2));
		}
		
		showAverages( values);
	}
	
	/**
	 * Generate a single run of general skill values, based on current level selection and
	 * current bias settings.
	 * @param archetype
	 * @param count
	 * @return
	 */
	protected int[] getSkillPoints(Archetype archetype, int count) {
		int[] ret = new int[ GeneralSkill.length() ];
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			ret[ skill.getIndex() ]=0;
		}
		while ( count>0 ){
			GeneralSkill skill = archetype.getRandomGeneralSkill( RandomFactory.getRandom() );
			ret[skill.getIndex()] += 2;
			count=count-2;
		}
		return ret;
	}
	
	protected void showAverages(int[] skillz) {
		for (int i = 0; i < skillz.length; i++) {
			final String result = ( skillz[i]==0 ? "" : Integer.toString( skillz[i]) );
			final BiasLabel label = biasLabels.get( GeneralSkill.getForIndex(i) );
			if ( firstStatShow || SwingUtilities.isEventDispatchThread() ) {
				label.setTestText(result);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						label.setTestText(result);
					}
				});
			}
		}
		firstStatShow=false;
	}
	
	/* (non-Javadoc)
	 * @see lug.gui.archetype.AbstractBiasControl#getKeyForIndex(int)
	 */
	@Override
	protected GeneralSkill getKeyForIndex(int idx) {
		return GeneralSkill.getForIndex(idx);
	}

	/* (non-Javadoc)
	 * @see lug.gui.archetype.AbstractBiasControl#getLabelFor(java.lang.Object)
	 */
	@Override
	protected String getLabelFor(GeneralSkill key) {
		return key.getName();
	}

	
	public void addListSelectionListener( ListSelectionListener listener) {
		if ( listeners==null ) {
			listeners = new ArrayList<ListSelectionListener>();
		}
		listeners.add( listener );
	}
	
	public void removeListSelectionListener( ListSelectionListener listener ) {
		if ( listeners==null ) {
			return;
		}
		listeners.remove( listener );
	}

	/**
	 * @return the selectedSkill
	 */
	public GeneralSkill getSelectedSkill() {
		return selectedSkill;
	}
}
