package dbsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class WithEq extends BaseModel {
	public Exercise ex;
	public Equipment eq;
	public Integer sets;
	public Double kilos;
	public Long ExerciseId;
	public Long EquipmentId;
	public WithEq(int sets, double kilos, Exercise ex, Equipment eq) {
		this.ex = ex;
		this.eq = eq;
		this.sets = sets;
		this.kilos = kilos;
		this.ExerciseId = ex.id;
		this.EquipmentId = eq.id;
	}
	
	@Override
	public String toString() {
		return "";
	}
	public String toDescString() {
		return "sets:\t" + sets + "<br>kilos:\t" + kilos + "<br>" + eq.toString().replace("\n", "<br>");
	}
	@Override
	void describe() {
		tableName = "WithEq";
		attributeNames = new String[] {"ExerciseId", "EquipmentId", "sets", "kilos"};
		mutableAttributeNames = new String[] {"sets", "kilos"};
		primaryKeyNames = new String[] {"ExerciseId"};
	}
	@Override
	public void setAttributes(ResultSet rs, int domain) {
		try {
			if (domain == Domains.SELECT) {
				ExerciseId = rs.getLong("ExerciseId");
				EquipmentId = rs.getLong("EquipmentId");
				sets = rs.getInt("sets");
				kilos = rs.getDouble("kilos");
			}
		} catch (Exception e) {
			System.out.println("Failed to set attributes: " + tableName + " " + e.getMessage());
		}
	}
	@Override
	void getAttributes(PreparedStatement st, int domain, int index) {
		try {
			if (domain == Domains.INIT) {
				st.setLong(index, ExerciseId);
				st.setLong(index + 1, EquipmentId);
				st.setInt(index + 2, sets);
				st.setDouble(index + 3, kilos);
			} else if (domain == Domains.SELECT) {
				st.setLong(index,  ExerciseId);
			} else if (domain == Domains.SAVE) {
				st.setInt(index, sets);
				st.setDouble(index + 1, kilos);
				st.setLong(index, ExerciseId);
			}
		} catch (Exception e) {
			System.out.println("Failed to get attributes: " + tableName + " " + e.getMessage());
		}
	}
	public int hashCode() {
		return Objects.hash(ExerciseId);
	}
	public boolean equals(Object other) {
		return other instanceof WithEq && ((WithEq) other).ExerciseId.equals(ExerciseId);
	}
	@Override
	public void remove(DBController dbc) {
		ex.removeEquipment();
		eq.removeExercise(this);
	}
}
