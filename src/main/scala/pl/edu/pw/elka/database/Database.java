package pl.edu.pw.elka.database;

import pl.edu.pw.elka.enums.Lanes;
import pl.edu.pw.elka.enums.Light;
import pl.edu.pw.elka.enums.Lights;
import pl.edu.pw.elka.enums.Roads;

import java.util.HashMap;

public final class Database {
    private final HashMap<String, Lane> hashMap = new HashMap<>();

    public synchronized Light getTrafficLight(final String junction, final Roads road, final Lanes lane){
        return getLane(hashmapKey(junction, road, lane)).getLight();
    }
    public synchronized Long getCarsNumber(final String junction, final Roads road, final Lanes lane){
        return getLane(hashmapKey(junction, road, lane)).numberOfCars;
    }
    public synchronized void setTrafficLight(final String junction, final Roads road, final Lights lights, final Light light){
        if(lights.state.equals(Lights.L.state)){
            getLane(hashmapKey(junction, road, Lanes.L)).light = light;
        } else {
            getLane(hashmapKey(junction, road, Lanes.P1)).light = light;
            getLane(hashmapKey(junction, road, Lanes.P2)).light = light;
        }
    }
    public synchronized void setCarsNumber(final String junction, final Roads road, final Lanes lane, final Long carsNumber){
        getLane(hashmapKey(junction, road, lane)).numberOfCars = carsNumber;
    }
    public synchronized void createLane(final String junction, final Roads road, final Lanes lane, final Light light, final Long carsNumber){
        hashMap.put(hashmapKey(junction, road, lane), new Lane(light, carsNumber));
    }
//    create with default values: RED and 0 cars
    public synchronized void createLane(final String junction, final Roads road, final Lanes lane){
        createLane(junction, road, lane, Light.RED, 0L);
    }
    public void createXJunction(final String junction){
        createLane(junction, Roads.A, Lanes.L);
        createLane(junction, Roads.A, Lanes.P1);
        createLane(junction, Roads.A, Lanes.P2);
        createLane(junction, Roads.B, Lanes.L);
        createLane(junction, Roads.B, Lanes.P1);
        createLane(junction, Roads.B, Lanes.P2);
        createLane(junction, Roads.C, Lanes.L);
        createLane(junction, Roads.C, Lanes.P1);
        createLane(junction, Roads.C, Lanes.P2);
        createLane(junction, Roads.D, Lanes.L);
        createLane(junction, Roads.D, Lanes.P1);
        createLane(junction, Roads.D, Lanes.P2);
    }


    private String hashmapKey(final String junction, final Roads road, final Lanes lane){
        return junction + road.state + lane.state;
    }
    private Lane getLane(final String key){
        return this.hashMap.get(key);
    }
}
