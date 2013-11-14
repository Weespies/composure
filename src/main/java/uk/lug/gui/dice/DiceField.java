package uk.lug.gui.dice;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.lug.fight.Dice;
import uk.lug.gui.IntegerListener;
import uk.lug.util.SwingHelper;

public class DiceField extends JPanel implements IntegerListener {
	private static final int ICON_SIZE = 24;
	private List<Dice> diceList = new ArrayList<Dice>();
	private static DiceIconMap iconMap = new DiceIconMap();
	private List<DiceListener> diceListeners = new ArrayList<DiceListener>();
	
	public DiceField() {
		super();
		buildUI();
	}
	
	public void addDiceListener(DiceListener diceListener) {
		diceListeners.add(diceListener);
	}
	
	public void removeDiceListener(DiceListener diceListener) {
		diceListeners.remove(diceListener);
	}

	private void buildUI() {
		setLayout(new BorderLayout());
		setLayout(new FlowLayout(FlowLayout.LEADING));
		addMouseListener(new PopClickListener());
		setBorder(BorderFactory.createEtchedBorder());
	}

	class PopClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(MouseEvent e) {
			DiceMenu menu = new DiceMenu();
			menu.show(e.getComponent(), e.getX(), e.getY());
			menu.addIntegerListener(DiceField.this);
		}
	}

	public static void main(String[] args) {
		try {
			JFrame win = new JFrame("Test");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			DiceField df = new DiceField();
			win.getContentPane().add(df);
			win.setSize(320, 200);
			win.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selected(Integer value) {
		final Dice dice = Dice.getForValue(value);
		diceList.add(dice);
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				final DiceLabel label = new DiceLabel(dice);
				DiceField.this.add(label);
				DiceField.this.revalidate();
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) {
							removeDice(dice, label);
						}
						super.mouseClicked(e);
					}
				});
			}
		});
		fireListeners();
	}

	private void removeDice(final Dice dice, final DiceLabel label) {
		diceList.remove(dice);
		SwingHelper.runInEventThread(new Runnable(){

			public void run() {
				remove(label);
				repaint();
				revalidate();
			}});
		fireListeners();
		
	}

	private void fireListeners() {
		List<Dice> list = Collections.unmodifiableList(diceList);
		for(  DiceListener dl : diceListeners ) {
			dl.diceChanged(list);
		}		
	}
}