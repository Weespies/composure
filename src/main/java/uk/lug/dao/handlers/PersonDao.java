package uk.lug.dao.handlers;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import uk.lug.dao.records.PersonRecord;

public class PersonDao extends AbstractDao<PersonRecord, Long> {

	@Override
	public PersonRecord save(PersonRecord bean) throws SQLException {
		if (bean.getCreated() == null) {
			bean.setCreated(new Date());
		}
		bean.setLastUpdated(new Date());
		return super.save(bean);
	}

	@Override
	protected Class<PersonRecord> getBeanClass() {
		return PersonRecord.class;
	}
}
