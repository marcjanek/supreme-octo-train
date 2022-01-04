package pl.edu.pw.elka.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pw.elka.terrain.TrafficLight;

@Data @AllArgsConstructor
class Lane {
    TrafficLight trafficLight = TrafficLight.RED;
    Long numberOfCars = 99L;
}
