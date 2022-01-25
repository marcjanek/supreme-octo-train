package pl.edu.pw.elka.simulator;

import java.util.Random;

/**
 * Override method used by Driver to get random number of passed cars
 * Use gaussian distribution with mean == argument_nextInt
 * and sigma^2 along with 3 sigma rules (ends range at 0)
 *
 * Instead of normal gaussian value all values more than argument_nextInt are not returned, but argument_nextInt is return.
 *
 * So the most possible is get value == argument_nextInt (mean of gaussian distribution + all density of right side of gaussian)
 * Least - 0
 */
public class GaussianRandomGenerator extends Random {

	public GaussianRandomGenerator() {
		super();
	}

	@Override
	public int nextInt(int carNumbers) {
		int mean = carNumbers;
		double variance = (carNumbers * carNumbers) / 36.0;
		double standardGaussian = super.nextGaussian();
		final double randomGaussianValue = standardGaussian * variance + mean;
		int finalValue = Math.min((int) randomGaussianValue, carNumbers);
		return Math.max(finalValue, 0);
	}

}
