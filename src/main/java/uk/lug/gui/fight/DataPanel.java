package uk.lug.gui.fight;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.lug.MutableList;

public class DataPanel extends JPanel {
	private JList list;
	private JScrollPane scrollPane;
	private MutableList<String> listModel;
	
	public DataPanel() {
		super();
		buildUI();
	}

	private void buildUI() {
		listModel = new MutableList<String>();
		setLayout( new BorderLayout());
		list = new JList(listModel);
		scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);
	}

}
