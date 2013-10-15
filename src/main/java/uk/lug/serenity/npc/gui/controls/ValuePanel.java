/**
 * 
 */
package uk.lug.serenity.npc.gui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import uk.lug.gui.CachedImageLoader;
import uk.lug.gui.gridbag.GridBagException;
import uk.lug.gui.gridbag.GridBagLayoutXML;
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
public class ValuePanel extends JPanel {
	public static final String MINUS_BUTTON = "valuePanelMinusButton";
	public static final String PLUS_BUTTON = "valuePanelPlusButton";
	private static final long serialVersionUID = 1L;
	private static final Icon PLUS_ICON = CachedImageLoader.getCachedIcon("images/plus_button.png");
	private static final Icon MINUS_ICON = CachedImageLoader.getCachedIcon("images/minus_button.png");
	
	private boolean showAsDice = false;
	private String text="SomeText";
	private int value=4;
	private int maxValue = 8;
	private int minValue = 2;
	private int valueStep = 2;
	
	private JLabel textLabel;
	private JLabel valueLabel;
	private JButton plusButton;
	private JButton minusButton;
	
	private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	
	public void setValues( int max, int min, int val ) {
		maxValue = max;
		minValue = min;
		value = val;
		valueLabel.setText( getValueText() );
		checkActions();
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
	 * Create the value panel
	 * @param text text to display
	 * @param value initial value
	 * @param maxValue maximum value
	 * @param minValue minimum value
	 * @param valueStep step size for buttons for changing the value.
	 */
	public ValuePanel(String text, int value, int maxValue, int minValue, int valueStep) {
		super();
		if ( maxValue<minValue ) {
			StringBuilder sb = new StringBuilder(128);
			sb.append("Max value is less than minimum.");
			sb.append("[MAX=");
			sb.append(maxValue);
			sb.append(" , VALUE=");
			sb.append(value );
			sb.append(" , MINVALUE = ");
			sb.append(minValue);
			sb.append("]");
			throw new IllegalArgumentException( sb.toString() );
		}
		this.text = text;
		this.value = value;
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.valueStep = valueStep;
		try {
			makeGUI();
		} catch (GridBagException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * 
	 */
	protected void doMinus() {
		int oldvalue = value;
		value = value - valueStep;
		valueLabel.setText( getValueText() );
		checkActions();
		propertySupport.firePropertyChange(text, oldvalue, value);
	}

	/**
	 * 
	 */
	protected void doPlus() {
		int oldvalue = value;
		value = value + valueStep;
		valueLabel.setText( getValueText() );
		checkActions();
		propertySupport.firePropertyChange(text, oldvalue, value);
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
		
		textLabel = new JLabel( text );
		
		valueLabel = new JLabel( getValueText() );
		valueLabel.setHorizontalAlignment( SwingConstants.CENTER );
		plusButton = new JButton( plusAction );
		minusButton = new JButton( minusAction );
		plusButton.setName( PLUS_BUTTON );
		minusButton.setName( MINUS_BUTTON );
		
		add( textLabel, gridbag.getConstraints("text.label"));
		add( valueLabel, gridbag.getConstraints("value.label"));
		add( plusButton, gridbag.getConstraints("plus.button"));
		add( minusButton, gridbag.getConstraints("minus.button"));
		plusButton.setMargin( new Insets(0,0,0,0 ) );
		minusButton.setMargin( new Insets(0,0,0,0) );
		
		checkActions();
	}
	
	/**
	 * Enable or disable the actions as required
	 */
	private void checkActions() {
		plusAction.setEnabled( canIncreaseValue() ) ;
		minusAction.setEnabled( canDecreaseValue() ) ;
	}
	
	/**
	 * @return true if the plus action is safe to invoke without
	 * violating the maximum constraint.
	 */
	private boolean canIncreaseValue() {
		return ( value+valueStep<=maxValue );
	}
	
	/**
	 * @return true if the plus action is safe to invoke without
	 * violating the minimum constraint.
	 */
	private boolean canDecreaseValue() {
		return ( value-valueStep>=minValue );
	}

	/**
	 * @return
	 */
	private String getValueText() {
		if ( !showAsDice ) {
			return Integer.toString(value);
		}
		return StepStat.getDiceFor( value );
	}

	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		checkActions();
	}

	/**
	 * @return the minValue
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
		checkActions();
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
		textLabel.setText( text );
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int v) {
		if ( v<minValue ) {
			StringBuilder sb= new StringBuilder(128);
			sb.append("Value is less than minimum.");
			sb.append(" [ MAX=" );
			sb.append( maxValue );
			sb.append(" , MIN=");
			sb.append( minValue );
			sb.append(" , VALUE=");
			sb.append(value);
			sb.append(" , NEWVALUE=");
			sb.append(v);
			sb.append(" ]");
			throw new IllegalArgumentException(sb.toString());
		}
		if ( v>maxValue ) {
			StringBuilder sb= new StringBuilder(128);
			sb.append("Value is exceeds maxmium.");
			sb.append(" [ MAX=" );
			sb.append( maxValue );
			sb.append(" , MIN=");
			sb.append( minValue );
			sb.append(" , VALUE=");
			sb.append(value);
			sb.append(" , NEWVALUE=");
			sb.append(v);
			sb.append(" ]");
			throw new IllegalArgumentException(sb.toString());
		}
		value = v;
		valueLabel.setText( getValueText() );
		checkActions();
	}

	/**
	 * @return the valueStep
	 */
	public int getValueStep() {
		return valueStep;
	}

	/**
	 * @param valueStep the valueStep to set
	 */
	public void setValueStep(int valueStep) {
		this.valueStep = valueStep;
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
	 * Adds a listener to be notified of property changes to this person.
	 * @param pcl
	 */
	@Override
	public void addPropertyChangeListener( PropertyChangeListener pcl ) {
		propertySupport.addPropertyChangeListener( pcl ) ;
	}
	
	/**
	 * remove a listener from property change notifications.
	 * @param pcl
	 */
	@Override
	public void removePropertyChangeListener( PropertyChangeListener pcl ) {
		propertySupport.removePropertyChangeListener( pcl );
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
	 * @return the textLabel
	 */
	public JLabel getTextLabel() {
		return textLabel;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if ( textLabel!=null ) {
			textLabel.setFont(font);
		}
	}

	/**
	 * @return current size of the value label.
	 */
	public String getValueLabelText() {
		return (valueLabel==null ? "" : valueLabel.getText());
	}
	
	public void resizeValueLabel(Dimension d) {
		valueLabel.setSize( d );
		valueLabel.setPreferredSize( d );
		valueLabel.setMaximumSize( d );
		valueLabel.setMinimumSize( d );
		revalidate();
		repaint();
		
		
	}
}
