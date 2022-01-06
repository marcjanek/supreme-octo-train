package pl.edu.pw.elka.enums;

public enum Roads {
    A("A"),B("B"),C("C"),D("D");
    public final String state;
    Roads(final String s){
        this.state = s;
    }

    public static Roads getByIndex(int road) {
        switch (road) {
            case 1:
                return Roads.A;
            case 0:
                return Roads.B;
            case 2:
                return Roads.C;
            case 3:
                return Roads.D;
            default:
                return null;
        }
    }

    public int getIndex() {
        if (state == Roads.A.state) {
            return 1; // Reverted values of A and B to sum up opposite roads to 3
        } else if (state == Roads.B.state) {
            return 0;
        } else if (state == Roads.C.state) {
            return 2;
        } else if (state == Roads.D.state) {
            return 3;
        } else {
            return -1;
        }
    }
}
