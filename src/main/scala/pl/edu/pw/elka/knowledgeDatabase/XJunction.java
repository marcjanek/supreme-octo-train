package pl.edu.pw.elka.knowledgeDatabase;

import java.util.Arrays;
import java.util.HashSet;
import pl.edu.pw.elka.enums.TrafficLightStates;

class XJunction extends Junction {
	XJunction() {
		initializeAllowedStates();
	}

	@Override
	protected void initializeAllowedStates() {
		// A
		this.states.put(TrafficLightStates.A_L,
				new HashSet<>(Arrays.asList(TrafficLightStates.A_P, TrafficLightStates.C_L, TrafficLightStates.D_P)));
		this.states.put(TrafficLightStates.A_P,
				new HashSet<>(Arrays.asList(TrafficLightStates.A_L, TrafficLightStates.C_P, TrafficLightStates.B_L)));
		// B
		this.states.put(TrafficLightStates.B_L,
				new HashSet<>(Arrays.asList(TrafficLightStates.B_L, TrafficLightStates.D_L, TrafficLightStates.A_P)));
		this.states.put(TrafficLightStates.B_P,
				new HashSet<>(Arrays.asList(TrafficLightStates.B_L, TrafficLightStates.D_P, TrafficLightStates.C_L)));
		// C
		this.states.put(TrafficLightStates.C_L,
				new HashSet<>(Arrays.asList(TrafficLightStates.C_P, TrafficLightStates.A_L, TrafficLightStates.B_P)));
		this.states.put(TrafficLightStates.C_P,
				new HashSet<>(Arrays.asList(TrafficLightStates.C_L, TrafficLightStates.A_P, TrafficLightStates.D_L)));
		// D
		this.states.put(TrafficLightStates.D_L,
				new HashSet<>(Arrays.asList(TrafficLightStates.D_P, TrafficLightStates.B_L, TrafficLightStates.C_P)));
		this.states.put(TrafficLightStates.D_P,
				new HashSet<>(Arrays.asList(TrafficLightStates.D_L, TrafficLightStates.B_P, TrafficLightStates.A_L)));
	}

}
