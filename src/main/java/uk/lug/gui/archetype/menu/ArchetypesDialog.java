package uk.lug.gui.archetype.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import uk.lug.MutableList;
import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.util.SwingHelper;

public class ArchetypesDialog extends JDialog {
	private ArchetypesMenu archetypesMenu;
	private JPanel okCancelPanel;
	private JPanel main;
	private boolean ok = false;

	public ArchetypesDialog(Component owner, String title) {
		super(SwingHelper.getOwnerFrame(owner), title);
		setModal(true);
		buildUI();
	}

	private void buildUI() {
		main = new JPanel(new BorderLayout());
		archetypesMenu = new ArchetypesMenu();
		main.add(archetypesMenu, BorderLayout.CENTER);
		
		buildOKCancelPanel();
		main.add(okCancelPanel,BorderLayout.SOUTH);
		getContentPane().add(main);
		pack();
	}

	private void buildOKCancelPanel() {
		okCancelPanel = new JPanel(new GridBagLayout());
		okCancelPanel.add(new JButton(okAction), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				SwingHelper.DEFAULT_INSETS, 0, 0));
		okCancelPanel.add(new JButton(cancelAction), new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				SwingHelper.DEFAULT_INSETS, 0, 0));
	}

	protected Action okAction = new AbstractAction("OK", CachedImageLoader.CHECK_ICON) {

		public void actionPerformed(ActionEvent e) {
			doOk();
		}
	};

	private Action cancelAction = new AbstractAction("Cancel", CachedImageLoader.DELETE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	};

	protected void doOk() {
		ok = true;
		setVisible(false);
	}

	protected void doCancel() {
		ok = false;
		setVisible(false);
	}

	public boolean isOk() {
		return ok;
	}
	
	public String getTag() {
		return archetypesMenu.getTags();
	}

	public List<BatchKey> getBatchMap() {
		return archetypesMenu.getBatchMap();
	}
}
