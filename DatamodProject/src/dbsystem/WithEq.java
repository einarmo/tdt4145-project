package dbsystem;

import java.sql.Types;

public class WithEq extends BaseModel {
	public Exercise ex;
	public Equipment eq;
	public WithEq(int sets, double kilos, Exercise ex, Equipment eq) {
		this.ex = ex;
		this.eq = eq;
		set("EquipmentId", eq.get("id"));
		set("ExerciseId", ex.get("id"));
		set("sets", sets);
		set("kilos", kilos);
	}
	
	@Override
	public String toString() {
		return "";
	}

	@Override
	void describe() {
		name = "WithEq";
		attributeNames = new String[] {"EquipmentId", "ExerciseId", "sets", "kilos"};
		attributes.put("EquipmentId", new Attribute<Long>("EquipmentId", Types.BIGINT));
		attributes.put("ExerciseId", new Attribute<Long>("ExerciseId", Types.BIGINT, Flags.PRIMARY_KEY, false));
		attributes.put("sets", new Attribute<Integer>("sets", Types.SMALLINT));
		attributes.put("kilos", new Attribute<Double>("kilos", Types.FLOAT));
	}

	@Override
	public void remove(DBController dbc) {
		
	}
}
