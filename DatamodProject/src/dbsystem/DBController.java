package dbsystem;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

public class DBController extends DBConn {
	HashSet<Workout> workouts;
	HashSet<Exercise> exercises;
	HashSet<Equipment> equipment;
	HashSet<ExerciseGroup> groups;
	public DBController() {
		connect();
		workouts = new HashSet<Workout>();
		exercises = new HashSet<Exercise>();
		equipment = new HashSet<Equipment>();
		groups = new HashSet<ExerciseGroup>();
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
		exercises.add(e);
		return e;
	}
	public Exercise getExercise(long id) {
		for (Exercise e : exercises) {
			if (e.id.equals(id)) return e;
		}
		Exercise e = new Exercise(id);
		e.refresh(this);
		exercises.add(e);
		return e;
	}
	public void removeExercise(Exercise ex) {
		exercises.remove(ex);
	}
	public Workout createWorkout(Timestamp timestamp, int performance, int shape, String note) {
		Workout w = new Workout(timestamp, performance, shape, note);
		w.initialize(this);
		workouts.add(w);
		return w;
	}
	public Workout createWorkout(int performance, int shape, String note) {
		return createWorkout(new Timestamp(new Date().getTime()), performance, shape, note);
	}
	public Workout getWorkout(long id) {
		for (Workout w : workouts) {
			if (w.id.equals(id)) return w;
		}
		Workout w = new Workout(id);
		w.refresh(this);
		workouts.add(w);
		return w;
	}
	public void removeWorkout(Workout wo) {
		workouts.remove(wo);
	}
	public Equipment createEquipment(String name, String description) {
		Equipment e = new Equipment(name, description);
		e.initialize(this);
		equipment.add(e);
		return e;
	}
	public Equipment getEquipment(long id) {
		for (Equipment e : equipment) {
			if (e.id.equals(id)) return e;
		}
		Equipment e = new Equipment(id);
		e.refresh(this);
		equipment.add(e);
		return e;
	}
	public void removeEquipment(Equipment eq) {
		equipment.remove(eq);
	}
	public ExerciseGroup createExerciseGroup(String name) {
		ExerciseGroup e = new ExerciseGroup(name);
		e.initialize(this);
		groups.add(e);
		return e;
	}
	public ExerciseGroup getExerciseGroup(long id) {
		for (ExerciseGroup g : groups) {
			if (g.id.equals(id)) return g;
		}
		ExerciseGroup g = new ExerciseGroup(id);
		g.refresh(this);
		groups.add(g);
		return g;
	}
	public void removeGroup(ExerciseGroup gr) {
		groups.remove(gr);
	}
}
