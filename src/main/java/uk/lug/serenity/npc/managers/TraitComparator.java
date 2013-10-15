package uk.lug.serenity.npc.managers;

import java.util.Comparator;

import uk.lug.serenity.npc.model.traits.Trait;

/**
 * Comparator for sorting traits by name and then major/minor status.
 */
public class TraitComparator implements Comparator<Trait> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Trait trait1, Trait trait2) {
		int ret = trait1.getName().compareTo(trait2.getName());
		if (ret != 0) {
			return 0;
		}
		if (trait1.isMinor() && trait2.isMajor()) {
			return -1;
		} else if (trait1.isMajor() && trait2.isMinor()) {
			return 1;
		}

		return 0;
	}

}