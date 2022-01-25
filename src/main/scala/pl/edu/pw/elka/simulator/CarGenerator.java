package pl.edu.pw.elka.simulator;

import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;

public class CarGenerator extends TimerTask {

	private final Database databaseRef;
	private final Random coordinateGenerator;
	private final Random carCountGenerator;
	private final Integer maxCars;

	public CarGenerator(Database databaseRef, Integer maxCars, Random coordinateGenerator, Random carCountGenerator) {
		this.databaseRef = databaseRef;
		this.coordinateGenerator = coordinateGenerator;
		this.carCountGenerator = carCountGenerator;
		this.maxCars = maxCars;
	}

	@Override
	public void run() {
		List<Coordinate> borderLanes = databaseRef.listBorderLanes();
		int chosenIdx = coordinateGenerator.nextInt(borderLanes.size());
		Coordinate chosen = borderLanes.get(chosenIdx);

		Long actualCarNumbers = databaseRef.getCarsNumber(chosen);
		int carsToAdd = carCountGenerator.nextInt(this.maxCars);

		databaseRef.setCarsNumber(chosen, actualCarNumbers + carsToAdd);
	}

}
