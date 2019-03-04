package dbsystem;

import java.sql.Types;

public class WithEx extends BaseModel implements Comparable<WithEx> {
	public Exercise ex;
	public Workout wo;
	public WithEx(int intorder, Exercise ex, Workout wo) {
		set("intorder", intorder);
		set("ExerciseId", ex.get("id"));
		set("WorkoutId", wo.get("id"));
		this.ex = ex;
		this.wo = wo;
	}
	@Override
	public String toString() {
		return "WithEx:\nintorder:\t" + get("intorder") + "\nExerciseId:\t" + get("ExerciseId") + "\nWorkoutId:\t"
				+ get("WorkoutId");
	}

	@Override
	public int compareTo(WithEx oth) {
		return ((Integer) this.get("intorder")) - ((Integer) oth.get("intorder"));
	}
	@Override
	void describe() {
		name = "WithEx";
		attributeNames = new String[] {"intorder", "WorkoutId", "ExerciseId"};
		attributes.put("intorder", new Attribute<Integer>("intorder", Types.INTEGER, Flags.PRIMARY_KEY, false));
		attributes.put("WorkoutId", new Attribute<Long>("WorkoutId", Types.BIGINT, Flags.PRIMARY_KEY, false));
		attributes.put("ExerciseId", new Attribute<Long>("ExerciseId", Types.BIGINT));
	}
	@Override
	public void remove(DBController dbc) {
		ex.removeWorkout(this);
		wo.removeExercise(this);
	}
}
