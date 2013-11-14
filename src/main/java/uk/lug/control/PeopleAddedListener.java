package uk.lug.control;

import java.util.List;

import uk.lug.dao.records.PersonRecord;

public interface PeopleAddedListener {
	public void peopleAdded(List<PersonRecord> records);
}
