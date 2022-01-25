package pl.edu.pw.elka.database;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.enums.TrafficLightStates;
import pl.edu.pw.elka.knowledgeDatabase.Junction;

public class YJunction extends Junction {

	public YJunction() {
		super();
		initializeAllowedStates();
	}

	@Override
	public String getNextRoad(Coordinate now) {
		Integer nextRoadIdx = null;
		switch (now.getRoad()) {
			case "A":
				if (Objects.equals(now.getLane(), "P1")) {
					nextRoadIdx = 2; //C
				} else {
					nextRoadIdx = 1; //B
				}
				break;
			case "B":
				if (Objects.equals(now.getLane(), "L")) {
					nextRoadIdx = 2; //C
				} else {
					nextRoadIdx = 0; //A
				}
				break;
			case "C":
				switch (now.getLane()) {
					case "L":
						nextRoadIdx = 0; //A
						break;
					case "P1":
						nextRoadIdx = 1; //B
						break;
					case "P2":
						nextRoadIdx = 0; //A - middle lane is for left riders
						break;
				}
				break;
			default:
				throw new RuntimeException("Not allowed road name");
		}
		return Roads.getByOrderedIndex(nextRoadIdx);
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
