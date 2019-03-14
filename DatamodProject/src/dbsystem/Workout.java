package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;

public class Workout extends BaseModel implements Comparable<Workout>{
	public HashSet<WithEx> exercises;
	public Long id = null;
	public Timestamp timestamp = null;
	public Integer performance = null;
	public Integer shape = null;
	public String note = null;
	public Workout(long id) {
		this.id = id;
	}
	public Workout(Timestamp timestamp, int performance, int shape, String note) {
		this.timestamp = timestamp;
		this.performance = performance;
		this.shape = shape;
		this.note = note;
	}
	@Override
	void describe() {
		tableName = "Workout";
		autoinc = true;
		attributeNames = new String[] {"timestamp", "performance", "shape", "note"};
		mutableAttributeNames = attributeNames;
		primaryKeyNames = new String[] {"id"};
		exercises = new HashSet<WithEx>();
	}
	@Override
	public void setAttributes(ResultSet rs, int domain) {
		try {
			if (domain == Domains.SELECT) {
				timestamp = rs.getTimestamp("timestamp");
				performance = rs.getInt("performance");
				shape = rs.getInt("shape");
				note = rs.getString("note");
				id = rs.getLong("id");
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
				st.setTimestamp(index, timestamp);
				st.setInt(index + 1, performance);
				st.setInt(index + 2, shape);
				st.setString(index + 3, note);
			} else if (domain == Domains.SELECT) {
				st.setLong(index, id);
			}
			if (domain == Domains.SAVE) {
				st.setLong(index + 4, id);
			}
		} catch (Exception e) {
			System.out.println("Failed to get attributes: " + tableName + " " + e.getMessage());
		}
	}
	public int hashCode() {
		return Objects.hash(id);
	}
	public boolean equals(Object other) {
		return other instanceof Workout && ((Workout) other).id.equals(id);
	}
	@Override
	public String toString() {
		String s = "Workout:\t" + id + "\ntimestamp:\t" + timestamp.toString()
				+ "\nperformance:\t" + performance + "\nshape:\t\t" + shape
				+ "\nnote:\t\t" + note + "\n";
		for (WithEx we : exercises) {
			s = s + we.ex.toString().replace("\n", "\n\t");
			s = s + "order:\t" + we.intorder + "\n";
		}
		return s;
	}
	public String toListString() {
		Calendar c = Calendar.getInstance();
		c.setTime(timestamp);
		DateFormat d = DateFormat.getInstance();
		d.setCalendar(c);
		return "timestamp: " + d.format(timestamp) + ", exercises: " + exercises.size();
	}
	public String toDescString() {
		return "<html><b>Workout:</b>\t " + id + "<br>Performance:\t " + performance + "<br>Shape:\t\t " + shape
				+ "<br>Exercises: " + exercises.size() + "<br>Note: <br>" + note.replace("\n", "<br>") + "</html>";
	}
	public void refreshExercises(DBController dbc) {
		exercises.clear();
		try {
			Statement st = dbc.con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM WithEx WHERE WorkoutId=" + id);
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
		Long ExerciseId = ex.id;
		for (WithEx we : exercises) {
			if (we.ExerciseId.equals(ExerciseId) && we.intorder.equals(intorder)) {
				return we;
			}
		}
		WithEx we = create ? new WithEx(intorder, ex, this) : ex.buildWithEx(intorder, this, true);
		exercises.add(we);
		return we;
	}
	public WithEx createWithEx(int intorder, Exercise ex, DBController dbc) {
		WithEx we = buildWithEx(intorder, ex, false);
		if (!we.initialize(dbc)) {
			exercises.remove(we);
			we.ex.removeWorkout(we);
			return null;
		}
		return we;
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
	@Override
	public int compareTo(Workout other) {
		return (int) (timestamp.compareTo(other.timestamp));
	}
}
