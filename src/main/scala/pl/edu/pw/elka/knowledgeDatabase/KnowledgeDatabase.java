package pl.edu.pw.elka.knowledgeDatabase;

import java.util.ArrayList;
import pl.edu.pw.elka.database.XJunction;
import pl.edu.pw.elka.database.YJunction;
import pl.edu.pw.elka.enums.TrafficLightStates;

import java.util.HashSet;

public class KnowledgeDatabase {
    static XJunction xJunction = new XJunction(new ArrayList<>(), null);
    static YJunction yJunction = new YJunction();
    public HashSet<TrafficLightStates> getAllowedStatesForXJunction(final TrafficLightStates key1){
        return xJunction.allowedStates(key1);
    }

    public boolean checkIfStateIsAllowedForXJunction(final TrafficLightStates key1, final TrafficLightStates key2){
        return xJunction.isAllowed(key1, key2);
    }

    public HashSet<TrafficLightStates> getAllowedStatesForYJunction(final TrafficLightStates key1){
        return yJunction.allowedStates(key1);
    }

    public boolean checkIfStateIsAllowedForYJunction(final TrafficLightStates key1, final TrafficLightStates key2){
        return yJunction.isAllowed(key1, key2);
    }

    public static void main(String[] args){
        KnowledgeDatabase knowledgeDatabase = new KnowledgeDatabase();
        HashSet<TrafficLightStates> allowedStatesForXJunction = knowledgeDatabase.getAllowedStatesForXJunction(TrafficLightStates.A_L);
    }
}
