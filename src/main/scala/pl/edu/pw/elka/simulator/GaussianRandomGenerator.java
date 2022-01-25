package pl.edu.pw.elka.simulator;

import java.util.Optional;
import java.util.Random;

public class GaussianRandomGenerator extends Random {

	private final Integer meanDifference;

	public GaussianRandomGenerator(Integer meanDifference) {
		super();
		this.meanDifference = Optional.ofNullable(meanDifference).orElse(1);
	}

	@Override
	public int nextInt(int carNumbers) {
		int mean = carNumbers - meanDifference;
		double variance = (carNumbers - 1) * (carNumbers - 1) / 9.0;//(carNumbers * carNumbers) / 36.0;
		double standardGaussian = super.nextGaussian();
		final double randomGaussianValue = standardGaussian * variance + mean;
		int finalValue = Math.min((int) randomGaussianValue, carNumbers);
		return Math.max(finalValue, 0);
	}

}
