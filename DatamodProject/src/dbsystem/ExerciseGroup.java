package dbsystem;

import java.sql.Types;
import java.util.HashSet;

public class ExerciseGroup extends BaseModel {
	HashSet<WithGr> exercises;
	ExerciseGroup(long id) {
		set("id", id);
	}
	ExerciseGroup(String name) {
		set("name", name);
	}
	@Override
	public String toString() {
		return "Group:\t" + get("id") + "\nname:\t" + get("name");
	}
	@Override
	void describe() {
		name = "ExerciseGroup";
		attributeNames = new String[] {"id", "name"};
		attributes.put("id", new Attribute<Long>("id", Types.BIGINT, Flags.PRIMARY_KEY, true));
		attributes.put("name", new Attribute<String>("name", Types.VARCHAR));
		exercises = new HashSet<WithGr>();
	}
	public WithGr buildWithGr(int intensity, Exercise ex, boolean create) {
		Long ExerciseId = ex.get("id");
		for (WithGr wg : exercises) {
			if (wg.get("ExerciseId").equals(ExerciseId)) return wg;
		}
		WithGr wg = create ? new WithGr(intensity, ex, this) : ex.buildWithGr(intensity, this, true);
		exercises.add(wg);
		return wg;
	}
	public void removeExercise(WithGr gr) {
		exercises.remove(gr);
	}
	@Override
	public void remove(DBController dbc) {
		for (WithGr gr : exercises) {
			gr.ex.removeGroup(gr);
		}
		dbc.removeGroup(this);
	}

}
