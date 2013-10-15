/**
 * 
 */
package uk.lug.gui.archetype;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import uk.lug.gui.ColorUtils;
import uk.lug.serenity.npc.gui.controls.WeightingSlider;
import uk.lug.serenity.npc.random.archetype.Weighting;

/**
 * @author Luggy
 * Bias control component, consisting of a name string, a bias slider 
 * and an example text field (for demoing typical results).
 */
public class BiasLabel extends JPanel {
	private JLabel nameLabel;
	private WeightingSlider slider;
	private JTextField testField;
	public static final Color UNSELECTED_GRADIENT1 = new Color( 0xc8def2 );
	public static final Color UNSELECTED_GRADIENT2 = ColorUtils.darken( UNSELECTED_GRADIENT1, 0.15f);

	public static final Color SELECTED_GRADIENT1 = new Color( 0x4e94d6 );
	public static final Color SELECTED_GRADIENT2 = ColorUtils.lighten( SELECTED_GRADIENT1, 0.15f);
	
	private boolean selected=false;
	private String text;
	private Weighting weighting;
	private String dataCode;
	
		/**
	 * @return the dataCode
	 */
	public String getDataCode() {
		return dataCode;
	}


	/**
	 * @param dataCode the dataCode to set
	 */
	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}


	/**
	 * @param text
	 * @param weighting
	 */
	public BiasLabel(String text, Weighting weighting) {
		super();
		this.text = text;
		this.weighting = weighting;
		build();
	}
	
	
	private void build() {
		setOpaque( true );
		setLayout( new GridBagLayout() );
		nameLabel = new JLabel(text);
		slider = new WeightingSlider(weighting);
		slider.setOpaque(false);
		testField = new JTextField(5);
		testField.setHorizontalAlignment(JTextField.CENTER);
		testField.setEditable(false);
		nameLabel.setFont( super.getFont().deriveFont(Font.BOLD));
		add( nameLabel, new GridBagConstraints(0,0, 1,1, 1.0,0, GridBagConstraints.WEST, 
				GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2),0,0));
		add( slider, new GridBagConstraints(0,1, 1,1, 0,0, GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(2,2,2,2),0,0));
		add( testField, new GridBagConstraints(1,0, 1,2, 0,0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(2,2,2,2),0,0));

		slider.setName("weightingSlider");
		testField.setName("testField");
		nameLabel.setName("nameLabel");
	}
	
	public void addSliderListener( ChangeListener listener ) {
		slider.addChangeListener( listener );
	}
	
	public void removeSliderListener( ChangeListener listener ) {
		slider.removeChangeListener( listener );
	}
	
	public void setTestText(String str) {
		testField.setText(str);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics gtemp = g.create();
		Graphics2D g2 = (Graphics2D)gtemp;
		Rectangle bounds = new Rectangle(0,0, getWidth(), getHeight());
		
		GradientPaint gradient = (selected ? 
			new GradientPaint( new Point(bounds.x,bounds.y), SELECTED_GRADIENT1, new Point(bounds.x,bounds.y+bounds.height), SELECTED_GRADIENT2 ) 
			: new GradientPaint( new Point(bounds.x,bounds.y), UNSELECTED_GRADIENT1, new Point(bounds.x,bounds.y+bounds.height), UNSELECTED_GRADIENT2 ) );
		g2.setPaint( gradient );
		g2.fill( bounds );
		gtemp.dispose();
		
	}


	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}


	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setWeightingValue( int v) {
		slider.setValue( v );
		slider.repaint();
	}


	/**
	 * @param weighting2
	 */
	public void setWeighting(Weighting weighting2) {
		slider.setWeighting(weighting2);
	}


}

