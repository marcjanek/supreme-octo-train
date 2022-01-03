package pl.edu.pw.elka.terrain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Junction {
    final String id;
    final Map<String, Road> roads = new HashMap<>();
    final Integer centerX;
    final Integer centerY;
    final Integer SIZE1 = 65, SIZE2 = 70;
    Road getRoad(final String id){
        return roads.get(id);
    }
}
