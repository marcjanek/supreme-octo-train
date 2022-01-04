package pl.edu.pw.elka.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pw.elka.enums.Light;

@Data @AllArgsConstructor
class Lane {
    Light light = Light.RED;
    Long numberOfCars = 99L;
}
