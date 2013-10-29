package uk.lug.serenity.npc.gui.generator;

import java.awt.Component;

import javax.swing.JDialog;

import uk.lug.dao.records.PersonRecord;
import uk.lug.util.SwingHelper;

public class GeneratorDialog extends JDialog implements GeneratorListener {
	private GeneratorPanel generatorPanel;
	private boolean ok = false;
	private PersonRecord personRecord;

	public GeneratorDialog(Component owner, String title) {
		super(SwingHelper.getOwnerFrame(owner), title);
		buildUI();
	}

	private void buildUI() {
		generatorPanel = new GeneratorPanel();
		generatorPanel.addGeneratorListener(this);
		getContentPane().add(generatorPanel);
		pack();
	}

	public void closed() {
		setVisible(false);
		ok=false;
		this.personRecord=null;
	}

	public void saved(PersonRecord personRecord) {
		setVisible(false);
		ok=true;
		this.personRecord=personRecord;
	}

	public boolean isOk() {
		return ok;
	}

	public PersonRecord getPersonRecord() {
		return personRecord;
	}
}
