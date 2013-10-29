package uk.lug.gui;

import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolbarPanel extends JPanel {

	public ToolbarPanel() {
		super(new WrapLayout(FlowLayout.LEFT));
	}

	public JButton addActionButton(Action action ) { 
		JButton button = new JButton(action);
		add( button ) ;
		return button;
	}

}
