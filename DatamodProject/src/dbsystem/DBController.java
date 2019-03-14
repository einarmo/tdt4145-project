package dbsystem;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class DBController extends DBConn {
	HashMap<Integer, Workout> workouts;
	HashMap<Integer, Exercise> exercises;
	HashMap<Integer, Equipment> equipment;
	HashMap<Integer, ExerciseGroup> groups;
	public DBController() {
		connect();
		workouts = new HashMap<Integer, Workout>();
		exercises = new HashMap<Integer, Exercise>();
		equipment = new HashMap<Integer, Equipment>();
		groups = new HashMap<Integer, ExerciseGroup>();
	}
	public int workoutsLength() { return workouts.size(); }
	public int exercisesLength() { return exercises.size(); }
	public int equipmentLength() { return equipment.size(); }
	public int groupsLength() { return groups.size(); }

	public void createdb() {
		if (force) {
			String[] wipes = readFileToString("wipedb.sql", true).split(";");
			executeQueries(wipes);
			System.out.println("Wiped existing tables");
		}
		String[] queries = readFileToString("createdb.sql", true).split(";");
		executeQueries(queries);
		System.out.println("Successfully initialized tables");
		// Clean up potential swap-residue, this should not be neccessary, but it's convenient for now.
		executeQueries(new String[] {"DELETE FROM WithEx WHERE intorder=0;"});
	}
	public Exercise createExercise(String description, String name) {
		Exercise e = new Exercise(name, description);
		if (!e.initialize(this)) return null;
		exercises.put(e.hashCode(), e);
		return e;
	}
	public Exercise getExercise(long id) {
		Exercise e = exercises.get(Objects.hash(id));
		if (e != null) return e;
		e = new Exercise(id);
		e.refresh(this);
		e.refreshFull(this);
		exercises.put(e.hashCode(), e);
		return e;
	}
	public void removeExercise(Exercise ex) {
		exercises.remove(ex.hashCode());
	}
	public ArrayList<Exercise> fetchExercises(int limit, int offset) {
		try {
			Statement st = con.createStatement();
			ResultSet rs;
			if (limit == 0) {
				rs = st.executeQuery("Select * FROM Exercise ORDER BY id DESC");
			} else {
				rs = st.executeQuery("SELECT * FROM Exercise ORDER BY id DESC LIMIT " + limit
					+ " OFFSET " + offset);
			}
			ArrayList<Exercise> lst = new ArrayList<Exercise>();
			while (rs.next()) {
				Long id = rs.getLong("id");
				if (id != null) {
					if (exercises.containsKey(Objects.hash(id))) {
						lst.add(exercises.get(Objects.hash(id)));
					} else {
						Exercise w = new Exercise(id);
						w.setAttributes(rs, BaseModel.Domains.SELECT);
						lst.add(w);
						w.refreshFull(this);
					}
				}
			}
			return lst;
		} catch (Exception e) {
			System.out.println("Failed to fetch workouts: " + e.getMessage());
			return new ArrayList<Exercise>();
		}
	}
	public Workout createWorkout(Timestamp timestamp, int performance, int shape, String note) {
		Workout w = new Workout(timestamp, performance, shape, note);
		if (!w.initialize(this)) return null;
		workouts.put(w.hashCode(), w);
		return w;
	}
	public Workout createWorkout(int performance, int shape, String note) {
		return createWorkout(new Timestamp(new Date().getTime()), performance, shape, note);
	}
	public Workout getWorkout(long id) {
		Workout w = workouts.get(Objects.hash(id));
		if (w != null) return w;
		w = new Workout(id);
		w.refresh(this);
		workouts.put(w.hashCode(), w);
		return w;
	}
	public ArrayList<Workout> fetchWorkouts(int limit, int offset) {
		try {
			Statement st = con.createStatement();
			ResultSet rs;
			if (limit == 0) {
				rs = st.executeQuery("Select * FROM Workout ORDER BY timestamp DESC, id DESC");
			} else {
				rs = st.executeQuery("SELECT * FROM Workout ORDER BY timestamp DESC, id DESC LIMIT " + limit
					+ " OFFSET " + offset);
			}
			ArrayList<Workout> lst = new ArrayList<Workout>();
			while (rs.next()) {
				Long id = rs.getLong("id");
				if (id != null) {
					if (workouts.containsKey(Objects.hash(id))) {
						lst.add(workouts.get(Objects.hash(id)));
					} else {
						Workout w = new Workout(id);
						w.setAttributes(rs, BaseModel.Domains.SELECT);
						lst.add(w);
						w.refreshExercises(this);
					}
				}
			}
			return lst;
		} catch (Exception e) {
			System.out.println("Failed to fetch workouts: " + e.getMessage());
			return new ArrayList<Workout>();
		}
	}
	public void removeWorkout(Workout wo) {
		workouts.remove(wo.hashCode());
	}
	public Equipment createEquipment(String name, String description) {
		Equipment e = new Equipment(name, description);
		if (!e.initialize(this)) return null;
		equipment.put(e.hashCode(), e);
		return e;
	}
	public Equipment getEquipment(long id) {
		Equipment e = equipment.get(Objects.hash(id));
		if (e != null) return e;
		e = new Equipment(id);
		e.refresh(this);
		equipment.put(e.hashCode(), e);
		return e;
	}
	public void removeEquipment(Equipment eq) {
		equipment.remove(eq.hashCode());
	}
	public ArrayList<Equipment> fetchEquipment(int limit, int offset) {
		try {
			Statement st = con.createStatement();
			ResultSet rs;
			if (limit == 0) {
				rs = st.executeQuery("Select * FROM Equipment ORDER BY id DESC");
			} else {
				rs = st.executeQuery("SELECT * FROM Equipment ORDER BY id DESC LIMIT " + limit
					+ " OFFSET " + offset);
			}
			ArrayList<Equipment> lst = new ArrayList<Equipment>();
			while (rs.next()) {
				Long id = rs.getLong("id");
				if (id != null) {
					if (equipment.containsKey(Objects.hash(id))) {
						lst.add(equipment.get(Objects.hash(id)));
					} else {
						Equipment eq = new Equipment(id);
						eq.setAttributes(rs, BaseModel.Domains.SELECT);
						lst.add(eq);
					}
				}
			}
			return lst;
		} catch (Exception e) {
			System.out.println("Failed to fetch equipment: " + e.getMessage());
			return new ArrayList<Equipment>();
		}
	}
	public ExerciseGroup createExerciseGroup(String name) {
		ExerciseGroup g = new ExerciseGroup(name);
		if (!g.initialize(this)) return null;
		groups.put(g.hashCode(), g);
		return g;
	}
	public ExerciseGroup getExerciseGroup(long id) {
		ExerciseGroup g = groups.get(Objects.hash(id));
		if (g != null) return g;
		g = new ExerciseGroup(id);
		g.refresh(this);
		groups.put(g.hashCode(), g);
		return g;
	}
	public void removeGroup(ExerciseGroup gr) {
		groups.remove(gr.hashCode());
	}
	public ArrayList<ExerciseGroup> fetchGroups(int limit, int offset) {
		try {
			Statement st = con.createStatement();
			ResultSet rs;
			if (limit == 0) {
				rs = st.executeQuery("Select * FROM ExerciseGroup ORDER BY id DESC");
			} else {
				rs = st.executeQuery("SELECT * FROM ExerciseGroup ORDER BY id DESC LIMIT " + limit
					+ " OFFSET " + offset);
			}
			ArrayList<ExerciseGroup> lst = new ArrayList<ExerciseGroup>();
			while (rs.next()) {
				Long id = rs.getLong("id");
				if (id != null) {
					if (groups.containsKey(Objects.hash(id))) {
						lst.add(groups.get(Objects.hash(id)));
					} else {
						ExerciseGroup gr = new ExerciseGroup(id);
						gr.setAttributes(rs, BaseModel.Domains.SELECT);
						lst.add(gr);
					}
				}
			}
			return lst;
		} catch (Exception e) {
			System.out.println("Failed to fetch equipment: " + e.getMessage());
			return new ArrayList<ExerciseGroup>();
		}
	}
	public void wipe() {
		workouts.clear();
		groups.clear();
		equipment.clear();
		exercises.clear();
	}
}
