package pl.edu.pw.elka.database;

import java.util.Objects;
import pl.edu.pw.elka.enums.Roads;

public class JunctionMatching {

	private final String junctionA;
	private final String junctionB;

	private final String roadA;
	private final String roadB;

	public JunctionMatching(String junctionA, String junctionB, String roadA, String roadB) {
		this.junctionA = junctionA;
		this.junctionB = junctionB;
		this.roadA = roadA;
		this.roadB = roadB;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof JunctionMatching))
			return false;
		final JunctionMatching that = (JunctionMatching) o;
		return junctionA.equals(that.junctionA) && junctionB.equals(that.junctionB) && roadA == that.roadA && roadB == that.roadB;
	}

	@Override
	public int hashCode() {
		return Objects.hash(junctionA, junctionB, roadA, roadB);
	}

	public boolean containsJunctionRoad(String junctionName, String road) {
		return (junctionName.equals(junctionA) && road.equals(roadA)) || (junctionName.equals(junctionB) && road.equals(roadB));
	}

	public String getJunctionA() {
		return junctionA;
	}

	public String getJunctionB() {
		return junctionB;
	}

	public String getRoadA() {
		return roadA;
	}

	public String getRoadB() {
		return roadB;
	}

}
