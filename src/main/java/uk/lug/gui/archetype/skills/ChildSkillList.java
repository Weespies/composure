/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.random.archetype.skills.ChildSkillGroup;
import uk.lug.serenity.npc.random.archetype.skills.ExclusiveChildSkillGroup;
import uk.lug.serenity.npc.random.archetype.skills.GeneralSkillBias;
import uk.lug.serenity.npc.random.archetype.skills.SkillBiasListener;
import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkill;
import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkillGroup;

/**
 * @author Luggy List both groups of skills and individual skill
 */
public class ChildSkillList extends JPanel implements DropTargetListener, SkillBiasListener, DeleteListener {
	private HashMap<String, WeightedChildSkillPanel> individualPanels;
	private HashMap<String, ChildSkillGroupPanel> groupPanels;
	private GeneralSkillBias dataModel;
	private DropTarget dropTarget;

	/**
	 * @param dataModel
	 */
	public ChildSkillList(GeneralSkillBias skillBias) {
		super();
		this.dataModel = skillBias;
		individualPanels = new HashMap<String, WeightedChildSkillPanel>();
		groupPanels = new HashMap<String, ChildSkillGroupPanel>();
		build();

		dropTarget = new DropTarget(this, DnDConstants.ACTION_MOVE, this);
		setDropTarget(dropTarget);
		skillBias.addListener(this);
	}

