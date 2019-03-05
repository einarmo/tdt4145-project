import dbsystem.DBController;
import dbsystem.Workout;
import dbsystem.Equipment;
import dbsystem.Exercise;
import dbsystem.ExerciseGroup;

public class WorkoutDiary {
	public static void main(String[] args) {
		DBController dbc = new DBController();
		dbc.createdb();
		Equipment e = dbc.createEquipment("Handlebar big", "Very long and even prettier");
		
		Workout w = dbc.createWorkout(6, 6, "I'm very tired now thanks");
		
		Exercise ex = dbc.createExercise("Big lifts", "Lift big");
		
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
		System.out.println(w.toString());
		exg3.destroy(dbc);
		w.refresh(dbc);
		System.out.println(w.toString());
		w.destroy(dbc);
		System.out.println(dbc.equipmentLength());
		System.out.println(dbc.workoutsLength());
		System.out.println(dbc.exercisesLength());
		System.out.println(dbc.groupsLength());

	}
}
