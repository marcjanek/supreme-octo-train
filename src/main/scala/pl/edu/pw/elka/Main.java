package pl.edu.pw.elka;

import pl.edu.pw.elka.terrain.Terrain;
import pl.edu.pw.elka.terrain.TrafficLight;
import pl.edu.pw.elka.terrain.XJunction;

public class Main {
    public static void main(String []args) {
        Terrain terrain = new Terrain();

        terrain.addJunction(new XJunction("1", 775,130));
        terrain.addJunction(new XJunction("2", 775, 130*3+12));

        terrain.changeLight(TrafficLight.GREEN, "1", "A", "P1");
        terrain.changeCarsNumber(11L, "1", "A", "P1");
    }
}
