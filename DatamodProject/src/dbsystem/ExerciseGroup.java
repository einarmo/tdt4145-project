package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Objects;

public class ExerciseGroup extends BaseModel implements Comparable<ExerciseGroup> {
	HashSet<WithGr> exercises;
	public Long id;
	public String name;
	ExerciseGroup(long id) {
		this.id = id;
	}
	public ExerciseGroup(String name) {
		this.name = name;
	}
	@Override
	void describe() {
		tableName = "ExerciseGroup";
		autoinc = true;
		attributeNames = new String[] {"name"};
		mutableAttributeNames = attributeNames;
		primaryKeyNames = new String[] {"id"};
		exercises = new HashSet<WithGr>();
	}
	@Override
	public void setAttributes(ResultSet rs, int domain) {
		try {
			if (domain == Domains.SELECT) {
				id = rs.getLong("id");
				name = rs.getString("name");
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
			} else if (domain == Domains.SELECT) {
				st.setLong(index, id);
			}
			if (domain == Domains.SAVE) {
				st.setLong(index + 1, id);
			}
		} catch (Exception e) {
			System.out.println("Failed to get attributes: " + tableName + " " + e.getMessage());
		}
	}
	public int hashCode() {
		return Objects.hash(id);
	}
	public boolean equals(Object other) {
		return other instanceof ExerciseGroup && ((ExerciseGroup) other).id.equals(id);
	}
	@Override
	public String toString() {
		return "Group:\t" + id + "\nname:\t" + name;
	}
	public String toListString() {
		return "Group: " + id + ", name: " + name;
	}
	public String toDescString() {
		return "<html><b>Group:\t</b>" + id + "<br>name:\t" + name + "<br><br>Exercises:\t" + exercises.size() + "</html>";
	}
	public WithGr buildWithGr(int intensity, Exercise ex, boolean create) {
		Long ExerciseId = ex.id;
		for (WithGr wg : exercises) {
			if (wg.ExerciseId.equals(ExerciseId)) return wg;
		}
		WithGr wg = create ? new WithGr(intensity, ex, this) : ex.buildWithGr(intensity, this, true);
		exercises.add(wg);
		return wg;
	}
	public void removeExercise(WithGr gr) {
		exercises.remove(gr);
	}
	@Override
	public void remove(DBController dbc) {
		for (WithGr gr : exercises) {
			gr.ex.removeGroup(gr);
		}
		dbc.removeGroup(this);
	}
	@Override
	public int compareTo(ExerciseGroup other) {
		return id.compareTo(other.id);
	}
}
