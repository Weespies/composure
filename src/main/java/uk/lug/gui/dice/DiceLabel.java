package uk.lug.gui.dice;

import javax.swing.JLabel;

import uk.lug.fight.Dice;

public class DiceLabel extends JLabel {
	private Dice dice;
	private static DiceIconMap iconMap = new DiceIconMap();	
	
	public DiceLabel(Dice d) {
		this(d.getValue());
	}

	public DiceLabel(Integer value) {
		setIcon(iconMap.get(value));
	}
}
