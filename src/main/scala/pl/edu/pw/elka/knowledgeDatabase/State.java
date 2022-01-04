package pl.edu.pw.elka.knowledgeDatabase;

public enum State {
    A_L("A_L"),
    A_P("A_P"),
    B_L("B_L"),
    B_P("B_P"),
    C_L("C_L"),
    C_P("C_P"),
    D_L("D_L"),
    D_P("D_P");
    public final String state;
    State(final String s){
        this.state = s;
    }

}
