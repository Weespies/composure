package uk.lug.dao.records;

import com.j256.ormlite.field.DatabaseField;

public class IdProvider {
	@DatabaseField(columnName = "ID", generatedId = true)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
