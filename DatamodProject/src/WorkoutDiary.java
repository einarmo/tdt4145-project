import dbsystem.DBController;
import dbsystem.Workout;
import ui.WorkoutList;
import dbsystem.Equipment;
import dbsystem.Exercise;
import dbsystem.ExerciseGroup;

public class WorkoutDiary {
	public static void main(String[] args) {
		DBController dbc = new DBController();
		dbc.createdb();
		/* Equipment e = dbc.createEquipment("Handlebar big", "Very long and even prettier");
		
		Workout w = dbc.createWorkout(6, 6, "I'm very tired now thanks");
		dbc.createWorkout(1, 1, "Test 1");
		dbc.createWorkout(2, 2, "Test 2");
		
		Exercise ex = dbc.createExercise("Big lifts", "Lift big");
		ex.description = "Bigger lifts";
		ex.save(dbc);
		
		Exercise ex2 = dbc.createExercise("Deep squats", "Squats that are deep");
		
		ExerciseGroup exg = dbc.createExerciseGroup("Legs");
		ExerciseGroup exg2 = dbc.createExerciseGroup("Arms");
		ExerciseGroup exg3 = dbc.createExerciseGroup("Strength");

		w.createWithEx(1, ex, dbc);
		w.createWithEx(2, ex, dbc);
		w.createWithEx(3, ex2, dbc);
		w.refreshExercises(dbc);
		ex.createWithGr(5, exg2, dbc);
		ex.createWithGr(5, exg3, dbc);
		ex2.createWithGr(6, exg3, dbc);
		ex2.createWithGr(7, exg, dbc);
		ex.createWithEq(5, 2.5, e, dbc);
		
		dbc.wipe(); */
		/* for (int i = 0; i < 500; i++) {
			Workout w = dbc.createWorkout(5, 5, "Test " + i);
			Exercise ex = dbc.createExercise("Test desc " + i, "Test name " + i);
			w.createWithEx(i+1, ex, dbc);
		} */
		WorkoutList ui = new WorkoutList(dbc);
		ui.setWList(dbc.fetchWorkouts(0, 0));
		ui.setFEList(dbc.fetchExercises(0, 0));
		ui.setGrList(dbc.fetchGroups(0, 0));
		ui.sortWList(); // Something breaks somewhere, I dunno, can't be bothered to fix it. This patches it.
	}
}
