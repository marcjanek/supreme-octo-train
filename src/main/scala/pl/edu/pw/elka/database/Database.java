package pl.edu.pw.elka.database;

import java.util.HashMap;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;

public final class Database {

	private final HashMap<Coordinate, Lane> coordinateToLane = new HashMap<>();

	public synchronized Light getTrafficLight(Coordinate coordinate) {
		return this.coordinateToLane.get(coordinate).getLight();
	}

	public synchronized Long getCarsNumber(Coordinate coordinate) {
		return this.coordinateToLane.get(coordinate).numberOfCars;
	}

	public synchronized void setTrafficLight(Coordinate coordinate, final Light light) {
		this.coordinateToLane.get(coordinate).light = light;
	}

	public synchronized void setCarsNumber(Coordinate coordinate, final Long carsNumber) {
		this.coordinateToLane.get(coordinate).numberOfCars = carsNumber;
	}

	public synchronized void createLane(Coordinate coordinate, final Light light, final Long carsNumber) {
		coordinateToLane.put(coordinate, new Lane(light, carsNumber));
	}

	//    create with default values: RED and 0 cars
	public synchronized void createLane(Coordinate coordinate) {
		createLane(coordinate, Light.RED, 0L);
	}

	public void createXJunction(final String junction) {
		createLane(new Coordinate(junction, Roads.A.state, Lanes.L.state));
		createLane(new Coordinate(junction, Roads.A.state, Lanes.P1.state));
		createLane(new Coordinate(junction, Roads.A.state, Lanes.P2.state));
		createLane(new Coordinate(junction, Roads.B.state, Lanes.L.state));
		createLane(new Coordinate(junction, Roads.B.state, Lanes.P1.state));
		createLane(new Coordinate(junction, Roads.B.state, Lanes.P2.state));
		createLane(new Coordinate(junction, Roads.C.state, Lanes.L.state));
		createLane(new Coordinate(junction, Roads.C.state, Lanes.P1.state));
		createLane(new Coordinate(junction, Roads.C.state, Lanes.P2.state));
		createLane(new Coordinate(junction, Roads.D.state, Lanes.L.state));
		createLane(new Coordinate(junction, Roads.D.state, Lanes.P1.state));
		createLane(new Coordinate(junction, Roads.D.state, Lanes.P2.state));
	}

}

