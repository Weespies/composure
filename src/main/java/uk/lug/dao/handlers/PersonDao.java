package uk.lug.dao.handlers;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;

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

	public PersonRecord readNamed(String name) throws SQLException {
		QueryBuilder qb = dao.queryBuilder();
		qb.where().eq("NAME", name);
		return dao.queryForFirst(qb.prepare());
	}
}
