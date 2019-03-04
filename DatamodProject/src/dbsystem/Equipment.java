package dbsystem;

import java.sql.Types;
import java.util.HashSet;

public class Equipment extends BaseModel {
	HashSet<WithEq> exercises;
	public Equipment(long id) {
		set("id", id);
	}
	public Equipment(String name, String description) {
		set("name", name);
		set("description", description);
	}
	@Override
	void describe() {
		this.name = "Equipment";
		attributeNames = new String[] {"id", "name", "description"};
		attributes.put("id", new Attribute<Long>("id", Types.BIGINT, Flags.PRIMARY_KEY, true));
		attributes.put("name", new Attribute<String>("name", Types.NVARCHAR));
		attributes.put("description", new Attribute<String>("description", Types.VARCHAR));
		exercises = new HashSet<WithEq>();
	}
	public WithEq buildWithEq(int sets, double kilos, Exercise ex, boolean create) {
		Long ExerciseId = ex.get("id");
		for (WithEq we : exercises) {
			if (we.get("ExerciseId").equals(ExerciseId)) return we;
		}
		WithEq we = create ? new WithEq(sets, kilos, ex, this) : ex.buildWithEq(sets, kilos, this, true);
		exercises.add(we);
		return we;
	}
	@Override
	public String toString() {
		return "Equipment:\t" + get("id") + "\nname: \t\t" + get("name")
				+ "\ndescription:\t" + get("description");
	}
	public void removeExercise(WithEq ex) {
		exercises.remove(ex);
	}
	@Override
	public void remove(DBController dbc) {
		for (WithEq eq : exercises) {
			eq.ex.removeEquipment();
		}
	}
}
