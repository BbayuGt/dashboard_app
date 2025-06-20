package dpbo.dashboardApp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas yang bertanggung jawab untuk mengelola koneksi ke database
 * dan menginisialisasi struktur database jika belum ada.
 * 
 */
public class DatabaseManager {
	// URL untuk koneksi ke database SQLite
	private static final String url = "jdbc:sqlite:sample.db";

	// Variabel untuk menyimpan status koneksi
	private static boolean isConnected = false;

	// Objek Connection untuk berinteraksi dengan database
	private static Connection connection;

	/**
	 * Memeriksa apakah koneksi ke database berhasil atau belum.
	 * 
	 * @return true jika koneksi berhasil, false jika tidak
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Mendapatkan koneksi ke database SQLite.
	 * Jika koneksi belum ada, maka akan dibuat koneksi baru.
	 * 
	 * @return objek {@see Connection} yang digunakan untuk berinteraksi dengan database
	 * @throws Exception jika terjadi kesalahan saat membuat koneksi
	 */
	public static Connection getConnection() throws Exception {
		
		if (connection == null) {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(url);
			isConnected = true;
		}

		return connection;
	}

	/**
	 * Menginisialisasi database dengan membuat tabel-tabel yang diperlukan (jika tabel belum ada)
	 * 
	 * @throws Exception jika terjadi kesalahan saat menginisialisasi database
	 */
	public void initializeDatabase() throws Exception {
		getConnection();
		if (!isConnected) {
			throw new Exception("Database connection is not established.");
		}

		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
												//
		// create user table
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS User ("
				+ "id INTEGER PRIMARY KEY,"
				+ "name VARCHAR(100) NOT NULL,"
				+ "email VARCHAR(100) NOT NULL"
				+ ");");

		// create Project table
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS Project ("
				+ "id INTEGER PRIMARY KEY,"
				+ "title VARCHAR(100) NOT NULL,"
				+ "description TEXT,"
				+ "deadline TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
				+ "type TEXT CHECK (type IN ('web', 'mobile', 'desktop')) NOT NULL,"
				+ "owner_id INTEGER NOT NULL,"
				+ "STATUS TEXT DEFAULT 'active',"
				+ "FOREIGN KEY (owner_id) REFERENCES User(id) ON DELETE CASCADE ON UPDATE CASCADE)"
				);

		// create Revision table
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS Revision ("
				+ "id INTEGER PRIMARY KEY,"
				+ "notes TEXT,"
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
				+ "project_id INTEGER NOT NULL,"
				+ "FOREIGN KEY (project_id) REFERENCES Project(id)"
				+ ")"
				);

		// Create sample user
		statement.executeUpdate("INSERT OR IGNORE INTO User (id, name, email) VALUES (1, 'admin', 'test@example.com')");
	}
}
