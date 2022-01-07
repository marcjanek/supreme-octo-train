package pl.edu.pw.elka.database;

import java.util.TimerTask;

public class Driver extends TimerTask {
	private Database databaseRef;

	public Driver(Database databaseRef){
		this.databaseRef = databaseRef;
	}

	@Override
	public void run() {

	}
}
