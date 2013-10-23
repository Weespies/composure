package uk.lug.dao.handlers;

import java.sql.SQLException;

public class DatabaseSchema {
	private static PersonDao personDao;
	
	static {
		try {
			init();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Cannot initialise dao schema");
		}
	}

	public static void init() throws SQLException {
		personDao = new PersonDao();
		personDao.init();
	}

	public static PersonDao getPersonDao() {
		return personDao;
	}

	public static void setPersonDao(PersonDao personDao) {
		DatabaseSchema.personDao = personDao;
	}
}
