package pl.edu.pw.elka.knowledgeDatabase;

import pl.edu.pw.elka.enums.TrafficLightStates;

import java.util.HashSet;

interface Junction {
    boolean isAllowed(final TrafficLightStates key1, final TrafficLightStates key2);
    HashSet<TrafficLightStates> allowedStates(final TrafficLightStates key1);
}
