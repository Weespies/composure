package uk.lug.control;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class MainControlPanel extends JPanel {
	
	private PersonTablePanel personPanel;

	public MainControlPanel() {
		super();
		buildUI();
	}

	private void buildUI() {
		setLayout( new BorderLayout() );
		personPanel = new PersonTablePanel();
		add(personPanel,BorderLayout.CENTER);
	}

}
