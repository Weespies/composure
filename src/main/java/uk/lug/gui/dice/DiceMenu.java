package uk.lug.gui.dice;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

import uk.lug.gui.IntegerListener;
import uk.lug.gui.util.CachedImageLoader;

public class DiceMenu extends JPopupMenu {
	private static final int ICON_SIZE = 48;
	private Action d4Action;
	private Action d6Action;
	private Action d8Action;
	private Action d10Action;
	private Action d12Action;
	private Action d20Action;
	private List<IntegerListener> listeners = new ArrayList<IntegerListener>();
	private static DiceIconMap iconMap = new DiceIconMap();

	public DiceMenu() {
		super();
		buildUI();
	}

	public void addIntegerListener(IntegerListener integerListener) {
		listeners.add(integerListener);
	}

	public void removeIntegerListener(IntegerListener integerListener) {
		listeners.remove(integerListener);
	}

	private void buildUI() {
		initActions();
		add(d4Action);
		add(d6Action);
		add(d8Action);
		add(d10Action);
		add(d12Action);
		add(d20Action);
	}

	private void initActions() {
		d4Action = buildAction(4);
		d6Action = buildAction(6);
		d8Action = buildAction(8);
		d10Action = buildAction(10);
		d12Action = buildAction(12);
		d20Action = buildAction(20);
	}

	public AbstractAction buildAction(final Integer sides) {
		Icon icon = iconMap.get(sides);
		return new AbstractAction("d" + Integer.toString(sides), icon) {

			public void actionPerformed(ActionEvent e) {
				fireSelected(sides);
			}
		};

	}

	protected void fireSelected(Integer sides) {
		for (IntegerListener il : listeners) {
			il.selected(sides);
		}
	}
}
