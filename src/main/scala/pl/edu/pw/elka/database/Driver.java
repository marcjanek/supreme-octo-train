package pl.edu.pw.elka.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;
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
	//algo
	//1sprawdz czy ma zielone
	//jak ma znajdx kolejną droge
	//sprawdz czy to granica, czy inne skrzyzowanie
	//jak to pierwsze to usuń
	//jak to drugie to przenies go na nowy koordynat

	@Override
	public void run() {
		Map<Coordinate, Long> greenLightLanesToCarNumbers = databaseRef.getLaneCoordinates()
				.stream()
				.filter(c -> databaseRef.getTrafficLight(c) == Light.GREEN)
				.collect(Collectors.toMap(Function.identity(), databaseRef::getCarsNumber));

		greenLightLanesToCarNumbers.forEach((actCoordinate, carNumbers) -> {

			Junction junction = databaseRef.getJunction(actCoordinate.getJunction())
					.orElseThrow(() -> new RuntimeException("Junction of this name does not exists"));
			Coordinate nextRoadInJunction = new Coordinate(junction.name(), junction.getNextRoad(actCoordinate), "");

			Optional<JunctionMatching> nextJunctionAndRoad = databaseRef.getMatchedRoad(nextRoadInJunction.getJunction(),
					nextRoadInJunction.getRoad());

			Map<Coordinate, Long> newCarsNumbersInCoordinates = nextJunctionAndRoad.map(
							junctionAndRoad -> this.generateNewCarsNumbersInCoordinates(junctionAndRoad, carNumbers))
					.orElseGet(
							HashMap::new); //ta lista musi byc globalna, tzn jej ustawianie nastąpi dopiero gdy wyjmiemy wszystkie zielone swiatła z aut i wtedy przydzielamy auta na przewidziane miejsca

			databaseRef.setCarsNumber(actCoordinate, 0L);

		});

	}

	protected Map<Coordinate, Long> generateNewCarsNumbersInCoordinates(JunctionMatching nextJunctionAndRoad, long carNumbers) {
		return Collections.nCopies((int) carNumbers, nextJunctionAndRoad)
				.stream()
				.map(jm -> new Coordinate(jm.getJunctionB(), jm.getRoadB(), this.randomLane()))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	protected String randomLane() {
		return Lanes.getByIndex(randomGenerator.nextInt(Lanes.values().length));
	}

}
