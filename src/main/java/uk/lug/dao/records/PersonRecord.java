package uk.lug.dao.records;

import java.io.IOException;
import java.util.Date;

import uk.lug.data.PersonUtils;
import uk.lug.serenity.npc.model.Person;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PEOPLE")
public class PersonRecord extends IdProvider {
	@DatabaseField(columnName = "DATA", canBeNull = false,dataType = DataType.SERIALIZABLE)
	private byte[] data;
	@DatabaseField(columnName = "NAME", canBeNull = false)
	private String name;
	@DatabaseField(columnName = "ARCHETYPE", canBeNull = false)
	private String archetype;
	@DatabaseField(columnName = "CREATED", canBeNull = false)
	private Date created;
	@DatabaseField(columnName = "LASTUPDATED", canBeNull = false)
	private Date lastUpdated;
	@DatabaseField(columnName = "ISPLAYER", canBeNull = false)
	private Boolean isPlayer=false;

	public static PersonRecord createFrom(Person data) {
		PersonRecord ret = new PersonRecord();
		ret.setName(data.getName());
		ret.setArchetype(data.getArchetypeName());
		ret.setCreated(new Date());
		try {
			ret.setData( PersonUtils.encode(data));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return ret;
	}



	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArchetype() {
		return archetype;
	}

	public void setArchetype(String archetype) {
		this.archetype = archetype;
	}

	public Boolean getIsPlayer() {
		return isPlayer;
	}

	public void setIsPlayer(Boolean isPlayer) {
		this.isPlayer = isPlayer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
