package simulator;

import java.util.Random;

class DeterministicGeneratorMock extends Random {

	int nextIntVal = 0;

	DeterministicGeneratorMock(int nextIntVal) {
		this.nextIntVal = nextIntVal;
	}

	@Override
	public int nextInt(int range) {
		if (nextIntVal >= range) {
			return range - 1;
		}
		return nextIntVal;
	}

}