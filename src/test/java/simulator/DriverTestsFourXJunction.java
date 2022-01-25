package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.knowledgeDatabase.Junction;
import pl.edu.pw.elka.simulator.Driver;

/**
 * 4 X junctions, 4 cars starts from x3, each ends on another junction (x1,x2,x4)
 * Scheme:
 * X1 X2 X3
 * s  X4
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DriverTestsFourXJunction {

	private final DeterministicGeneratorMock nextLaneRandomGenerator = new DeterministicGeneratorMock(0, new ArrayList<>());
	private final DeterministicGeneratorMock passedCarsRandomGenerator = new DeterministicGeneratorMock(10, new ArrayList<>());

	private final Database database = new Database();
	private final Driver driver = new Driver(database, nextLaneRandomGenerator, passedCarsRandomGenerator);

	private final Coordinate threeCarsStraight = new Coordinate("3", Roads.A.name(), Lanes.P2.name());
	private final Coordinate oneCarLeft = new Coordinate("3", Roads.A.name(), Lanes.L.name());

	@BeforeAll
	void setup() {
		Junction x1 = database.createXJunction("1");
		Junction x2 = database.createXJunction("2");
		Junction x3 = database.createXJunction("3");
		Junction x4 = database.createXJunction("4");
		database.match(x1, x2, Roads.A, Roads.C);
		database.match(x2, x3, Roads.A, Roads.C);
		database.match(x2, x4, Roads.B, Roads.D);

		database.getLaneCoordinates().forEach(c -> database.setCarsNumber(c, 0L));
		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.RED));

	}

	@BeforeEach
	void initAll() {
		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.RED));
	}

	@Test
	@Order(1)
	void shouldAllCoordinatesHaveZeroCarsAfterTurningAllGreenLightsIfAnyCarsAreInDatabase() {
		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.GREEN));
		driver.run();

		database.getLaneCoordinates().stream().map(database::getCarsNumber).forEach(carNumber -> assertEquals(0L, carNumber));
	}

	@Test
	@Order(2)
	void shouldOneCarTurnLeftAndGoneAndOtherRunStraightToX2() {
		database.setCarsNumber(threeCarsStraight, 3L);
		database.setCarsNumber(oneCarLeft, 1L);

		List<Integer> nextLaneRandomGeneratorValues = Arrays.asList(0, 1, 2);//left, right, straight
		List<Integer> passedCarsRandomGenerator = Collections.emptyList();
		final Map<Coordinate, Long> coordinatesToCarNumbers = new HashMap<>();
		coordinatesToCarNumbers.put(new Coordinate("2", Roads.A.name(), Lanes.P1.name()), 1L);
		coordinatesToCarNumbers.put(new Coordinate("2", Roads.A.name(), Lanes.P2.name()), 1L);
		coordinatesToCarNumbers.put(new Coordinate("2", Roads.A.name(), Lanes.L.name()), 1L);

		List<Coordinate> greenLights = Arrays.asList(new Coordinate("3", Roads.A.name(), Lanes.P2.name()),
				new Coordinate("3", Roads.A.name(), Lanes.L.name()));

		performTurn(coordinatesToCarNumbers, nextLaneRandomGeneratorValues, passedCarsRandomGenerator, greenLights);
	}

	@Test
	@Order(3)
	void shouldOneCarTurnToX4OneRunStraightToX1AndOneTurnRightAndGone() {
		List<Integer> nextLaneRandomGeneratorValues = Arrays.asList(2, 0);
		List<Integer> passedCarsRandomGenerator = Collections.emptyList();// straight, left
		final Map<Coordinate, Long> coordinatesToCarNumbers = new HashMap<>();
		coordinatesToCarNumbers.put(new Coordinate("1", Roads.A.name(), Lanes.P2.name()), 1L);
		coordinatesToCarNumbers.put(new Coordinate("4", Roads.D.name(), Lanes.L.name()), 1L);

		List<Coordinate> greenLights = Arrays.asList(new Coordinate("2", Roads.A.name(), Lanes.P1.name()),
				new Coordinate("2", Roads.A.name(), Lanes.P2.name()), new Coordinate("2", Roads.A.name(), Lanes.L.name()));

		performTurn(coordinatesToCarNumbers, nextLaneRandomGeneratorValues, passedCarsRandomGenerator, greenLights);
	}

	@Test
	@Order(4)
	void shouldOneCarTurnLeftAndGoneAndOtherRunStraightAndGone() {
		List<Integer> nextLaneRandomGeneratorValues = Collections.emptyList();
		List<Integer> passedCarsRandomGenerator = Collections.emptyList();
		final Map<Coordinate, Long> coordinatesToCarNumbers = new HashMap<>();

		List<Coordinate> greenLights = Arrays.asList(new Coordinate("2", Roads.A.name(), Lanes.P2.name()),
				new Coordinate("2", Roads.A.name(), Lanes.L.name()), new Coordinate("1", Roads.A.name(), Lanes.P2.name()),
				new Coordinate("4", Roads.D.name(), Lanes.L.name()));

		performTurn(coordinatesToCarNumbers, nextLaneRandomGeneratorValues, passedCarsRandomGenerator, greenLights);
	}

	void performTurn(Map<Coordinate, Long> coordinateToCarNumbers, List<Integer> nextLaneRandomGeneratorValues,
			List<Integer> passedCarsRandomGenerator, List<Coordinate> greenLights) {
		this.nextLaneRandomGenerator.addValues(nextLaneRandomGeneratorValues);
		this.passedCarsRandomGenerator.addValues(passedCarsRandomGenerator);
		greenLights.forEach(c -> database.setTrafficLight(c, Light.GREEN));

		driver.run();

		database.getLaneCoordinates()
				.stream()
				.filter(c -> coordinateToCarNumbers.get(c) == null)
				.map(database::getCarsNumber)
				.forEach(carNumber -> assertEquals(0L, carNumber));

		coordinateToCarNumbers.forEach((c, expectedCarNumber) -> assertEquals(expectedCarNumber, database.getCarsNumber(c)));
	}

}
