package pl.edu.pw.elka.knowledgeDatabase;

import pl.edu.pw.elka.enums.TrafficLightStates;

import java.util.HashSet;

class YJunction implements Junction{
    @Override
    public boolean isAllowed(TrafficLightStates key1, TrafficLightStates key2) {
        return false;
    }

    @Override
    public HashSet<TrafficLightStates> allowedStates(TrafficLightStates key1) {
        return null;
    }
}
