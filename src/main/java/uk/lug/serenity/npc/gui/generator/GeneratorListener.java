package uk.lug.serenity.npc.gui.generator;

import uk.lug.dao.records.PersonRecord;

public interface GeneratorListener {
	public void closed();

	public void saved(PersonRecord personRecord);
}
