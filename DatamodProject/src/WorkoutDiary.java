import java.awt.EventQueue;

import dbsystem.DBController;
import ui.WorkoutList;

public class WorkoutDiary {
	public static void main(String[] args) {
		DBController dbc = new DBController();
		dbc.createdb();
		Runnable runnable = new Runnable() {
            @Override
            public void run() {
                WorkoutList ui = new WorkoutList(dbc);
                ui.setWList(dbc.fetchWorkouts(0, 0));
        		ui.setFEList(dbc.fetchExercises(0, 0));
        		ui.setGrList(dbc.fetchGroups(0, 0));
            }
        };
        EventQueue.invokeLater(runnable);
	}
}
