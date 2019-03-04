package dbsystem;

import java.sql.Types;

public class WithGr extends BaseModel {
	public Exercise ex;
	public ExerciseGroup gr;
	public WithGr(int intensity, Exercise ex, ExerciseGroup gr) {
		set("GroupId", gr.get("id"));
		set("ExerciseId", ex.get("id"));
		set("intensity", intensity);
		this.ex = ex;
		this.gr = gr;
	}
	@Override
	public String toString() {
		return "";
	}
	@Override
	void describe() {
		name = "WithGr";
		attributeNames = new String[] {"GroupId", "ExerciseId", "intensity"};
		attributes.put("GroupId", new Attribute<Long>("GroupId", Types.BIGINT, Flags.PRIMARY_KEY, false));
		attributes.put("ExerciseId", new Attribute<Long>("ExerciseId", Types.BIGINT, Flags.PRIMARY_KEY, false));
		attributes.put("intensity", new Attribute<Integer>("intensity", Types.SMALLINT));
	}
	@Override
	public void remove(DBController dbc) {
		ex.removeGroup(this);
		gr.removeExercise(this);
	}
}
