package pl.edu.pw.elka.enums;

public enum Light {
    GREEN(true),
    RED(false);
    public final boolean state;
    Light(final boolean s){
        this.state = s;
    }
    public String getImagePath(){
        if (state == Light.GREEN.state){
            return "src/main/resources/green.png";
        } else {
            return "src/main/resources/red.png";
        }
    }

    public int getValue() {
        return (state == Light.GREEN.state ? 1 : 0);
    }
}
