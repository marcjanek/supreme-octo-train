package pl.edu.pw.elka.enums;

public enum Light {
    GREEN(true),
    RED(false);
    public final boolean state;
    Light(final boolean s){
        this.state = s;
    }
}
