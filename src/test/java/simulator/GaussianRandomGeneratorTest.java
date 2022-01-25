package simulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.edu.pw.elka.simulator.GaussianRandomGenerator;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GaussianRandomGeneratorTest {

	@BeforeAll
	void setup() {
		//		Junction x1 = database.createXJunction("1");
		//		Junction x2 = database.createXJunction("2");
		//		Junction x3 = database.createXJunction("3");
		//		Junction x4 = database.createXJunction("4");
		//		database.match(x1, x2, Roads.A, Roads.C);
		//		database.match(x2, x3, Roads.A, Roads.C);
		//		database.match(x2, x4, Roads.B, Roads.D);
		//
		//		database.getLaneCoordinates().forEach(c -> database.setCarsNumber(c, 0L));
		//		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.RED));
	}

	@BeforeEach
	void initAll() {
		//		database.getLaneCoordinates().forEach(c -> database.setTrafficLight(c, Light.RED));
	}

	@Test
	void shouldGenerateValuesLessOrEqualRange() {
		int range = 20;
		int iterations = 100;
		GaussianRandomGenerator generator = new GaussianRandomGenerator();
		for (int i = 0; i < iterations; ++i) {
			Assertions.assertTrue(generator.nextInt(range) <= range);
		}
	}

	@Test
	void shouldGenerateValuesMoreThanOrEqualZero() {
		int range = 20;
		int iterations = 100;
		GaussianRandomGenerator generator = new GaussianRandomGenerator();
		for (int i = 0; i < iterations; ++i) {
			Assertions.assertTrue(generator.nextInt(range) >= 0);
		}
	}

	@Test
	void shouldGenerateValuesWithMeanEqualsRange() {
		int range = 20;
		int iterations = 200;
		int expectedMean = range;
		List<Integer> generated = new LinkedList<>();

		GaussianRandomGenerator generator = new GaussianRandomGenerator();
		for (int i = 0; i < iterations; ++i) {
			generated.add(generator.nextInt(range));
		}

		Map<Integer, Long> occurrencesMap = generated.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		int mean = occurrencesMap.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0);

		Assertions.assertEquals(expectedMean, mean);
	}

	@Test
	void shouldGenerateValuesWithLeastZeroValues() {
		int range = 10;
		int iterations = 100000;
		List<Integer> generated = new LinkedList<>();

		GaussianRandomGenerator generator = new GaussianRandomGenerator();
		for (int i = 0; i < iterations; ++i) {
			generated.add(generator.nextInt(range));
		}

		Map<Integer, Long> occurrencesMap = generated.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		int leastProbableValue = occurrencesMap.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(range);

		Assertions.assertEquals(0, leastProbableValue);
	}

}
