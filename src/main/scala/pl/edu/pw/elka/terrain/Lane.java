package pl.edu.pw.elka.terrain;

import lombok.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
class Lane {
    public static final Map<TrafficLight, Image> images;
    static {
        images = new HashMap<>();
        try {
            images.put(TrafficLight.GREEN,  ImageIO.read(new File(TrafficLight.GREEN.state)).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
            images.put(TrafficLight.RED, ImageIO.read(new File(TrafficLight.RED.state)).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final String id;

    TrafficLight trafficLight = TrafficLight.RED;
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
