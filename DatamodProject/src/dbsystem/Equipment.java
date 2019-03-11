package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Objects;

public class Equipment extends BaseModel {
	HashSet<WithEq> exercises;
	public Long id;
	public String name;
	public String description;
	public Equipment(long id) {
		this.id = id;
	}
	public Equipment(String name, String description) {
		this.name = name;
		this.description = description;
	}
	@Override
	void describe() {
		tableName = "Equipment";
		autoinc = true;
		attributeNames = new String[] {"name", "description"};
		mutableAttributeNames = attributeNames;
		primaryKeyNames = new String[] {"id"};
		exercises = new HashSet<WithEq>();
	}
	@Override
	public void setAttributes(ResultSet rs, int domain) {
		try {
			if (domain == Domains.SELECT) {
				id = rs.getLong("id");
				name = rs.getString("name");
				description = rs.getString("description");
			} else if (domain == Domains.INIT) {
				id = rs.getLong(1);
			}
		} catch (Exception e) {
			System.out.println("Failed to set attributes: " + tableName + " " + e.getMessage());
		}
	}
	@Override
	void getAttributes(PreparedStatement st, int domain, int index) {
		try {
			if (domain == Domains.SAVE || domain == Domains.INIT) {
				st.setString(index, name);
				st.setString(index + 1, description);
			} else if (domain == Domains.SELECT) {
				st.setLong(index, id);
			}
			if (domain == Domains.SAVE) {
				st.setLong(index + 2, id);
			}
		} catch (Exception e) {
			System.out.println("Failed to get attributes: " + tableName + " " + e.getMessage());
		}
	}
	public int hashCode() {
		return Objects.hash(id);
	}
	public boolean equals(Object other) {
		return other instanceof Equipment && ((Equipment) other).id.equals(id);
	}
	@Override
	public String toString() {
		return "Equipment:\t" + id + "\nname: \t\t" + name
				+ "\ndescription:\t" + description;
	}
	public String toListString() {
		return "id: " + id + ", name: " + name;
	}
	public String toDescString() {
		return "<html><b>Equipment:</b>\t" + id + "<br>name:\t" + name + "<br>description: " + description;
	}
	public WithEq buildWithEq(int sets, double kilos, Exercise ex, boolean create) {
		Long ExerciseId = ex.id;
		for (WithEq we : exercises) {
			if (we.ExerciseId.equals(ExerciseId)) return we;
		}
		WithEq we = create ? new WithEq(sets, kilos, ex, this) : ex.buildWithEq(sets, kilos, this, true);
		exercises.add(we);
		return we;
	}
	
	public void removeExercise(WithEq ex) {
		exercises.remove(ex);
	}
	@Override
	public void remove(DBController dbc) {
		for (WithEq eq : exercises) {
			eq.ex.removeEquipment();
		}
	}
}
