package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class WithEx extends BaseModel implements Comparable<WithEx> {
	public Exercise ex;
	public Workout wo;
	public Integer intorder;
	public Long ExerciseId;
	public Long WorkoutId;
	public WithEx(int intorder, Exercise ex, Workout wo) {
		this.intorder = intorder;
		this.ExerciseId = ex.id;
		this.WorkoutId = wo.id;
		this.ex = ex;
		this.wo = wo;
	}
	@Override
	void describe() {
		tableName = "WithEx";
		attributeNames = new String[] {"intorder", "WorkoutId", "ExerciseId"};
		mutableAttributeNames = new String[] {};
		primaryKeyNames = new String[] {"intorder", "WorkoutId"};
	}
	@Override
	public void setAttributes(ResultSet rs, int domain) {
		try {
			if (domain == Domains.SELECT) {
				intorder = rs.getInt("intorder");
				WorkoutId = rs.getLong("WorkoutId");
				ExerciseId = rs.getLong("ExerciseId");
			}
		} catch (Exception e) {
			System.out.println("Failed to set attributes: " + tableName + " " + e.getMessage());
		}
	}
	@Override
	void getAttributes(PreparedStatement st, int domain, int index) {
		if (domain == Domains.SAVE) {
			throw new RuntimeException("No mutable properties " + tableName);
		}
		try {
			if (domain == Domains.INIT) {
				st.setInt(index, intorder);
				st.setLong(index + 1, WorkoutId);
				st.setLong(index + 2, ExerciseId);
			} else if (domain == Domains.SELECT) {
				st.setInt(index,  intorder);
				st.setLong(index + 1, WorkoutId);
			}
		} catch (Exception e) {
			System.out.println("Failed to get attributes: " + tableName + " " + e.getMessage());
		}
	}
	public int hashCode() {
		return Objects.hash(intorder, ExerciseId);
	}
	public boolean equals(Object other) {
		return other instanceof WithEx && ((WithEx) other).intorder.equals(intorder)
				&& ((WithEx) other).ExerciseId.equals(ExerciseId);
	}
	@Override
	public String toString() {
		return "WithEx:\nintorder:\t" + intorder + "\nExerciseId:\t" + ExerciseId + "\nWorkoutId:\t"
				+ WorkoutId;
	}

	@Override
	public int compareTo(WithEx oth) {
		return intorder - oth.intorder;
	}
	
	@Override
	public void remove(DBController dbc) {
		ex.removeWorkout(this);
		wo.removeExercise(this);
	}
}
