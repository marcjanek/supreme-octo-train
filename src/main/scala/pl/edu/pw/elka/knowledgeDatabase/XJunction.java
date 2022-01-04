package pl.edu.pw.elka.knowledgeDatabase;

import pl.edu.pw.elka.enums.TrafficLightStates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

class XJunction implements Junction{
    HashMap<TrafficLightStates, HashSet<TrafficLightStates>> states = new HashMap<>();
    XJunction(){
        //        A
        states.put(TrafficLightStates.A_L, new HashSet<>(Arrays.asList(TrafficLightStates.A_P, TrafficLightStates.C_L, TrafficLightStates.D_P)));
        states.put(TrafficLightStates.A_P, new HashSet<>(Arrays.asList(TrafficLightStates.A_L, TrafficLightStates.C_P, TrafficLightStates.B_L)));
        //        B
        //        todo
        //        C
        //        todo
        //        D
        //        todo
    }
    @Override
    public boolean isAllowed(final TrafficLightStates key1, final TrafficLightStates key2){
        return states.get(key1).contains(key2);
    }
    @Override
    public HashSet<TrafficLightStates> allowedStates(final TrafficLightStates key1) {
        return states.get(key1);
    }
}
