package uk.lug.dao.handlers;

import java.io.File;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class HypersonicConnection {
	private static ConnectionSource connection = null;

	public static ConnectionSource getConnection() throws SQLException {
		if (connection == null || !connection.isOpen()) {
			initConnection();
		}
		return connection;
	}

	private static void initConnection() throws SQLException {
		// this uses h2 by default but change to match your database
		String databaseUrl = buildDatabaseURL();
		// create a connection source to our database
		connection = new JdbcConnectionSource(databaseUrl, "SA", "");

		// instantiate the dao
		// Dao<Account, String> accountDao =
		// DaoManager.createDao(connectionSource, Account.class);

		// if you need to create the 'accounts' table make this call
		// TableUtils.createTable(connectionSource, Account.class);
	}

	public static void close() throws SQLException {
		connection.close();
	}

	private static String buildDatabaseURL() {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:h2:file:");
		sb.append(getDatabaseFile());
		sb.append(";");
		return sb.toString();
	}

	private static Object getDatabaseFile() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.home"));
		sb.append(File.separator);
		sb.append("composure");
		sb.append(File.separator);
		sb.append("db");
		sb.append(File.separator);
		sb.append("hsql.db");
		File dbfile = new File(sb.toString());
		if (!dbfile.exists()) {
			dbfile.mkdirs();	
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			ConnectionSource connection = getConnection();
			System.out.println("Connected");
			Thread.sleep(1000*5);
			close();
			System.out.println("Closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
