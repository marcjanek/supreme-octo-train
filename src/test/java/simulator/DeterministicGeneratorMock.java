package simulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;

class DeterministicGeneratorMock extends Random {

	int defaultIntVal = 0;
	Queue<Integer> nextIntValues;

	DeterministicGeneratorMock(int nextIntVal, List<Integer> expectedIntValues) {
		this.defaultIntVal = nextIntVal;
		this.nextIntValues = new LinkedList<>(expectedIntValues);
	}

	void addValues(List<Integer> expectedIntValues) {
		this.nextIntValues = new LinkedList<>(expectedIntValues);
	}

	@Override
	public int nextInt(int range) {
		return Optional.ofNullable(nextIntValues.poll()).orElse(defaultIntVal);
	}

}