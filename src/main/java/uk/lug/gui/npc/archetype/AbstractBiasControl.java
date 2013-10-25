/**
 * 
 */
package uk.lug.gui.npc.archetype;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.lug.serenity.npc.random.archetype.Weighting;
import uk.lug.util.RandomFactory;

/**
 * @author Luggy
 * Abstract parent class of GUI's that control biasing information.
 */
public abstract class AbstractBiasControl<T> extends JPanel {
	public static final String LABEL_NAME_PREFIX = "label_";
	public static final String TESTFIELD_NAME_PREFIX = "averageField_";
	public static final String LEVEL_COMBO_NAME = "biasTestLevelCombo";
	private static final String[] DICE_STEPS = {"D2","D4", "D6", "D8", "D10", "D12", "D12+D2","D12+D4",
		"D12+D6","D12+D8","D12+D10","2D12"};
	protected static final int SLIDER_MAX = 7;
	protected static final int TEST_CYCLE_COUNT = 100;
	protected static final String[] TEST_LEVELS = { "GreenHorn", "Veteran",
				"Big Damn Hero" };
	protected static final int[] TEST_STAT_POINTS = {18,20,24};
	protected static final int[] TEST_SKILL_POINTS = {62,68,74};
	                          
	protected static final int[] TEST_STAT_MAXES = {12,14,16};
	
	protected Map<T, Weighting> dataModel;
	protected JComboBox levelCombo;
	protected Random random = RandomFactory.getRandom();
	protected boolean firstStatShow = true;
	protected int selectedIndex = -1;
	private JLabel testLabel;
	protected Map<T, BiasLabel> biasLabels;
	protected boolean valueIsAdjusting=false;
	private GridBagLayout gridbagLayout;

	/**
	 * @param statBiases
	 */
	public AbstractBiasControl(Map<T, Weighting> biases) {
		super();
		this.dataModel = biases;
		build();
	}


	public void setColorAtRow( int row, Color color ) {
		selectedIndex=row;
		repaint();
	}
	
	/**
	 * Build user interface
	 */
	private void build() {
		gridbagLayout = new GridBagLayout();
		setLayout( gridbagLayout );

		int y = 0;
		biasLabels = new HashMap<T, BiasLabel>( );
		for (int idx=0; idx<dataModel.size(); idx++ ) {
			T key = getKeyForIndex( idx );
			BiasLabel rowLabel = new BiasLabel( getLabelFor(key) , dataModel.get(key));
			add( rowLabel , new GridBagConstraints(0,y,2,1,1.0,0,GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(1,0,1,0),0,0));
			rowLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1) );
			rowLabel.addSliderListener( new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					generateAverageResults();
				}} );
			rowLabel.setName( "biasLabel_"+key.toString() );
			biasLabels.put( key, rowLabel );
			y++;
		}
		
		y=y+5;
		
		Font f = super.getFont().deriveFont( 10f);

		testLabel = new JLabel("Test at level");
		testLabel.setFont(f);
		add(testLabel, new GridBagConstraints(0,y,1,1,1.0,0,GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		levelCombo = new JComboBox(TEST_LEVELS);
		levelCombo.setName(LEVEL_COMBO_NAME);
		levelCombo.setEditable(false);
		levelCombo.setFont(f);
		add( levelCombo, new GridBagConstraints(1,y,1,1,1.0,0,GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		
		levelCombo.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				generateAverageResults();
			}});
		
		
		generateAverageResults();
	}


	/**
	 * This method is where you generate your average
	 * rolls for the test data. 
	 */
	protected abstract void generateAverageResults();


	/**
	 * Return a given key object for an index value
	 * @param idx
	 * @return
	 */
	protected abstract T getKeyForIndex(int idx);
	
	/**
	 * String a string label for a given key.
	 * @param key
	 * @return
	 */
	protected abstract String getLabelFor(T key);

	protected void showAverages(int[] stats) {
		for (int i = 0; i < stats.length; i++) {
			final String result = ( stats[i]==0 ? "" : DICE_STEPS[stats[i]] );
			final BiasLabel label = biasLabels.get( biasLabels.keySet().toArray()[i]);
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


	public int getSelectedIndex() {
		return selectedIndex;
	}


	/**
	 * @param point
	 * @return
	 */
	public int getLabelIndex(Point point) {
		Component c = getComponentAt(point);
		if ( c==null || !(c instanceof BiasLabel ) ) {
			return -1;
		}
		BiasLabel bl = (BiasLabel)c;
		int index=0;
		return gridbagLayout.getConstraints(bl).gridy;
	}

	private BiasLabel getLabelAtIndex( int i ){
		for ( BiasLabel biasLabel : biasLabels.values() ) {
			if ( gridbagLayout.getConstraints(biasLabel).gridy==i)  {
				return biasLabel;
			}
		}
		return null;
	}

	/**
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		if ( selectedIndex!=-1 ) {
			getLabelAtIndex( selectedIndex).setSelected(false);
			getLabelAtIndex( selectedIndex).repaint();
		}
		selectedIndex=index;
		getLabelAtIndex( selectedIndex).setSelected(true);
		getLabelAtIndex( selectedIndex).repaint();
	}
	
}
