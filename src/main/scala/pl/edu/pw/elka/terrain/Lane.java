package pl.edu.pw.elka.terrain;

import lombok.*;
import pl.edu.pw.elka.enums.Light;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
class Lane {
    public static final Map<Light, Image> images;
    static {
        images = new HashMap<>();
        try {
            images.put(Light.GREEN,  ImageIO.read(new File(Light.GREEN.getImagePath())).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
            images.put(Light.RED, ImageIO.read(new File(Light.RED.getImagePath())).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final String id;

    Light light = Light.RED;
    Long numberOfCars = 99L;

    final Integer centerX;
    final Integer centerY;

    final Integer centerXText;
    final Integer centerYText;

    @Override
    public int hashCode() {
        return Integer.parseInt(id);
    }
}
