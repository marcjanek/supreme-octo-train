package pl.edu.pw.elka;

import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.terrain.Terrain;
import pl.edu.pw.elka.terrain.XJunction;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    static Terrain terrain;
    static public Database database;
    public static void main(String []args) {
//        database
        database = new Database();
        database.createXJunction("1");
        database.createXJunction("2");

//        terrain config
        terrain = new Terrain();
        terrain.addJunction(new XJunction("1", 775,130));
        terrain.addJunction(new XJunction("2", 775, 130*3+12));
//        config database with visualization
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Lanes lane : Lanes.values()) {
                    //                xJunctions
                    for (int junction=1;junction<=2;++junction){
                        for (Roads road : Roads.values()) {
                            final Long carsNumber = database.getCarsNumber(String.valueOf(junction), road, lane);
                            terrain.changeCarsNumber(carsNumber, String.valueOf(junction), road.state, lane.state);
//                            light
                            final Light light = database.getTrafficLight(String.valueOf(junction), road, lane);
                            terrain.changeLight(light, String.valueOf(junction), road.state, lane.state);
                        }
                    }
                    //                yJunctions
                }
            }
        }, 0, 1000); // Create Repetitively task for every 1 secs
//        start generator
//        new Timer().schedule(new Generator, 0, 1000);

//        Akka
        pl.edu.pw.elka.akka.Main.main();
    }
}
