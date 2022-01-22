package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.knowledgeDatabase.Junction;
import pl.edu.pw.elka.simulator.Driver;

public class DriverTestsTwoXJunction {

	private final DeterministicGeneratorMock randomGenerator = new DeterministicGeneratorMock(0);
	private Database database = null;

	private final Coordinate threeCarsOnRightLane = new Coordinate("1", Roads.D.name(), Lanes.P1.name());
	private final Coordinate twoCarsOnStraightLane = new Coordinate("1", Roads.D.name(), Lanes.P2.name());
	private final Coordinate threeCarsOnLeftLane = new Coordinate("1", Roads.A.name(), Lanes.L.name());

	@BeforeEach
	void initAll() {
		database = new Database();
		Junction x1 = database.createXJunction("1");
		Junction x2 = database.createXJunction("2");
		database.match(x1, x2, Roads.B, Roads.D);

		database.getLaneCoordinates().forEach(c -> database.setCarsNumber(c, 0L));
		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.RED));
		database.setCarsNumber(threeCarsOnLeftLane, 3L);
		database.setCarsNumber(twoCarsOnStraightLane, 2L);
		database.setCarsNumber(threeCarsOnRightLane, 3L);
	}

	@Test
	public void shouldNotMoveIfAllLightsAreRed() {
		Driver driver = new Driver(database, randomGenerator);
		driver.run();
		final Set<Coordinate> nonZeroCarNumbers = Stream.of(threeCarsOnRightLane, twoCarsOnStraightLane, threeCarsOnLeftLane)
				.collect(Collectors.toSet());

		database.getLaneCoordinates()
				.stream()
				.filter(c -> !nonZeroCarNumbers.contains(c))
				.map(c -> database.getCarsNumber(c))
				.forEach(carNumber -> assertEquals(0L, carNumber));

		assertEquals(3L, database.getCarsNumber(threeCarsOnRightLane));
		assertEquals(2L, database.getCarsNumber(twoCarsOnStraightLane));
		assertEquals(3L, database.getCarsNumber(threeCarsOnLeftLane));
	}

	@Test
	public void shouldMoveAllCarsToNextLanesIfAllLightsAreGreen() {
		Driver driver = new Driver(database, randomGenerator);
		randomGenerator.nextIntVal = 0; //should all cars be on left lane after riding by a junction
		Coordinate fiveCarsFromLeftAndStraight = new Coordinate("2", Roads.D.name(), Lanes.L.name());

		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.GREEN));
		driver.run();

		database.getLaneCoordinates()
				.stream()
				.filter(c -> !fiveCarsFromLeftAndStraight.equals(c))
				.map(c -> database.getCarsNumber(c))
				.forEach(carNumber -> assertEquals(0L, carNumber));

		assertEquals(5L, database.getCarsNumber(fiveCarsFromLeftAndStraight));
	}

	@Test
	public void shouldMoveStraightAndRightCarsToNextLanesIfOnlyStraightAndRightLightsAreGreen() {
		Driver driver = new Driver(database, randomGenerator);
		randomGenerator.nextIntVal = 2; //should all cars be on straight lane after riding by a junction
		Coordinate twoFromStraight = new Coordinate("2", Roads.D.name(), Lanes.P2.name());
		final Set<Coordinate> nonZeroCarNumbers = Stream.of(twoFromStraight, threeCarsOnLeftLane, threeCarsOnRightLane)
				.collect(Collectors.toSet());

		database.setTrafficLight(twoCarsOnStraightLane, Light.GREEN);
		driver.run();

		database.getLaneCoordinates()
				.stream()
				.filter(c -> !nonZeroCarNumbers.contains(c))
				.map(c -> database.getCarsNumber(c))
				.forEach(carNumber -> assertEquals(0L, carNumber));

		assertEquals(2L, database.getCarsNumber(twoFromStraight));
		assertEquals(3L, database.getCarsNumber(threeCarsOnLeftLane));
		assertEquals(3L, database.getCarsNumber(threeCarsOnRightLane));
	}

}


