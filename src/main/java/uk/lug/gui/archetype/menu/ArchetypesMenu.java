package uk.lug.gui.archetype.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.lug.MutableList;
import uk.lug.serenity.npc.managers.ArchetypesManager;
import uk.lug.serenity.npc.model.Level;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.util.SwingHelper;

public class ArchetypesMenu extends JPanel {
	private static MutableList<Archetype> archetypeList = new MutableList<Archetype>();
	private JTextField tagField;
	private List<BatchKey> batchList;

	public ArchetypesMenu() {
		super();
		batchList = new ArrayList<BatchKey>();
		checkArchetypeList();
		buildUI();
	}

	private void buildUI() {
		setLayout(new GridBagLayout());
		int y = 1;
		for (final Archetype arch : archetypeList) {
			JLabel l = new JLabel(arch.getName());
			SwingHelper.boldLabel(l);

			add(l, new GridBagConstraints(0, y, 1, 1, .6d, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, SwingHelper.DEFAULT_INSETS, 0, 0));
			SpinnerModel ghModel = new SpinnerNumberModel(0, 0, 10, 1);
			SpinnerModel vModel = new SpinnerNumberModel(0, 0, 10, 1);
			SpinnerModel bdhModel = new SpinnerNumberModel(0, 0, 10, 1);
			ghModel.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					add(Level.GREENHORN, arch);
				}
			});
			vModel.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					add(Level.VETERAN, arch);
				}
			});
			bdhModel.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					add(Level.BIG_DAMN_HERO, arch);
				}
			});
			JSpinner ghSpinner = new JSpinner(ghModel);
			JSpinner vSpinner = new JSpinner(vModel);
			JSpinner bdhSpinner = new JSpinner(bdhModel);
			Insets i = new Insets(2, 5, 2, 5);
			add(ghSpinner, new GridBagConstraints(1, y, 1, 1, .3d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 20, 2, 5), 0, 0));
			add(vSpinner, new GridBagConstraints(2, y, 1, 1, .3d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, i, 0, 0));
			add(bdhSpinner, new GridBagConstraints(3, y, 1, 1, .3d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, i, 0, 0));
			y++;
		}
		JLabel glabel = new JLabel("Greenhorn");
		JLabel vlabel = new JLabel("Veteran");
		JLabel bdhlabel = new JLabel("Big Damn Hero");
		glabel.setHorizontalAlignment(SwingConstants.CENTER);
		vlabel.setHorizontalAlignment(SwingConstants.CENTER);
		bdhlabel.setHorizontalAlignment(SwingConstants.CENTER);
		SwingHelper.boldLabel(glabel);
		SwingHelper.boldLabel(vlabel);
		SwingHelper.boldLabel(bdhlabel);
		add(glabel, new GridBagConstraints(1, 0, 1, 1, .3d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		add(vlabel, new GridBagConstraints(2, 0, 1, 1, .3d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		add(bdhlabel, new GridBagConstraints(3, 0, 1, 1, .3d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		add(new JLabel("Tags"), new GridBagConstraints(0, y, 1, 1, .6d, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, SwingHelper.DEFAULT_INSETS,
				0, 0));
		tagField = new JTextField(30);
		add(tagField, new GridBagConstraints(1, y, 3, 1, .4d, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

	}

	protected void add(Level level, Archetype arch) {
		BatchKey key = BatchKey.create(arch, level);
		batchList.add(key);
	}

	private void checkArchetypeList() {
		if (archetypeList.isEmpty()) {
			archetypeList.addAll(ArchetypesManager.getArchetypes());
		}
	}

	public static void main(String[] args) {
		try {
			JFrame win = new JFrame("TEST");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ArchetypesMenu amenu = new ArchetypesMenu();
			win.getContentPane().add(amenu);
			win.pack();
			win.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MutableList<Archetype> getArchetypeList() {
		return archetypeList;
	}

	public List<BatchKey> getBatchMap() {
		return batchList;
	}

	public String getTags() {
		return tagField.getText();
	}

}
