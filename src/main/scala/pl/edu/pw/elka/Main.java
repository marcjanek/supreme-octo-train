package pl.edu.pw.elka;

import java.util.Timer;
import java.util.TimerTask;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.terrain.Terrain;
import pl.edu.pw.elka.terrain.XJunction;

public class Main {

	static Terrain terrain;
	static public Database database;

	public static void main(String[] args) {
		//        database
		database = new Database();
		database.createXJunction("1");
		database.createXJunction("2");

		//        terrain config
		terrain = new Terrain();
		terrain.addJunction(new XJunction("1", 775, 130));
		terrain.addJunction(new XJunction("2", 775, 130 * 3 + 12));
		//        config database with visualization
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				for (int lane = 0; lane < 3; ++lane) {
					//                xJunctions
					for (int junction = 1; junction <= 2; ++junction) {
						for (int road = 0; road < 4; ++road) {
							//                            cars number
							final Long carsNumber = database.getCarsNumber(
									new Coordinate(String.valueOf(junction), Roads.getByIndex(road), Lanes.getByIndex(lane)));
							terrain.changeCarsNumber(carsNumber, String.valueOf(junction), Roads.getByIndex(road), Lanes.getByIndex(lane));
							//                            light
							final Light light = database.getTrafficLight(
									new Coordinate(String.valueOf(junction), Roads.getByIndex(road), Lanes.getByIndex(lane)));
							terrain.changeLight(light, String.valueOf(junction), Roads.getByIndex(road), Lanes.getByIndex(lane));
						}
					}
					//                yJunctions

				}
			}
		}, 0, 1000); // Create Repetitively task for every 1 secs
		//        Akka
		pl.edu.pw.elka.akka.Main.main();
	}

}
