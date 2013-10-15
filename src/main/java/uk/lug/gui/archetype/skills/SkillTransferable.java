/**
 * 
 */
package uk.lug.gui.archetype.skills;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkill;

/**
 * @author Luggy
 *
 */
public class SkillTransferable implements Transferable {
	private WeightedChildSkill transferChildSkill;
	
	/**
	 * @param transferChildSkill
	 */
	public SkillTransferable(WeightedChildSkill transferChildSkill) {
		super();
		this.transferChildSkill = transferChildSkill;
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if ( !(flavor instanceof WeightedChildSkillFlavor) ) {
			throw new UnsupportedFlavorException(flavor);
		}
		return transferChildSkill.getSkillName();
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{ WeightedChildSkillFlavor.get() };
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return ( flavor instanceof WeightedChildSkillFlavor );
	}

}
