package pl.edu.pw.elka.knowledgeDatabase;

import java.util.HashSet;

interface Junction {
    boolean isAllowed(final State key1, final State key2);
    HashSet<State> allowedStates(final State key1);
}
