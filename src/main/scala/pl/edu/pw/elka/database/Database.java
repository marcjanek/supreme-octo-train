package pl.edu.pw.elka.database;

import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Roads;

import java.util.HashMap;

public final class Database {
    private final HashMap<String, Lane> hashMap = new HashMap<>();

    public synchronized Light getTrafficLight(final String junction, final String road, final String lane){
        return getLane(hashmapKey(junction, road, lane)).getLight();
    }
    public synchronized Long getCarsNumber(final String junction, final String road, final String lane){
        return getLane(hashmapKey(junction, road, lane)).numberOfCars;
    }
    public synchronized void setTrafficLight(final String junction, final String road, final String lane, final Light light){
        getLane(hashmapKey(junction, road, lane)).light = light;
    }
    public synchronized void setCarsNumber(final String junction, final String road, final String lane, final Long carsNumber){
        getLane(hashmapKey(junction, road, lane)).numberOfCars = carsNumber;
    }
    public synchronized void createLane(final String junction, final String road, final String lane, final Light light, final Long carsNumber){
        hashMap.put(hashmapKey(junction, road, lane), new Lane(light, carsNumber));
    }
//    create with default values: RED and 0 cars
    public synchronized void createLane(final String junction, final String road, final String lane){
        createLane(junction, road, lane, Light.RED, 0L);
    }
    public void createXJunction(final String junction){
        createLane(junction, Roads.A.state, Lanes.L.state);
        createLane(junction, Roads.A.state, Lanes.P1.state);
        createLane(junction, Roads.A.state, Lanes.P2.state);
        createLane(junction, Roads.B.state, Lanes.L.state);
        createLane(junction, Roads.B.state, Lanes.P1.state);
        createLane(junction, Roads.B.state, Lanes.P2.state);
        createLane(junction, Roads.C.state, Lanes.L.state);
        createLane(junction, Roads.C.state, Lanes.P1.state);
        createLane(junction, Roads.C.state, Lanes.P2.state);
        createLane(junction, Roads.D.state, Lanes.L.state);
        createLane(junction, Roads.D.state, Lanes.P1.state);
        createLane(junction, Roads.D.state, Lanes.P2.state);
    }


    private String hashmapKey(final String junction, final String road, final String lane){
        return junction + road + lane;
    }
    private Lane getLane(final String key){
        return this.hashMap.get(key);
    }
}
