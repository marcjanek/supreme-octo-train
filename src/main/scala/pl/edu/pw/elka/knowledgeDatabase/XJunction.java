package pl.edu.pw.elka.knowledgeDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

class XJunction implements Junction{
    HashMap<State, HashSet<State>> states = new HashMap<>();
    XJunction(){
        //        A
        states.put(State.A_L, new HashSet<>(Arrays.asList(State.A_P, State.C_L, State.D_P)));
        states.put(State.A_P, new HashSet<>(Arrays.asList(State.A_L, State.C_P, State.B_L)));
        //        B
        //        todo
        //        C
        //        todo
        //        D
        //        todo
    }
    @Override
    public boolean isAllowed(final State key1, final State key2){
        return states.get(key1).contains(key2);
    }
    @Override
    public HashSet<State> allowedStates(final State key1) {
        return states.get(key1);
    }
}
