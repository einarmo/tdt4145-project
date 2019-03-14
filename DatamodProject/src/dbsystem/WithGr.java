package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class WithGr extends BaseModel {
	public Exercise ex;
	public ExerciseGroup gr;
	public Long ExerciseId;
	public Long GroupId;
	public Integer intensity;
	public WithGr(int intensity, Exercise ex, ExerciseGroup gr) {
		this.ex = ex;
		this.gr = gr;
		this.GroupId = gr.id;
		this.ExerciseId = ex.id;
		this.intensity = intensity;
	}
	@Override
	public String toString() {
		return "";
	}
	public String toEListString() {
		return gr.toListString() + ", intensity: " + intensity;
	}
	@Override
	void describe() {
		tableName = "WithGr";
		attributeNames = new String[] {"GroupId", "ExerciseId", "intensity"};
		mutableAttributeNames = new String[] {"intensity"};
		primaryKeyNames = new String[] {"GroupId", "ExerciseId"};
	}
	@Override
	public void setAttributes(ResultSet rs, int domain) {
		try {
			if (domain == Domains.SELECT) {
				GroupId = rs.getLong("GroupId");
				ExerciseId = rs.getLong("ExerciseId");
				intensity = rs.getInt("intensity");
			}
		} catch (Exception e) {
			System.out.println("Failed to set attributes: " + tableName + " " + e.getMessage());
		}
	}
	@Override
	void getAttributes(PreparedStatement st, int domain, int index) {
		try {
			if (domain == Domains.INIT) {
				st.setLong(index, GroupId);
				st.setLong(index + 1, ExerciseId);
				st.setInt(index + 2, intensity);
			} else if (domain == Domains.SELECT) {
				st.setLong(index,  GroupId);
				st.setLong(index + 1, ExerciseId);
			} else if (domain == Domains.SAVE) {
				st.setInt(index, intensity);
				st.setLong(index + 1, GroupId);
				st.setLong(index + 2, ExerciseId);
			}
		} catch (Exception e) {
			System.out.println("Failed to get attributes: " + tableName + " " + e.getMessage());
		}
	}
	public int hashCode() {
		return Objects.hash(GroupId, ExerciseId);
	}
	public boolean equals(Object other) {
		return other instanceof WithGr && ((WithGr) other).GroupId.equals(GroupId)
				&& ((WithGr) other).ExerciseId.equals(ExerciseId);
	}
	@Override
	public void remove(DBController dbc) {
		ex.removeGroup(this);
		gr.removeExercise(this);
	}
}
