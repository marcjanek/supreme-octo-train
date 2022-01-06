package pl.edu.pw.elka.generator;

import java.util.Random;
import pl.edu.pw.elka.database.Database;

public class CarGenerator {

	private final Database databaseRef;
	private final Random randomGenerator;
	private final Integer maxCars;

	public CarGenerator(Database databaseRef, Random randomGenerator, Integer maxCars) {
		this.databaseRef = databaseRef;
		this.randomGenerator = randomGenerator;
		this.maxCars = maxCars;
	}

	public void generate() {
		int carsToGenerate = this.randomGenerator.nextInt(this.maxCars);

	}

}
