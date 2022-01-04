package pl.edu.pw.elka.enums;

public enum TrafficLightStates {
    A_L(Roads.A.state + "_" + Lights.L.state),
    A_P("A_P"),
    B_L("B_L"),
    B_P("B_P"),
    C_L("C_L"),
    C_P("C_P"),
    D_L("D_L"),
    D_P("D_P");
    public final String state;
    TrafficLightStates(final String s){
        this.state = s;
    }

    public int getIndex(){
//        todo:
        return 0;
    }
}
