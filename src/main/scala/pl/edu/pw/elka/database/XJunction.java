package pl.edu.pw.elka.database;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.enums.TrafficLightStates;
import pl.edu.pw.elka.knowledgeDatabase.Junction;

public class XJunction extends Junction {

	public XJunction(List<Lane> lanes, String name) {
		this.lanesInJunction.addAll(lanes);
		this.name = name;
		initializeAllowedStates();
	}

	public String getNextRoad(Coordinate now) {
		int actRoadIdx = Roads.orderedIndexOf(now.getRoad());
		Integer nextRoadIdx = null;
		switch (now.getLane()) {
			case "L":
				nextRoadIdx = (actRoadIdx + 1) % 4;
				break;
			case "P1": //FIXME some error with driving at right - cars rides left
				nextRoadIdx = (actRoadIdx - 1) % 4;
				break;
			case "P2":
				nextRoadIdx = (actRoadIdx + 2) % 4;
				break;
		}
		return Roads.getByOrderedIndex(nextRoadIdx);
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
