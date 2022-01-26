package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.knowledgeDatabase.Junction;
import pl.edu.pw.elka.simulator.Driver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DriverTestsFourXJunctionSquareTopology {

	private final DeterministicGeneratorMock nextLaneRandomGenerator = new DeterministicGeneratorMock(0, new ArrayList<>());
	private final DeterministicGeneratorMock passedCarsRandomGenerator = new DeterministicGeneratorMock(0, new ArrayList<>());

	private final Database database = new Database();
	private final Driver driver = new Driver(database, nextLaneRandomGenerator, passedCarsRandomGenerator);

	private final Map<Coordinate, Long> firstTurnCars = new HashMap<>();
	private final Map<Coordinate, Long> secondTurnCars = new HashMap<>();

	@BeforeAll
	void setup() {
		Junction x1 = database.createXJunction("1");
		Junction x2 = database.createXJunction("2");
		Junction x3 = database.createXJunction("3");
		Junction x4 = database.createXJunction("4");
		database.match(x1, x2, Roads.B, Roads.D);
		database.match(x1, x3, Roads.C, Roads.A);
		database.match(x3, x4, Roads.B, Roads.D);
		database.match(x2, x4, Roads.C, Roads.A);

		database.getLaneCoordinates().forEach(c -> database.setCarsNumber(c, 0L));
		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.RED));

		firstTurnCars.put(new Coordinate("1", "D", "P1"), 5L);
		firstTurnCars.put(new Coordinate("1", "A", "P2"), 10L);

		firstTurnCars.put(new Coordinate("2", "C", "L"), 3L);
		firstTurnCars.put(new Coordinate("2", "A", "P1"), 3L);
		firstTurnCars.put(new Coordinate("2", "A", "L"), 7L);

		firstTurnCars.put(new Coordinate("3", "B", "P1"), 6L);

		firstTurnCars.put(new Coordinate("4", "C", "P2"), 6L);
		firstTurnCars.put(new Coordinate("4", "A", "P1"), 5L);

		secondTurnCars.put(new Coordinate("1", "A", "P2"), 5L);
		secondTurnCars.put(new Coordinate("1", "B", "P1"), 1L);
		secondTurnCars.put(new Coordinate("1", "B", "P2"), 2L);
		secondTurnCars.put(new Coordinate("1", "B", "L"), 1L);
		secondTurnCars.put(new Coordinate("1", "C", "P1"), 2L);
		secondTurnCars.put(new Coordinate("1", "C", "P2"), 3L);
		secondTurnCars.put(new Coordinate("1", "C", "L"), 1L);
		secondTurnCars.put(new Coordinate("1", "D", "P1"), 2L);

		secondTurnCars.put(new Coordinate("2", "A", "P1"), 1L);
		secondTurnCars.put(new Coordinate("2", "A", "L"), 7L);
		secondTurnCars.put(new Coordinate("2", "C", "P1"), 2L);
		secondTurnCars.put(new Coordinate("2", "C", "P2"), 2L);
		secondTurnCars.put(new Coordinate("2", "C", "L"), 2L);

		secondTurnCars.put(new Coordinate("3", "A", "P1"), 3L);
		secondTurnCars.put(new Coordinate("3", "A", "P2"), 2L);
		secondTurnCars.put(new Coordinate("3", "A", "L"), 3L);
		secondTurnCars.put(new Coordinate("3", "B", "P1"), 3L);
		secondTurnCars.put(new Coordinate("3", "B", "P2"), 1L);
		secondTurnCars.put(new Coordinate("3", "B", "L"), 1L);

		secondTurnCars.put(new Coordinate("4", "C", "P2"), 1L);
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
	void shouldPerformFirstTurn() {
		firstTurnCars.forEach(database::setCarsNumber);

		List<Integer> nextLaneRandomGeneratorValues = Arrays.asList(0, 0, 0, 2, 2,// (0)
				0, 1, 1, 2, 2,// (1)
				1, 1, 1,// (2)
				0, 1, 1, 1, 2, // (4)
				2, 2, // (5)
				0, 1, 1, 2, 2, 2, // (6)
				0, 1 // (7)
		);

		List<Integer> passedCarsRandomGenerator = Arrays.asList(5, 5, 3, 0, 5, 2, 6, 2);

		final Map<Coordinate, Long> firstTurnFinalCarNumbers = new HashMap<>();
		firstTurnFinalCarNumbers.put(new Coordinate("1", "A", "P2"), 5L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "B", "P1"), 1L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "B", "P2"), 2L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "B", "L"), 1L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "C", "P1"), 2L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "C", "P2"), 3L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "C", "L"), 1L);
		firstTurnFinalCarNumbers.put(new Coordinate("1", "D", "P1"), 2L);

		firstTurnFinalCarNumbers.put(new Coordinate("2", "A", "P1"), 1L);
		firstTurnFinalCarNumbers.put(new Coordinate("2", "A", "L"), 7L);
		firstTurnFinalCarNumbers.put(new Coordinate("2", "C", "P1"), 2L);
		firstTurnFinalCarNumbers.put(new Coordinate("2", "C", "P2"), 2L);
		firstTurnFinalCarNumbers.put(new Coordinate("2", "C", "L"), 2L);

		firstTurnFinalCarNumbers.put(new Coordinate("3", "A", "P1"), 3L);
		firstTurnFinalCarNumbers.put(new Coordinate("3", "A", "P2"), 2L);
		firstTurnFinalCarNumbers.put(new Coordinate("3", "A", "L"), 3L);
		firstTurnFinalCarNumbers.put(new Coordinate("3", "B", "P1"), 3L);
		firstTurnFinalCarNumbers.put(new Coordinate("3", "B", "P2"), 1L);
		firstTurnFinalCarNumbers.put(new Coordinate("3", "B", "L"), 1L);

		firstTurnFinalCarNumbers.put(new Coordinate("4", "C", "P2"), 1L);

		List<Coordinate> greenLights = Arrays.asList(new Coordinate("1", "A", "P2"), new Coordinate("1", "D", "P1"),
				new Coordinate("2", "A", "P1"), new Coordinate("2", "A", "L"), new Coordinate("2", "C", "L"),
				new Coordinate("3", "B", "P1"), new Coordinate("4", "A", "P1"), new Coordinate("4", "C", "P2"));

		performTurn(firstTurnFinalCarNumbers, nextLaneRandomGeneratorValues, passedCarsRandomGenerator, greenLights);
	}

	@Test
	@Order(3)
	void shouldPerformSecondTurn() {
		//		secondTurnCars.forEach(database::setCarsNumber);

		List<Integer> nextLaneRandomGeneratorValues = Arrays.asList(1, 1, 1,// (0)
				0,// (2)
				1, 1, 1, 1, 1, // (5)
				1,// (8)
				2, 2, //(9)
				1, //(11)
				0, 1, 2 // (14)
		);

		List<Integer> passedCarsRandomGenerator = Arrays.asList(3, 2, 1, 0, 0, 5, 1, 1, 1, 2, 3, 1, 2, 2, 3, 7, 0, 1, 0, 0);

		final Map<Coordinate, Long> secondTurnFinalCarNumbers = new HashMap<>();
		secondTurnFinalCarNumbers.put(new Coordinate("1", "B", "P1"), 1L);
		secondTurnFinalCarNumbers.put(new Coordinate("1", "B", "P2"), 0L);
		secondTurnFinalCarNumbers.put(new Coordinate("1", "B", "L"), 2L);
		secondTurnFinalCarNumbers.put(new Coordinate("1", "C", "P1"), 4L);
		secondTurnFinalCarNumbers.put(new Coordinate("1", "D", "P1"), 0L);

		secondTurnFinalCarNumbers.put(new Coordinate("2", "C", "P1"), 2L);
		secondTurnFinalCarNumbers.put(new Coordinate("2", "C", "L"), 2L);
		secondTurnFinalCarNumbers.put(new Coordinate("2", "D", "P1"), 1L);

		secondTurnFinalCarNumbers.put(new Coordinate("3", "A", "P1"), 8L);
		secondTurnFinalCarNumbers.put(new Coordinate("3", "A", "P2"), 2L);
		secondTurnFinalCarNumbers.put(new Coordinate("3", "B", "L"), 1L);

		secondTurnFinalCarNumbers.put(new Coordinate("4", "D", "P1"), 1L);
		secondTurnFinalCarNumbers.put(new Coordinate("4", "D", "P2"), 1L);
		secondTurnFinalCarNumbers.put(new Coordinate("4", "D", "L"), 1L);
		List<Coordinate> greenLights = new ArrayList<>(secondTurnCars.keySet());

		performTurn(secondTurnFinalCarNumbers, nextLaneRandomGeneratorValues, passedCarsRandomGenerator, greenLights);
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
