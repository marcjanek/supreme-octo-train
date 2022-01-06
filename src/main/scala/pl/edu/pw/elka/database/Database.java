package pl.edu.pw.elka.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Lights;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.knowledgeDatabase.Junction;

public final class Database {

	private final HashMap<Coordinate, Lane> coordinateToLane = new HashMap<>();
	private final ArrayList<Junction> junctions = new ArrayList<>();
	private final List<JunctionMatching> junctionMatching = new ArrayList<>();

	public synchronized Boolean isBorderRoad(Coordinate c) { //FIXME must see if lane is input or output of junction
		String junctionName = c.getJunction();
		String road = c.getRoad();
		return junctionMatching.stream().noneMatch(m -> m.containsJunctionRoad(junctionName, road));
	}

	public synchronized List<Coordinate> listBorderLanes() {
		return coordinateToLane.keySet().stream().filter(this::isBorderRoad).collect(Collectors.toList());
	}

	public synchronized Set<Coordinate> getLaneCoordinates() {
		return coordinateToLane.keySet();
	}

	public synchronized Light getTrafficLight(final String junction, final Roads road, final Lanes lane) {
		return this.coordinateToLane.get(new Coordinate(junction, road.name(), lane.name())).getLight();
	}

	public synchronized Long getCarsNumber(final String junction, final Roads road, final Lanes lane) {
		return this.coordinateToLane.get(new Coordinate(junction, road.name(), lane.name())).numberOfCars;
	}

	public synchronized Light getTrafficLight(Coordinate coordinate) {
		return this.coordinateToLane.get(coordinate).getLight();
	}

	public synchronized Long getCarsNumber(Coordinate coordinate) {
		return this.coordinateToLane.get(coordinate).numberOfCars;
	}

	public synchronized void setTrafficLight(Coordinate coordinate, final Light light) {
		this.coordinateToLane.get(coordinate).light = light;
	}

	public synchronized void setTrafficLight(final String junction, final Roads road, final Lights lights, final Light light) {
		if (lights.state.equals(Lights.L.state)) {
			this.coordinateToLane.get(new Coordinate(junction, road.name(), Lanes.L.name())).light = light;
		} else {
			this.coordinateToLane.get(new Coordinate(junction, road.name(), Lanes.P1.name())).light = light;
			this.coordinateToLane.get(new Coordinate(junction, road.name(), Lanes.P2.name())).light = light;
		}

	}

	public synchronized void setCarsNumber(Coordinate coordinate, final Long carsNumber) {
		this.coordinateToLane.get(coordinate).numberOfCars = carsNumber;
	}

	public synchronized Lane createLane(Coordinate coordinate, final Light light, final Long carsNumber) {
		Lane lane = new Lane(coordinate, light, carsNumber);
		coordinateToLane.put(coordinate, lane);
		return lane;
	}

	//    create with default values: RED and 0 cars
	public synchronized Lane createLane(Coordinate coordinate) {
		return createLane(coordinate, Light.RED, 0L);
	}

	public Junction createXJunction(final String junction) {
		List<Coordinate> coordinates = Arrays.asList(new Coordinate(junction, Roads.A.state, Lanes.L.state),
				new Coordinate(junction, Roads.A.state, Lanes.P1.state), new Coordinate(junction, Roads.A.state, Lanes.P2.state),
				new Coordinate(junction, Roads.B.state, Lanes.L.state), new Coordinate(junction, Roads.B.state, Lanes.P1.state),
				new Coordinate(junction, Roads.B.state, Lanes.P2.state), new Coordinate(junction, Roads.C.state, Lanes.L.state),
				new Coordinate(junction, Roads.C.state, Lanes.P1.state), new Coordinate(junction, Roads.C.state, Lanes.P2.state),
				new Coordinate(junction, Roads.D.state, Lanes.L.state), new Coordinate(junction, Roads.D.state, Lanes.P1.state),
				new Coordinate(junction, Roads.D.state, Lanes.P2.state));
		List<Lane> lanes = coordinates.stream().map(this::createLane).collect(Collectors.toList());
		Junction xjunction = new XJunction(lanes, junction);
		this.junctions.add(xjunction);
		return xjunction;
	}

	public void match(Junction a, Junction b, Roads roadFromA, Roads roadFromB) {
		junctionMatching.add(new JunctionMatching(a.name(), b.name(), roadFromA.name(), roadFromB.name()));
	}

}

