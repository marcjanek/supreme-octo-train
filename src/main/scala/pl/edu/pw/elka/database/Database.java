package pl.edu.pw.elka.database;

import pl.edu.pw.elka.terrain.TrafficLight;

import java.util.HashMap;

public final class Database {
    private final HashMap<String, Lane> hashMap = new HashMap<>();

    public synchronized TrafficLight getTrafficLight(final String junction, final String road, final String lane){
        return getLane(hashmapKey(junction, road, lane)).getTrafficLight();
    }
    public synchronized Long getCarsNumber(final String junction, final String road, final String lane){
        return getLane(hashmapKey(junction, road, lane)).numberOfCars;
    }
    public synchronized void setTrafficLight(final String junction, final String road, final String lane, final TrafficLight trafficLight){
        getLane(hashmapKey(junction, road, lane)).trafficLight = trafficLight;
    }
    public synchronized void setCarsNumber(final String junction, final String road, final String lane, final Long carsNumber){
        getLane(hashmapKey(junction, road, lane)).numberOfCars = carsNumber;
    }
    public synchronized void createLane(final String junction, final String road, final String lane, final TrafficLight trafficLight, final Long carsNumber){
        hashMap.put(hashmapKey(junction, road, lane), new Lane(trafficLight, carsNumber));
    }
//    create with default values: RED and 0 cars
    public synchronized void createLane(final String junction, final String road, final String lane){
        createLane(junction, road, lane, TrafficLight.RED, 0L);
    }
    private String hashmapKey(final String junction, final String road, final String lane){
        return junction + road + lane;
    }
    private Lane getLane(final String key){
        return this.hashMap.get(key);
    }
}
