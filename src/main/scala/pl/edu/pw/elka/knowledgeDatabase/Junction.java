package pl.edu.pw.elka.knowledgeDatabase;

import java.util.HashMap;
import java.util.HashSet;
import pl.edu.pw.elka.enums.TrafficLightStates;

public abstract class Junction {
	protected final HashMap<TrafficLightStates, HashSet<TrafficLightStates>> states = new HashMap<>();

	protected abstract void initializeAllowedStates();

	public Junction() {

	}

	public boolean isAllowed(final TrafficLightStates key1, final TrafficLightStates key2) {
		return states.get(key1).contains(key2);
	}

	public HashSet<TrafficLightStates> allowedStates(final TrafficLightStates key1) {
		return states.get(key1);
	}

}
