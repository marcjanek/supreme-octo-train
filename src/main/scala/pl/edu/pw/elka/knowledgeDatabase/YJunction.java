package pl.edu.pw.elka.knowledgeDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import pl.edu.pw.elka.enums.TrafficLightStates;

class YJunction extends Junction {
	public YJunction() {
		super();
	}

	@Override
	protected void initializeAllowedStates() {
		// A
		this.states.put(TrafficLightStates.A_P, new HashSet<>(Collections.singletonList(TrafficLightStates.B_P)));
		// B
		this.states.put(TrafficLightStates.B_L, new HashSet<>(Arrays.asList(TrafficLightStates.C_P, TrafficLightStates.B_P)));
		this.states.put(TrafficLightStates.B_P,
				new HashSet<>(Arrays.asList(TrafficLightStates.A_P, TrafficLightStates.C_P, TrafficLightStates.B_L)));
		// C
		this.states.put(TrafficLightStates.C_L, new HashSet<>(Collections.singletonList(TrafficLightStates.C_P)));
		this.states.put(TrafficLightStates.C_P,
				new HashSet<>(Arrays.asList(TrafficLightStates.C_L, TrafficLightStates.B_P, TrafficLightStates.B_L)));
	}
}
