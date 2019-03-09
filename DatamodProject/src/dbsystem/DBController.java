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
	}
	public Exercise createExercise(String description, String name) {
		Exercise e = new Exercise(name, description);
		e.initialize(this);
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
	ArrayList<Exercise> initExercises(ResultSet rs) {
		ArrayList<Exercise> lst = new ArrayList<Exercise>();
		try {
			while (rs.next()) {
				Long id = rs.getLong("id");
				if (id != null) {
					System.out.println("Init ex");
					if (exercises.containsKey(Objects.hash(id))) {
						System.out.println("Found exercise " + id);
						lst.add(exercises.get(Objects.hash(id)));
					} else {
						Exercise w = new Exercise(id);
						w.setAttributes(rs, BaseModel.Domains.SELECT);
						lst.add(w);
						w.refreshFull(this);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to initialize exercises: " + e.getMessage());
		}
		return lst;
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
			return initExercises(rs);
		} catch (Exception e) {
			System.out.println("Failed to fetch workouts: " + e.getMessage());
			return new ArrayList<Exercise>();
		}
	}
	public Workout createWorkout(Timestamp timestamp, int performance, int shape, String note) {
		Workout w = new Workout(timestamp, performance, shape, note);
		w.initialize(this);
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
	ArrayList<Workout> initWorkouts(ResultSet rs) {
		ArrayList<Workout> lst = new ArrayList<Workout>();
		try {
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
		} catch (Exception e) {
			System.out.println("Failed to initialize workouts: " + e.getMessage());
		}
		return lst;
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
			return initWorkouts(rs);
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
		e.initialize(this);
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
	public ExerciseGroup createExerciseGroup(String name) {
		ExerciseGroup g = new ExerciseGroup(name);
		g.initialize(this);
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
	public void wipe() {
		workouts.clear();
		groups.clear();
		equipment.clear();
		exercises.clear();
	}
}
