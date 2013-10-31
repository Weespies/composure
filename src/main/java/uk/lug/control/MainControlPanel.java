package uk.lug.control;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import uk.lug.gui.fight.RoundPanel;

public class MainControlPanel extends JPanel {
	private JTabbedPane tabPane;
	private PersonTablePanel personPanel;
	private RoundPanel fightPanel;

	public MainControlPanel() {
		super();
		buildUI();
	}

	private void buildUI() {
		tabPane = new JTabbedPane();
		setLayout(new BorderLayout());
		personPanel = new PersonTablePanel();
		fightPanel = new RoundPanel();
		add(tabPane, BorderLayout.CENTER);
		tabPane.addTab("People", personPanel);
		tabPane.addTab("Combat",fightPanel);
		personPanel.addFightListeners(fightPanel);
	}

}
