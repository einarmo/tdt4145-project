package dbsystem;

import java.sql.*;
import java.util.Properties;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

public abstract class DBConn {
	protected Connection con = null;
	protected boolean force = false;

	public DBConn() {}
	public Connection getCon() { return con; }
	protected String readFileToString(String name, boolean internal) {
		String data = "";
		BufferedReader br = null;
		try {
			InputStream in;
			if (internal) {
				in = getClass().getClassLoader().getResourceAsStream(name);
				br = new BufferedReader(new InputStreamReader(in));
			} else {
				br = new BufferedReader(new FileReader(new File(name)));
			}
			String line = null;
			while ((line = br.readLine()) != null) {
				data = data + (data.equals("") ? "" : "\n") + line;
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to read " + (internal ? "internal" : "external") + " file: " + name, e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to close reader");
			}
		}
		return data;
	}

	protected void executeQueries(String[] queries) {
		for (int i = 0; i < queries.length; i++) {
			String query = queries[i] + ";";
			try {
				Statement stmt = con.createStatement();
				stmt.executeUpdate(query);
			} catch (Exception e) {
				System.out.println("Failed to execute query: " + e.getMessage());
			}
		}
	}

	public void connect() {
		Properties p = new Properties();
		String[] data = readFileToString("config.txt", false).split("\n");
		for (int i = 0; i < data.length; i++) {
			String[] dt = data[i].split(" ")[0].split("=");
			if (dt.length < 2)
				throw new RuntimeException("Bad config");
			dt[1] = dt[1].replaceAll("\n|\r\n|\r", "");
			p.put(dt[0], dt[1]);
		}
		try {
			String frc = p.getProperty("force");
			if (frc != null && frc.equals("true")) {
				force = true;
			}
			p.remove("force");
			String dbname = p.getProperty("dbname");
			if (dbname == null) {
				dbname = "datamod";
			}
			p.remove("dbname");
			con = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1/" + dbname + "?useSSL=false&allowPublicKeyRetrieval=true", p);
			System.out.println("Successfully connected to mysql server");
		} catch (Exception e) {
			throw new RuntimeException("Unable to connect to database", e);
		}
	}
}
