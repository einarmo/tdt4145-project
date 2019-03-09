package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class BaseModel {
	public String tableName;
	public String[] attributeNames; // All but auto-inc keys
	public String[] mutableAttributeNames; // All except primary keys
	public String[] primaryKeyNames; // All primary keys (also auto-inc)
	boolean autoinc = false;
	abstract void describe();
	public abstract void remove(DBController dbc);
	public abstract void setAttributes(ResultSet rs, int domain);
	abstract void getAttributes(PreparedStatement st, int domain, int index);
	public static class Domains {
		static int SAVE = 0;
		static int INIT = 1;
		static int SELECT = 2;
	}
	public BaseModel() {
		describe();
	}
	public boolean save(DBController dbc) {
		try {
			String fields = "";
			String clause = "WHERE ";
			for (int i = 0; i < mutableAttributeNames.length; i++) {
				fields = fields + (i != 0 ? ", " : "") + attributeNames[i] + "=?";
			}
			for (int i = 0; i < primaryKeyNames.length; i++) {
				clause = clause + (i != 0 ? " AND " : "") + primaryKeyNames[i] + "=?";
			}
			PreparedStatement st = dbc.con.prepareStatement("UPDATE " + tableName + " SET " + fields + " " + clause + ";");
			getAttributes(st, Domains.SAVE, 1);
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			System.out.println("Failed to save " + tableName + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	public boolean refresh(DBController dbc) {
		try {
			String clause = "WHERE ";
			for (int i = 0; i < primaryKeyNames.length; i++) {
				clause = clause + (i != 0 ? " AND " : "") + primaryKeyNames[i] + "=?";
			}
			PreparedStatement st = dbc.con.prepareStatement("SELECT * FROM " + tableName + " " + clause + ";");
			getAttributes(st, Domains.SELECT, 1);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				setAttributes(rs, Domains.SELECT);
			}
		} catch (Exception e) {
			System.out.println("Failed to refresh " + tableName + ": " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean initialize(DBController dbc) {
		try {
			String fields = "";
			String values = "";
			boolean autoinc = attributeNames.length < (mutableAttributeNames.length + primaryKeyNames.length);
			for (int i = 0; i < attributeNames.length; i++) {
				fields = fields + (i != 0 ? ", " : "") + attributeNames[i];
				values = values + (i != 0 ? ", " : "") + "?";
			}
			PreparedStatement st = dbc.con.prepareStatement("INSERT INTO " + tableName + " (" + fields + ")" + " VALUES "
					+ "(" + values + ");", autoinc ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
			getAttributes(st, Domains.INIT, 1);
			st.executeUpdate();
			if (autoinc) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					setAttributes(rs, Domains.INIT);
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to initialize " + tableName + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean destroy(DBController dbc) {
		try {
			String clause = "WHERE ";
			for (int i = 0; i < primaryKeyNames.length; i++) {
				clause = clause + (i != 0 ? " AND " : "") + primaryKeyNames[i] + "=?";
			}
			PreparedStatement st = dbc.con.prepareStatement("DELETE FROM " + tableName + " " + clause + ";");
			getAttributes(st, Domains.SELECT, 1);
			st.executeUpdate();
		} catch (Exception e) {
			System.out.println("Failed to destroy " + tableName + ": " + e.getMessage());
		}
		remove(dbc);
		return true;
	}


}
