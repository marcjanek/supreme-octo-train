package pl.edu.pw.elka.simulator;

import java.util.Random;

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
