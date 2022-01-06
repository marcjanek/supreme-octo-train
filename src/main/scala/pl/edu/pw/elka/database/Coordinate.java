package pl.edu.pw.elka.database;

import java.util.Objects;

/**
 * Represents coordinate of lane in the model (Junction name, Road in Junction name, and lane in junction name)
 */
public class Coordinate {

	private final String road;
	private final String junction;
	private final String lane;

	public Coordinate(String junction, String road, String lane) {
		this.junction = junction;
		this.road = road;
		this.lane = lane;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Coordinate))
			return false;
		final Coordinate that = (Coordinate) o;
		return Objects.equals(road, that.road) && Objects.equals(junction, that.junction) && Objects.equals(lane, that.lane);
	}

	@Override
	public int hashCode() {
		return Objects.hash(road, junction, lane);
	}

	public String getRoad() {
		return road;
	}

	public String getJunction() {
		return junction;
	}

	public String getLane() {
		return lane;
	}

}
