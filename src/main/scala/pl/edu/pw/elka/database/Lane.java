package pl.edu.pw.elka.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pw.elka.enums.Light;

@Data @AllArgsConstructor
public class Lane {
    Coordinate coordinate;
    Light light = Light.RED;
    Long numberOfCars = 99L;
}
