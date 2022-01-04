package pl.edu.pw.elka.knowledgeDatabase;

import java.util.HashSet;

class YJunction implements Junction{
    @Override
    public boolean isAllowed(State key1, State key2) {
        return false;
    }

    @Override
    public HashSet<State> allowedStates(State key1) {
        return null;
    }
}
