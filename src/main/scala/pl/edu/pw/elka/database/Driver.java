package pl.edu.pw.elka.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.knowledgeDatabase.Junction;

public class Driver extends TimerTask {

	private final Database databaseRef;
	private final Random randomGenerator;

	public Driver(Database databaseRef, Random randomGenerator) {
		this.databaseRef = databaseRef;
		this.randomGenerator = randomGenerator;
	}

	@Override
	public void run() {
		//FIXME some loosing cars - make unit test to detect this
		System.out.printf("Run driver %s%n", new Date());
		Map<Coordinate, Long> greenLightLanesToCarNumbers = databaseRef.getLaneCoordinates()
				.stream()
				.filter(c -> databaseRef.getTrafficLight(c) == Light.GREEN)
				.collect(Collectors.toMap(Function.identity(), databaseRef::getCarsNumber));

		Stream<Map<Coordinate, Long>> increaseCarNumbersInCoordinates = greenLightLanesToCarNumbers.entrySet()
				.stream()
				.map(entry -> processGreenLightLane(entry.getKey(), entry.getValue()));

		Map<Coordinate, Long> finalCarsNumbersIncrease = increaseCarNumbersInCoordinates.map(map -> new ArrayList<>(map.entrySet()))
				.flatMap(List::stream)
				.map(entry -> new CarsAggregation(entry.getKey(), entry.getValue()))
				.collect(Collectors.groupingBy(CarsAggregation::getCoordinate, Collectors.summingLong(CarsAggregation::getCarNumber)));

		increaseCarNumbers(finalCarsNumbersIncrease);
	}

	protected void increaseCarNumbers(Map<Coordinate, Long> finalCarsNumbersIncrease) {
		finalCarsNumbersIncrease.forEach((c, carIncrease) -> {
			long actCarNumber = databaseRef.getCarsNumber(c);
			databaseRef.setCarsNumber(c, actCarNumber + carIncrease);
		});
	}

	protected Map<Coordinate, Long> processGreenLightLane(Coordinate laneCoordinate, Long carNumbers) {
		Junction junction = databaseRef.getJunction(laneCoordinate.getJunction())
				.orElseThrow(() -> new RuntimeException("Junction of this name does not exists"));
		Coordinate nextRoadInJunction = new Coordinate(junction.name(), junction.getNextRoad(laneCoordinate), "");

		Optional<JunctionMatching> nextJunctionAndRoad = databaseRef.getMatchedRoad(nextRoadInJunction.getJunction(),
				nextRoadInJunction.getRoad());

		Map<Coordinate, Long> increaseCarNumbersInCoordinates = nextJunctionAndRoad.map(
				junctionAndRoad -> this.computeIncreaseCarNumbersInNextLane(junctionAndRoad, carNumbers)).orElseGet(HashMap::new);

		databaseRef.setCarsNumber(laneCoordinate, 0L); //TODO release cars partially, not all from lane
		return increaseCarNumbersInCoordinates;
	}

	protected Map<Coordinate, Long> computeIncreaseCarNumbersInNextLane(JunctionMatching nextJunctionAndRoad, long carNumbers) {
		return Collections.nCopies((int) carNumbers, nextJunctionAndRoad)
				.stream()
				.map(jm -> new Coordinate(jm.getJunctionB(), jm.getRoadB(), this.randomLane()))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	protected String randomLane() {
		return Lanes.getByIndex(randomGenerator.nextInt(Lanes.values().length));
	}

	protected static class CarsAggregation {

		private final Coordinate c;
		private final Long carNumber;

		CarsAggregation(Coordinate c, Long carNumber) {
			this.c = c;
			this.carNumber = carNumber;
		}

		public Coordinate getCoordinate() {
			return c;
		}

		public Long getCarNumber() {
			return carNumber;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof CarsAggregation))
				return false;
			final CarsAggregation that = (CarsAggregation) o;
			return c.equals(that.c) && carNumber.equals(that.carNumber);
		}

		@Override
		public int hashCode() {
			return Objects.hash(c, carNumber);
		}

	}

}

