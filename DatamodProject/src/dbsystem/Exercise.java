package dbsystem;

import java.sql.Types;
import java.util.HashSet;

public class Exercise extends BaseModel {
	HashSet<WithEx> workouts;
	HashSet<WithGr> groups;
	WithEq eq;
	public Exercise(long id) {
		set("id", id);
	}
	public Exercise(String name, String description) {
		set("description", description);
		set("name", name);
	}
	@Override
	void describe() {
		name = "Exercise";
		attributeNames = new String[] {"id", "name", "description"};
		attributes.put("id", new Attribute<Long>("id", Types.BIGINT, Flags.PRIMARY_KEY, true));
		attributes.put("name", new Attribute<String>("name", Types.VARCHAR));
		attributes.put("description", new Attribute<String>("description", Types.NVARCHAR));
		workouts = new HashSet<WithEx>();
		groups = new HashSet<WithGr>();
	}
	@Override
	public String toString() {
		String s = "Exercise:\t" + get("id") + "\nname:\t\t" + get("name") + "\ndescription:\t"
				+ get("description") + "\n";
		if (eq != null) {
			s = s + eq.eq.toString() + "\n";
		}
		for (WithGr wg : groups) {
			s = s + wg.gr.toString().replace("\n", "\n\t") + "\n";
		}
		return s;
	}
	public WithEx buildWithEx(Integer intorder, Workout wo, boolean create) {
		Long WorkoutId = wo.get("id");
		for (WithEx we : workouts) {
			if (we.get("WorkoutId").equals(WorkoutId) && we.get("intorder").equals(intorder)) return we;
		}
		WithEx we = create ? new WithEx(intorder, this, wo) : wo.buildWithEx(intorder, this, true);
		workouts.add(we);
		return we;
	}
	public WithGr buildWithGr(int intensity, ExerciseGroup gr, boolean create) {
		Long GroupId = gr.get("id");
		for (WithGr wg : groups) {
			if (wg.get("GroupId").equals(GroupId)) return wg;
		}
		WithGr wg = create ? new WithGr(intensity, this, gr) : gr.buildWithGr(intensity, this, true);
		groups.add(wg);
		return wg;
	}
	public WithGr createWithGr(int intensity, ExerciseGroup gr, DBController dbc) {
		WithGr wg = buildWithGr(intensity, gr, false);
		wg.initialize(dbc);
		return wg;
	}
	public WithEq buildWithEq(int sets, double kilos, Equipment eq, boolean create) {
		if (this.eq != null) {
			if (!this.eq.get("EquipmentId").equals(eq.get("EquipmentId"))) {
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
		we.initialize(dbc);
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
}
