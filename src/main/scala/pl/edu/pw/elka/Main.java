package pl.edu.pw.elka;

import java.util.Random;
import java.util.Timer;
import pl.edu.pw.elka.database.Database;
import pl.edu.pw.elka.drawer.TerrainDrawer;
import pl.edu.pw.elka.enums.Roads;
import pl.edu.pw.elka.knowledgeDatabase.Junction;
import pl.edu.pw.elka.simulator.CarGenerator;
import pl.edu.pw.elka.simulator.Driver;
import pl.edu.pw.elka.simulator.GaussianRandomGenerator;
import pl.edu.pw.elka.terrain.Terrain;
import pl.edu.pw.elka.terrain.XJunction;

public class Main {

	static Terrain terrain;
	static public Database database;

	public static void main(String[] args) {
		//        database
		database = new Database();
		Junction x1 = database.createXJunction("1");
		Junction x2 = database.createXJunction("2");
		Junction x3 = database.createXJunction("3");
		Junction x4 = database.createXJunction("4");

		database.match(x1, x2, Roads.B, Roads.D);
		database.match(x2, x1, Roads.D, Roads.B);

		database.match(x1, x3, Roads.C, Roads.A);
		database.match(x3, x1, Roads.A, Roads.C);

		database.match(x3, x4, Roads.B, Roads.D);
		database.match(x4, x3, Roads.D, Roads.B);

		database.match(x2, x4, Roads.C, Roads.A);
		database.match(x4, x2, Roads.A, Roads.C);

		//        terrain config
		terrain = new Terrain();
		terrain.addJunction(new XJunction("1", 775, 130));
		terrain.addJunction(new XJunction("2", 775, 130 * 3 + 12));
		terrain.addJunction(new XJunction("3", 173, 130));
		terrain.addJunction(new XJunction("4", 173, 130 * 3 + 12));
		//        config database with visualization
		new Timer().schedule(new TerrainDrawer(database, terrain), 0, 1000); // Create Repetitively task for every 1 secs
		//        Akka
		new Timer().schedule(new CarGenerator(database, 10, new Random(), new Random()), 0, 1000);

		new Timer().schedule(new Driver(database, new Random(), new GaussianRandomGenerator()), 0, 2000);

		pl.edu.pw.elka.akka.Main.main();
	}

}
