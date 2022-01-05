package pl.edu.pw.elka.enums;

public enum Roads {
    A("A"),B("B"),C("C"),D("D");
    public final String state;
    Roads(final String s){
        this.state = s;
    }

    public static String getByIndex(int road) {
        switch (road) {
            case 0:
                return Roads.A.state;
            case 1:
                return Roads.B.state;
            case 2:
                return Roads.C.state;
            case 3:
                return Roads.D.state;
            default:
                return "";
        }
    }
}
