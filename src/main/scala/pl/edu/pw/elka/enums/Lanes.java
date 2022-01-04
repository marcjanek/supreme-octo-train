package pl.edu.pw.elka.enums;

public enum Lanes {
    L(Lights.L.state),
    P1(Lights.P.state + "1"),
    P2(Lights.P.state + "2");
    public final String state;
    Lanes(final String s){
        this.state = s;
    }

    public static String getByIndex(int lane) {
        switch (lane){
            case 0:
                return Lanes.L.state;
            case 1:
                return Lanes.P1.state;
            case 2:
                return Lanes.P2.state;
            default:
                return "";
        }
    }
}
