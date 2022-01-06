package pl.edu.pw.elka.drawer;

import java.util.Set;
import java.util.TimerTask;
import pl.edu.pw.elka.database.Coordinate;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.terrain.Terrain;

public class TerrainDrawer extends TimerTask {

	private final Database database;
	private final Terrain terrain;

	public TerrainDrawer(Database database, Terrain terrain) {
		this.database = database;
		this.terrain = terrain;
	}

	@Override
	public void run() {
		Set<Coordinate> trafficPointCoordinates = database.getLaneCoordinates();
		for (Coordinate c: trafficPointCoordinates){
			final Long carsNumber = database.getCarsNumber(c);
			terrain.changeCarsNumber(carsNumber, c);

			final Light light = database.getTrafficLight(c);
			terrain.changeLight(light, c);
		}
	}

}
