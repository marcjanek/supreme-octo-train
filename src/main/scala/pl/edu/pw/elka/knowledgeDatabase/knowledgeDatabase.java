package pl.edu.pw.elka.knowledgeDatabase;

import java.util.HashSet;

public class knowledgeDatabase {
    static XJunction xJunction = new XJunction();
    static YJunction yJunction = new YJunction();
    public HashSet<State> getAllowedStatesForXJunction(final State key1){
        return xJunction.allowedStates(key1);
    }

    public boolean checkIfStateIsAllowedForXJunction(final State key1, final State key2){
        return xJunction.isAllowed(key1, key2);
    }

    public HashSet<State> getAllowedStatesForYJunction(final State key1){
        return yJunction.allowedStates(key1);
    }

    public boolean checkIfStateIsAllowedForYJunction(final State key1, final State key2){
        return yJunction.isAllowed(key1, key2);
    }
}