	/**
	 * 
	 */
	private void build() {
		removeAll();
		dataModel.sortChildGroups();
		dataModel.sortChildSkills();
		setLayout(new GridBagLayout());
		int y = 0;
		final Insets insets = new Insets(1, 2, 1, 2);
		for (WeightedChildSkill wcs : dataModel.getChildSkills()) {
			WeightedChildSkillPanel wcsPanel = new WeightedChildSkillPanel(wcs);
			wcsPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			add(wcsPanel, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
			wcsPanel.addDeleteListener(this);
			individualPanels.put(wcs.getSkillName(), wcsPanel);

			y++;
		}
		for (ChildSkillGroup group : dataModel.getChildGroups()) {
			ChildSkillGroupPanel groupPanel = null;
			if (group instanceof WeightedChildSkillGroup) {
				groupPanel = new WeightedChildSkillGroupPanel((WeightedChildSkillGroup) group);
			} else if (group instanceof ExclusiveChildSkillGroup) {
				groupPanel = new ExclusiveChildSkillGroupPanel((ExclusiveChildSkillGroup) group);
			}
			groupPanels.put(group.getName(), groupPanel);
			groupPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			add(groupPanel, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
			groupPanel.addDeleteListener(this);
			y++;
		}

		// padding label, to absorb any left over space
		add(new JLabel(" "), new GridBagConstraints(0, y, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
	}

	private static final GeneralSkillBias makeTestData() {
		GeneralSkillBias bias = new GeneralSkillBias(GeneralSkill.GUNS);
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		list1.add("Assault Rifles");
		list1.add("Rifles");
		list1.add("Plasma Rifle");
		list1.add("Laser Rifle");
		list1.add("Pulse Rifle");
		list2.add("Shotguns");
		list2.add("Energy Weapons");
		bias.createExclusiveGroup("ExGroup", list1);
		bias.createWeightingGroup("WtGroup", list2);
		return bias;
	}

	/**
	 * 
	 */
	public void refreshList() {
		for (WeightedChildSkillPanel wcspanel : individualPanels.values()) {
			wcspanel.removeDeleteListener(this);
		}
		for (ChildSkillGroupPanel csgp : groupPanels.values()) {
			csgp.removeDeleteListener(this);
		}
		build();
		revalidate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	public void dragEnter(DropTargetDragEvent dtde) {

	}

	private boolean dropSupported(DataFlavor[] flavors) {
		for (DataFlavor df : flavors) {
			if (df instanceof WeightedChildSkillFlavor) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	public void dragExit(DropTargetEvent dte) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	public void dragOver(DropTargetDragEvent dtde) {
		if (!dropSupported(dtde.getCurrentDataFlavors())) {
			dtde.rejectDrag();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {

		Transferable transferable = dtde.getTransferable();
		String skillName = null;
		try {
			skillName = (String) transferable.getTransferData(WeightedChildSkillFlavor.get());

		} catch (UnsupportedFlavorException e) {
			dtde.rejectDrop();
			return;
		} catch (IOException e) {
			dtde.rejectDrop();
			return;
		} catch (ClassCastException e) {
			dtde.rejectDrop();
			return;
		}

		boolean dropComleted = performSkillDrop(skillName, dtde.getLocation());
		dtde.dropComplete(dropComleted);
		if (!dropComleted && individualPanels.containsKey(skillName)) {
			individualPanels.get(skillName).dropFailed();
		}
	}

	/**
	 * React to a skill drop event.
	 * 
	 * @param dropSkillName
	 *            name of the skill
	 * @param dropLocation
	 *            location within this component of the drop event.
	 * @return true if the drop is accepted, false if it is rejected.
	 */
	private boolean performSkillDrop(String dropSkillName, Point dropLocation) {
		Component dropOn = getComponentAt(dropLocation);

		if (dropOn instanceof ChildSkillGroupPanel) {
			ChildSkillGroup targetGroup = ((ChildSkillGroupPanel) dropOn).getDataModel();
			if (dataModel.isInGroup(dropSkillName)) {
				if (targetGroup == dataModel.getGroupContaining(dropSkillName)) {
					return false;
				}
				dataModel.removeFromGroup(dataModel.getGroupContaining(dropSkillName).getName(), dropSkillName);
			}
			dataModel.addToGroup(targetGroup, dropSkillName);
			return true;
		} else {
			if (!dataModel.isInGroup(dropSkillName)) {
				return false;
			}
			dataModel.removeFromGroup(dataModel.getGroupContaining(dropSkillName).getName(), dropSkillName);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	public void dropActionChanged(DropTargetDragEvent dtde) {
		if (dtde.getDropAction() != DnDConstants.ACTION_MOVE) {
			dtde.rejectDrag();
		}
	}

	/**
	 * @param data
	 * @param win
	 * @param control
	 * @throws InterruptedException
	 */
	private static void runDataModelUpdateSequence(GeneralSkillBias data, JFrame win, ChildSkillList control) throws InterruptedException {
		int delay = 750;
		Thread.sleep(delay);
		data.removeFromGroup("ExGroup", "Rifles");
		Thread.sleep(delay);
		data.addToGroup(control.groupPanels.get("ExGroup").getDataModel(), "Rifles");
		Thread.sleep(delay);
		data.removeFromGroup("ExGroup", "Assault Rifles");
		Thread.sleep(delay);
		data.removeFromGroup("ExGroup", "Plasma Rifle");
		Thread.sleep(delay);
		data.removeFromGroup("ExGroup", "Pulse Rifle");
		Thread.sleep(delay);
		win.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lug.serenity.npc.random.archetype.skills.SkillBiasListener#addedChildSkill
	 * (lug.serenity.npc.random.archetype.skills.WeightedChildSkill)
	 */
	public void addedChildSkill(WeightedChildSkill skill) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshList();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lug.serenity.npc.random.archetype.skills.SkillBiasListener#addedToGroup
	 * (lug.serenity.npc.random.archetype.skills.ChildSkillGroup,
	 * lug.serenity.npc.random.archetype.skills.WeightedChildSkill)
	 */
	public void addedToGroup(ChildSkillGroup group, WeightedChildSkill skill) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshList();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lug.serenity.npc.random.archetype.skills.SkillBiasListener#groupDisbanded
	 * (lug.serenity.npc.random.archetype.skills.ChildSkillGroup,
	 * java.util.List)
	 */
	public void groupDisbanded(ChildSkillGroup group, List<WeightedChildSkill> members) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshList();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lug.serenity.npc.random.archetype.skills.SkillBiasListener#groupFormed
	 * (lug.serenity.npc.random.archetype.skills.ChildSkillGroup)
	 */
	public void groupFormed(ChildSkillGroup group) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshList();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lug.serenity.npc.random.archetype.skills.SkillBiasListener#removedChildSkill
	 * (lug.serenity.npc.random.archetype.skills.WeightedChildSkill)
	 */
	public void removedChildSkill(WeightedChildSkill skill) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshList();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lug.serenity.npc.random.archetype.skills.SkillBiasListener#removedFromGroup
	 * (lug.serenity.npc.random.archetype.skills.ChildSkillGroup,
	 * lug.serenity.npc.random.archetype.skills.WeightedChildSkill)
	 */
	public void removedFromGroup(ChildSkillGroup group, WeightedChildSkill skill) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshList();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lug.gui.archetype.skills.DeleteListener#delete(java.lang.String)
	 */
	public void delete(String name) {
		boolean isGroup = dataModel.hasChildGroup(name);

		if (isGroup) {
			dataModel.disbandGroup(name);
		} else {
			dataModel.removeChildSkill(name);
		}
	}

}