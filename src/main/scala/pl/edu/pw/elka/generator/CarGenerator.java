package pl.edu.pw.elka.generator;

import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;

public class CarGenerator extends TimerTask {

	private final Database databaseRef;
	private final Random randomGenerator;
	private final Integer maxCars;

	public CarGenerator(Database databaseRef, Random randomGenerator, Integer maxCars) {
		this.databaseRef = databaseRef;
		this.randomGenerator = randomGenerator;
		this.maxCars = maxCars;//TODO now now used - remove or use it
	}

	@Override
	public void run() {
		List<Coordinate> borderLanes = databaseRef.listBorderLanes();
		int chosenIdx = randomGenerator.nextInt(borderLanes.size() - 1);
		Coordinate chosen = borderLanes.get(chosenIdx);

		databaseRef.setCarsNumber(chosen, databaseRef.getCarsNumber(chosen) + 1);
	}

}
