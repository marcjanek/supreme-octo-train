package pl.edu.pw.elka.enums;

public enum Lanes {
    L(Lights.L.state),
    P1(Lights.P.state + "1"),
    P2(Lights.P.state + "2");
    public final String state;
    Lanes(final String s){
        this.state = s;
    }
}
