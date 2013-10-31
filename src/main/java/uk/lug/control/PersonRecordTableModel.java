package uk.lug.control;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import uk.lug.dao.records.PersonRecord;
import uk.lug.data.AbstractListBackedTableModel;
import uk.lug.data.FilteringListBackedTableModel;
import uk.lug.data.filter.AbstractFilter;
import uk.lug.data.filter.Filter;
import uk.lug.serenity.npc.model.Person;

public class PersonRecordTableModel extends FilteringListBackedTableModel<PersonRecord> {
	private static final String LEVEL = "Level";
	private static final String PLAYER = "Player";
	private static final String UPDATED = "Updated";
	private static final String CREATED = "Created";
	private static final String TAGS = "Tags";
	private static final String ARCHETYPE = "Archetype";
	private static final String NAME = "Name";
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MMM/yy");
	private static final String[] COLUMN_NAMES = { NAME, LEVEL, ARCHETYPE, TAGS, CREATED, UPDATED, PLAYER };
	private String tag;

	public PersonRecordTableModel(List<PersonRecord> dataList) {
		super(dataList);
	}

	@Override
	protected String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	@Override
	protected Object getValueFor(PersonRecord rowObject, String columnName) {
		if (StringUtils.equals(NAME, columnName)) {
			return rowObject.getName();
		} else if (StringUtils.equals(ARCHETYPE, columnName)) {
			return rowObject.getArchetype();
		} else if (StringUtils.equals(TAGS, columnName)) {
			return rowObject.getTags();
		} else if (StringUtils.equals(PLAYER, columnName)) {
			return StringUtils.isEmpty(rowObject.getPlayerName()) ? "" : rowObject.getPlayerName();
		} else if (StringUtils.equals(CREATED, columnName)) {
			return SDF.format(rowObject.getCreated());
		} else if (StringUtils.equals(UPDATED, columnName)) {
			return SDF.format(rowObject.getLastUpdated());
		} else if (StringUtils.equals(LEVEL, columnName)) {
			int l = rowObject.getPerson().getLevel();
			switch (l) {
			case Person.LEVEL_GREENHORN:
				return "Greenhorn";
			case Person.LEVEL_VETERAN:
				return "Veteran";
			case Person.LEVEL_BIG_DAM_HERO:
				return "Big Dam Hero";
			default:
				return "Other";
			}
		} else {
			throw new IllegalArgumentException("Unrecognised column name : " + columnName);
		}
	}

	public void setTag(String tag) {
		this.tag = tag;
		doFilter();
	}

	public String getTag() {
		return tag;
	}

	@Override
	protected Filter<PersonRecord> getFilter() {
		return tagFilter;
	}

	private Filter<PersonRecord> tagFilter = new AbstractFilter<PersonRecord>() {

		public boolean matches(PersonRecord object) {
			if (StringUtils.isEmpty(tag)) {
				return true;
			}
			String ptag = object.getTags().toLowerCase();
			if (StringUtils.isEmpty(ptag)) {
				return false;
			}

			for (String t : StringUtils.split(tag, " ")) {
				if (ptag.indexOf(t.toLowerCase()) == -1) {
					return false;
				}
			}
			return true;
		}

	};

}
