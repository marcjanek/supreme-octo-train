package pl.edu.pw.elka.terrain;

public enum TrafficLight {

    RED("src/main/resources/red.png"),
    GREEN("src/main/resources/green.png");

    final String state;
    TrafficLight(final String s){
        this.state = s;
    }
}
