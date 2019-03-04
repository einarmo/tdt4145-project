package dbsystem;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Workout extends BaseModel {
	HashSet<WithEx> exercises;
	public Workout(long id) {
		this.set("id", id);
	}
	public Workout(Timestamp timestamp, int performance, int shape, String note) {
		this.set("timestamp", timestamp);
		this.set("performance", performance);
		this.set("shape", shape);
		this.set("note", note);
	}
	@Override
	void describe() {
		this.name = "Workout";
		attributeNames = new String[] {"id", "timestamp", "performance", "shape", "note"};
		attributes.put("id", new Attribute<Long>("id", Types.BIGINT, Flags.PRIMARY_KEY, true));
		attributes.put("timestamp", new Attribute<Timestamp>("timestamp", Types.TIMESTAMP));
		attributes.put("performance", new Attribute<Integer>("performance", Types.SMALLINT));
		attributes.put("shape", new Attribute<Integer>("shape", Types.SMALLINT));
		attributes.put("note", new Attribute<String>("note", Types.NVARCHAR));
		exercises = new HashSet<WithEx>();
	}
	@Override
	public String toString() {
		String s = "Workout:\t" + get("id") + "\ntimestamp:\t" + get("timestamp").toString()
				+ "\nperformance:\t" + get("performance") + "\nshape:\t\t" + get("shape")
				+ "\nnote:\t\t" + get("note") + "\n";
		for (WithEx we : exercises) {
			s = s + we.ex.toString().replace("\n", "\n\t");
			s = s + "order:\t" + we.get("intorder") + "\n";
		}
		return s;
	}
	public void refreshExercises(DBController dbc) {
		exercises.clear();
		try {
			Statement st = dbc.con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM WithEx WHERE WorkoutId=" + get("id"));
			while (rs.next()) {
				Exercise ex = dbc.getExercise(rs.getLong("ExerciseId"));
				buildWithEx(rs.getInt("intorder"), ex, false);
			}
		} catch (Exception e) {
			System.out.println("Failed to refresh Exercises in Workout: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public WithEx buildWithEx(int intorder, Exercise ex, boolean create) {
		Long ExerciseId = ex.get("id");
		for (WithEx we : exercises) {
			if (ex.get("ExerciseId") == ExerciseId && we.get("intorder").equals(intorder)) {
				return we;
			}
		}
		WithEx we = create ? new WithEx(intorder, ex, this) : ex.buildWithEx(intorder, this, true);
		exercises.add(we);
		return we;
	}
	public void createWithEx(int intorder, Exercise ex, DBController dbc) {
		WithEx we = buildWithEx(intorder, ex, false);
		we.initialize(dbc);
	}
	public void removeExercise(WithEx we) {
		exercises.remove(we);
	}
	@Override
	public void remove(DBController dbc) {
		for (WithEx we : exercises) {
			we.ex.removeWorkout(we);
		}
		dbc.removeWorkout(this);
	}
}
