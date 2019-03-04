package dbsystem;

import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public abstract class BaseModel {
	public String name;
	HashMap<String, Attribute<?>> attributes = new HashMap<String, Attribute<?>>();
	public String[] attributeNames;
	abstract void describe();
	public abstract void remove(DBController dbc);
	public BaseModel() {
		describe();
	}
	public boolean save(DBController dbc) {
		try {
			String fields = "";
			String clause = "WHERE ";
			int clausecnt = 0;
			int attrcnt = 0;
			for (String atrn : attributeNames) {
				Attribute<?> atr = attributes.get(atrn);
				// Assumes immutable primary keys, which is standard in this database
				if (atr.flag != Flags.PRIMARY_KEY) {
					fields = fields + (attrcnt != 0 ? ", " : "") + atr.name + "=?";
					attrcnt++;
				} else {
					clause = clause + (clausecnt > 0 ? " AND " : "") + atr.name + "=?";
					clausecnt++;
				}
			}
			PreparedStatement st = dbc.con.prepareStatement("UPDATE " + name + " SET " + fields + " " + clause + ";");
			int atrind = 1;
			int clauseind = 1;
			for (String atrn : attributeNames) {
				Attribute<?> atr = attributes.get(atrn);
				if (atr.flag != Flags.PRIMARY_KEY) {
					atr.getAttribute(atrind, st);
					atrind++;
				} else {
					atr.getAttribute(clauseind+attrcnt, st);
					clauseind++;
				}
			}
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			System.out.println("Failed to save " + name + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	public boolean refresh(DBController dbc) {
		try {
			String clause = "WHERE ";
			int clausecnt = 0;
			for (int i = 0; i < attributeNames.length; i++) {
				Attribute<?> atr = attributes.get(attributeNames[i]);
				if (atr.flag == Flags.PRIMARY_KEY) {
					clause = clause + (clausecnt > 0 ? " AND " : "") + atr.name + "=?";
					clausecnt++;
				}
			}
			PreparedStatement st = dbc.con.prepareStatement("SELECT * FROM " + name + " " + clause + ";");
			int clauseind = 1;
			for (int i = 0; i < attributeNames.length; i++) {
				Attribute<?> atr = attributes.get(attributeNames[i]);
				if (atr.flag == Flags.PRIMARY_KEY) {
					atr.getAttribute(clauseind, st);
					clauseind++;
				}
			}
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				for (String atrn : attributeNames) {
					Attribute<?> atr = attributes.get(atrn);
					if (atr.flag != Flags.PRIMARY_KEY) { 
						setAttribute(atr, rs);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to refresh " + name + ": " + e.getMessage());
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean initialize(DBController dbc) {
		try {
			String fields = "";
			String values = "";
			int attrcnt = 0;
			boolean autoinc = false;
			String autoincname = null;
			for (int i = 0; i < attributeNames.length; i++) {
				Attribute<?> atr = attributes.get(attributeNames[i]);
				if (!atr.autoInc) {
					fields = fields + (attrcnt != 0 ? ", " : "") + atr.name;
					values = values + (attrcnt != 0 ? ", " : "") + "?";
					attrcnt++;
				} else {
					autoinc = true;
					autoincname = atr.name;
					if (atr.get() != null) return false;
				}
			}
			PreparedStatement st = dbc.con.prepareStatement("INSERT INTO " + name + " (" + fields + ")" + " VALUES "
					+ "(" + values + ");", autoinc ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
			int atrind = 1;
			for (String atrn : attributeNames) {
				Attribute<?> atr = attributes.get(atrn);
				if (!atr.autoInc) {
					atr.getAttribute(atrind, st);
					atrind++;
				}
			}
			st.executeUpdate();
			if (autoinc) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					((Attribute<Long>) attributes.get(autoincname)).setAttribute(rs.getLong(1));
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to initialize " + name + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean destroy(DBController dbc) {
		try {
			String clause = "WHERE ";
			int clausecnt = 0;
			for (int i = 0; i < attributeNames.length; i++) {
				Attribute<?> atr = attributes.get(attributeNames[i]);
				if (atr.flag == Flags.PRIMARY_KEY) {
					clause = clause + (clausecnt > 0 ? " AND " : "") + atr.name + "=?";
					clausecnt++;
				}
			}
			PreparedStatement st = dbc.con.prepareStatement("DELETE FROM " + name + " " + clause + ";");
			int clauseind = 1;
			for (int i = 0; i < attributeNames.length; i++) {
				Attribute<?> atr = attributes.get(attributeNames[i]);
				if (atr.flag == Flags.PRIMARY_KEY) {
					atr.getAttribute(clauseind, st);
					clauseind++;
				}
			}
			System.out.println(st.toString());
		} catch (Exception e) {
			System.out.println("Failed to destroy " + name + ": " + e.getMessage());
		}
		remove(dbc);
		return true;
	}
	@SuppressWarnings("unchecked")
	void setAttribute(Attribute<?> rawatr, ResultSet rs) {
		if (rawatr == null) return;
		try {
			switch(rawatr.type) {
			case Types.BIGINT:
				((Attribute<Long>) rawatr).setAttribute(rs.getLong(rawatr.name)); break;
			case Types.INTEGER:
				((Attribute<Integer>) rawatr).setAttribute(rs.getInt(rawatr.name)); break;
			case Types.VARCHAR:
				((Attribute<String>) rawatr).setAttribute(rs.getString(rawatr.name)); break;
			case Types.NVARCHAR:
				((Attribute<String>) rawatr).setAttribute(rs.getString(rawatr.name)); break;
			case Types.SMALLINT:
				((Attribute<Integer>) rawatr).setAttribute(rs.getInt(rawatr.name)); break;
			case Types.TIMESTAMP:
				((Attribute<Timestamp>) rawatr).setAttribute(rs.getTimestamp(rawatr.name)); break;
			case Types.FLOAT:
				((Attribute<Double>) rawatr).setAttribute(rs.getDouble(rawatr.name)); break;
			default:
				throw new RuntimeException("Unhandled type");
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to set attribute " + rawatr.name, e);
		}
	}
	protected class Attribute<T> {
		public String name;
		T value;
		int type;
		public int flag;
		public boolean autoInc = false;
		Attribute(String name, int type, int flag, boolean autoInc) {
			this.name = name;
			this.type = type;
			this.flag = flag;
			this.autoInc = autoInc;
		}
		Attribute(String name, int type) {
			this.name = name;
			this.type = type;
			this.flag = Flags.NONE;
			this.autoInc = false;
		}
		T get() { return value; }
		public String toString() {
			return value.toString();
		}
		public void getAttribute(int index, PreparedStatement st) {
			try {
				switch(type) {
				case Types.BIGINT:
					st.setLong(index, (Long) value); break;
				case Types.INTEGER:
					st.setInt(index, (Integer) value); break;
				case Types.VARCHAR:
					st.setString(index, (String) value); break;
				case Types.NVARCHAR:
					st.setString(index, (String) value); break;
				case Types.SMALLINT:
					st.setInt(index, (Integer) value); break;
				case Types.TIMESTAMP:
					st.setTimestamp(index, (Timestamp) value); break;
				case Types.FLOAT:
					st.setDouble(index, (Double) value); break;
				default:
					throw new RuntimeException("Unhandled type");
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to set attribute: " + name, e);
			}
		}
		public void setAttribute(T value) {
			this.value = value;
		}
	}

	public static class Flags {
		static int NONE = 0;
		static int FOREIGN_KEY = 1;
		static int PRIMARY_KEY = 2;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void set(String name, T value) {
		Attribute<?> atr = attributes.get(name);
		if (atr == null) return;
		((Attribute<T>) atr).setAttribute(value);
	}
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		Attribute<?> atr = attributes.get(name);
		if (atr == null) return null;
		return (T) atr.get();
	}

}
