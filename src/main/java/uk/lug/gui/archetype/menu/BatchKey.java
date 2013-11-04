package uk.lug.gui.archetype.menu;

import uk.lug.serenity.npc.model.Level;
import uk.lug.serenity.npc.random.archetype.Archetype;

public class BatchKey {
	private Archetype archetype;
	private Level level;

	public static BatchKey create(Archetype arch, Level level) {
		BatchKey ret = new BatchKey();
		ret.setArchetype(arch);
		ret.setLevel(level);
		return ret;
	}
	
	public Archetype getArchetype() {
		return archetype;
	}

	public void setArchetype(Archetype archetype) {
		this.archetype = archetype;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * @autogenerated by CodeHaggis (http://sourceforge.net/projects/haggis)
	 * @overwrite equals()
	 * @return boolean returns a boolean value, which calculates, if the objects are equal.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		BatchKey castedObj = (BatchKey) o;
		return ((this.archetype == null ? castedObj.archetype == null : this.archetype.equals(castedObj.archetype)) && (this.level == null ? castedObj.level == null
				: this.level.equals(castedObj.level)));
	}
}