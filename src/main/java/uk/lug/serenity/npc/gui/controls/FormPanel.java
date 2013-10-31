package uk.lug.serenity.npc.gui.controls;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import uk.lug.util.SwingHelper;

public class FormPanel extends JPanel {
	private int nextRow = 0;
	private boolean finished=false;

	public FormPanel() {
		super();
		buildUI();
	}

	private void buildUI() {
		setLayout( new GridBagLayout() );
		nextRow=0;;
	}
	
	public void append(String title, Component c) {
		if ( finished ) {
			throw new IllegalStateException("Form builder has finished.");
		}
		JLabel label= new JLabel(title);
		SwingHelper.boldLabel(label);
		add( label, new GridBagConstraints(0,nextRow,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,SwingHelper.DEFAULT_INSETS,0,0));
		add( c, new GridBagConstraints(1,nextRow,1,1,1f,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,SwingHelper.DEFAULT_INSETS,0,0));
		nextRow++;
	}
	

	public void appendBlock(Component block) {
		add( block, new GridBagConstraints(0,nextRow,2,1,1f,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,SwingHelper.DEFAULT_INSETS,0,0));
		nextRow++;
	}


	public void appendSeparator() {
		if ( finished ) {
			throw new IllegalStateException("Form builder has finished.");
		}
		add( new JSeparator(SwingConstants.HORIZONTAL),new GridBagConstraints(0,nextRow,2,1,1f,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL, SwingHelper.DEFAULT_INSETS,0,0));
		nextRow++;
	}
	
	public void finish() {
		finished=true;
		add( new JPanel(), new GridBagConstraints(0,nextRow,2,1,1f,1f,GridBagConstraints.CENTER,GridBagConstraints.BOTH,SwingHelper.DEFAULT_INSETS,0,0));
		nextRow++;
	}

	public void subtitle(String string) {
		if ( finished ) {
			throw new IllegalStateException("Form builder has finished.");
		}
		JLabel label = new JLabel(string);
		SwingHelper.boldLabel(label);
		SwingHelper.changeFont(label, 1.5f);
		add( label,new GridBagConstraints(0,nextRow,2,1,1f,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL, SwingHelper.DEFAULT_INSETS,0,0));
		nextRow++;
	}

}
