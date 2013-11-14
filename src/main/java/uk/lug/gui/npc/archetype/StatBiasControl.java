/**6
 * 
 */
package uk.lug.gui.npc.archetype;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.model.stats.StepStat;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.util.RandomFactory;

/**
 * @author Luggy
 *
 */
public class StatBiasControl extends AbstractBiasControl<MainStat> {
	private static final int AVERAGE_COUNTING = 100;
	
	/**
	 * @param biases
	 */
	public StatBiasControl(HashMap<MainStat, Weighting> biases) {
		super(biases);
	}


	/* (non-Javadoc)
	 * @see lug.gui.archetype.AbstractBiasControl#generateAverageResults()
	 */
	@Override
	protected void generateAverageResults() {
		Archetype arche = new Archetype();
		for ( MainStat stat : MainStat.values() ) {
			arche.getStatBiases().put(stat, dataModel.get(stat) );
		}
		int pointCount = (TEST_STAT_POINTS[levelCombo.getSelectedIndex()]);
		
		int[] values = new int[ MainStat.values().length ];
		for ( int i=0; i<values.length; i++ ) {
			values[i]=4;
		}
		for ( int i=0;i<AVERAGE_COUNTING;i++ ) {
			int[] cycleRet = getStatPoints(arche, pointCount);
			for ( int j=0; j<values.length; j++ ) {
				values[j]=values[j]+cycleRet[j];
			}
		}
		for ( MainStat stat : MainStat.values() ) {
			int i = stat.getIndex();
			values[i]=Math.round( (values[i]/(AVERAGE_COUNTING)) );
			values[i]=2*((int)(values[i]/2));
		}
		showAverages( values);
	}

	/**
	 * Generate a single run of stat values, based on current level selection and
	 * current bias settings.
	 * @param archetype
	 * @param count
	 * @return
	 */
	protected int[] getStatPoints(Archetype archetype, int count) {
		int[] ret = new int[ MainStat.values().length ];
		for ( MainStat stat : MainStat.values() ) {
			ret[ stat.getIndex() ]=4;
		}
		int max = TEST_STAT_MAXES[levelCombo.getSelectedIndex()];
		while ( count>0 ){
			MainStat stat = archetype.getRandomStat( RandomFactory.getRandom() );
			if ( ret[stat.getIndex()]<max ) {
				ret[stat.getIndex()] += 2;
				count=count-2;
			}
		}
		return ret;
	}
	
	/**
	 * @return
	 */
	private int keyCount() {
		return MainStat.values().length;
	}
	
	private MainStat getRandomStat() {
		
		for ( MainStat stat : dataModel.keySet() ) {
			
		}
		return null;
	}
	
	protected void showAverages(int[] stats) {
		for (int i = 0; i < stats.length; i++) {
			final String result = ( stats[i]==0 ? "" : StepStat.getDiceFor( stats[i] ) );
			final BiasLabel label = biasLabels.get( MainStat.values()[i] );
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
	protected MainStat getKeyForIndex(int idx) {
		return MainStat.getForIndex(idx);
	}

	/* (non-Javadoc)
	 * @see lug.gui.archetype.AbstractBiasControl#getLabelFor(java.lang.Object)
	 */
	@Override
	protected String getLabelFor(MainStat key) {
		return key.getName();
	}


	public int getSelectedIndex() {
		return selectedIndex;
	}


	/**
	 * @param dataModel
	 */
	public void setArchetype(Archetype dataModel) {
		this.dataModel = dataModel.getStatBiases();
		for ( MainStat stat : MainStat.values() ) {
			biasLabels.get(stat).setWeighting( this.dataModel.get(stat) );

		}
		
	}

}
