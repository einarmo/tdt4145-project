package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
	public String toWString() {
		return ex.toListString();
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
	public void swapOrder(DBController dbc, WithEx other) {
		if (other.WorkoutId != WorkoutId) return;
		try {
			// We have to remove the objects from any hashmaps/sets, as modifying hashed keys cause the sets to be invalidated
			wo.removeExercise(this);
			ex.removeWorkout(this);
			other.wo.removeExercise(other);
			other.ex.removeWorkout(other);
			Statement st = dbc.con.createStatement();
			int temp = intorder;
			int temp2 = other.intorder;
			st.executeUpdate("UPDATE WithEx SET intorder=0 WHERE intorder=" +intorder + " AND WorkoutId=" + WorkoutId);
			intorder = 0;
			st.executeUpdate("UPDATE WithEx SET intorder=" + temp + " WHERE intorder=" + other.intorder
					+ " AND WorkoutId=" + WorkoutId);
			other.intorder = temp;
			st.executeUpdate("UPDATE WithEx SET intorder=" + temp2 + " WHERE intorder=0 AND WorkoutId=" + WorkoutId);
			intorder = temp2;
			wo.exercises.add(this);
			ex.workouts.add(this);
			other.wo.exercises.add(other);
			other.ex.workouts.add(other);
		} catch (Exception e) {
			System.out.println("Failed to swap withEx: " + e.getMessage());
		}
	}
}
