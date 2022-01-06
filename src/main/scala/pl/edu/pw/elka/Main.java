package pl.edu.pw.elka;

import java.util.Timer;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.drawer.TerrainDrawer;
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
		new Timer().schedule(new TerrainDrawer(database, terrain), 0, 1000); // Create Repetitively task for every 1 secs
		//        Akka
		pl.edu.pw.elka.akka.Main.main();
	}

}
