package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;

public class Exercise extends BaseModel implements Comparable<Exercise> {
	HashSet<WithEx> workouts;
	public HashSet<WithGr> groups;
	public WithEq eq;
	public Long id = null;
	public String name = null;
	public String description = null;
	public Exercise(long id) {
		this.id = id;
	}
	public Exercise(String name, String description) {
		this.name = name;
		this.description = description;
	}
	@Override
	void describe() {
		tableName = "Exercise";
		autoinc = true;
		attributeNames = new String[] {"name", "description"};
		mutableAttributeNames = attributeNames;
		primaryKeyNames = new String[] {"id"};
		workouts = new HashSet<WithEx>();
		groups = new HashSet<WithGr>();
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
		return other instanceof Exercise && ((Exercise) other).id.equals(id);
	}
	@Override
	public String toString() {
		String s = "Exercise:\t" + id + "\nname:\t\t" + name + "\ndescription:\t"
				+ description + "\n";
		if (eq != null) {
			s = s + eq.eq.toString() + "\n";
		}
		for (WithGr wg : groups) {
			s = s + wg.gr.toString().replace("\n", "\n\t") + "\n";
		}
		return s;
	}
	public String toDescString() {
		String s = "<html><b>Exercise:</b>\t" + id + "<br>name:\t" + name + "<br>description:\t"
				+ description.replace("\n", "<br>") + "<br>";
		if (eq != null) {
			s = s + eq.toDescString() + "<br>";
		}
		s = s + "<br>groups:\t" + groups.size();
		return s;
	}
	public String toListString() {
		return "id: " + id + ", name: " + name;
	}
	public void refreshFull(DBController dbc) {
		eq = null;
		groups.clear();
		try {
			Statement st = dbc.con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM WithGr WHERE ExerciseId=" + id + ";");
			while (rs.next()) {
				ExerciseGroup gr = dbc.getExerciseGroup(rs.getLong("GroupId"));
				buildWithGr(rs.getInt("intensity"), gr, false);
			}
		} catch (Exception e) {
			System.out.println("Failed to load groups " + e.getMessage());
		}
		try {
			Statement st = dbc.con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM WithEq WHERE ExerciseId=" + id + ";");
			if (rs.next()) {
				Equipment eq = dbc.getEquipment(rs.getLong("EquipmentId"));
				buildWithEq(rs.getInt("sets"), rs.getDouble("kilos"), eq, false);
			}
		} catch (Exception e) {
			System.out.println("Failed to load equipment " + e.getMessage());
		}
		System.out.println("Refresh full exercise");
	}
	public WithEx buildWithEx(Integer intorder, Workout wo, boolean create) {
		Long WorkoutId = wo.id;
		for (WithEx we : workouts) {
			if (we.WorkoutId.equals(WorkoutId) && we.intorder.equals(intorder)) return we;
		}
		WithEx we = create ? new WithEx(intorder, this, wo) : wo.buildWithEx(intorder, this, true);
		workouts.add(we);
		return we;
	}
	public WithGr buildWithGr(int intensity, ExerciseGroup gr, boolean create) {
		Long GroupId = gr.id;
		for (WithGr wg : groups) {
			if (wg.GroupId.equals(GroupId)) return wg;
		}
		WithGr wg = create ? new WithGr(intensity, this, gr) : gr.buildWithGr(intensity, this, true);
		groups.add(wg);
		return wg;
	}
	public WithGr createWithGr(int intensity, ExerciseGroup gr, DBController dbc) {
		WithGr wg = buildWithGr(intensity, gr, false);
		if (!wg.initialize(dbc)) {
			groups.remove(wg);
			wg.gr.removeExercise(wg);
			return null;
		}
		return wg;
	}
	public WithEq buildWithEq(int sets, double kilos, Equipment eq, boolean create) {
		if (this.eq != null) {
			if (!this.eq.EquipmentId.equals(eq.id)) {
				throw new RuntimeException("Attempt to overwrite equipment");
			}
			return this.eq;
		}
		WithEq weq = create ? new WithEq(sets, kilos, this, eq) : eq.buildWithEq(sets, kilos, this, true);
		this.eq = weq;
		return weq;
	}
	public WithEq createWithEq(int sets, double kilos, Equipment eq, DBController dbc) {
		if (this.eq != null) {
			throw new RuntimeException("Attempt to overwrite equipment");
		}
		WithEq we = buildWithEq(sets, kilos, eq, false);
		if (!we.initialize(dbc)) {
			eq = null;
			we.eq.removeExercise(we);
			return null;
		}
		return we;
	}
	public void removeWorkout(WithEx we) {
		workouts.remove(we);
	}
	public void removeGroup(WithGr gr) {
		groups.remove(gr);
	}
	public void removeEquipment() {
		eq = null;
	}
	@Override
	public void remove(DBController dbc) {
		for (WithEx we : workouts) {
			we.wo.removeExercise(we);
		}
		for (WithGr gr : groups) {
			gr.gr.removeExercise(gr);
		}
		if (eq != null) {
			eq.eq.removeExercise(eq);
		}
		dbc.removeExercise(this);
	}
	@Override
	public int compareTo(Exercise other) {
		return id.compareTo(other.id);
	}
}
