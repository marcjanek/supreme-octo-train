package pl.edu.pw.elka.terrain;

import lombok.Data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public
class Terrain extends JPanel {

    final private Dimension screenSize;
    final private JFrame frame;
    static Image map;
    static {
        try {
            map = ImageIO.read(new File("src/main/resources/map.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    final Map<String, Junction> junctions = new HashMap<>();

    public Terrain(){
        frame = new JFrame();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(this);
        frame.setResizable(false);
//        frame.repaint();
//        SwingUtilities.updateComponentTreeUI(frame);
        frame.setSize((int) (screenSize.getWidth()/2), (int)(screenSize.getHeight()/2));
        frame.setVisible(true);
    }


    public List<Lane> getLanes(){
        List<Lane> list = new LinkedList<>();
        this.junctions.forEach(
                (k,v)->v.roads.forEach(
                        (k1,v1)->v1.getLanes().forEach(
                                (k2,v2) -> list.add(v2)
                        )
                )
        );
        return list;
    }

    public void addJunction(final Junction junction){
        junctions.put(junction.id, junction);
    }

    @Override
    protected void paintComponent(Graphics graphics){
//
        map = map.getScaledInstance((int) (screenSize.getWidth()/2), (int) (screenSize.getHeight()/2), Image.SCALE_SMOOTH);
        graphics.drawImage(map, 0,0,null);
        for (Lane lane : this.getLanes()) {
            graphics.drawImage(Lane.images.get(lane.getTrafficLight()), lane.centerX,lane.centerY,null);
            graphics.drawString(lane.getNumberOfCars().toString(), lane.centerXText,lane.centerYText);
        }

//       super.paintComponent(graphics);
    }

    public void changeLight(final TrafficLight newState, final String junction, final String road, final String lane){
        this.junctions.get(junction).roads.get(road).setLaneState(lane, newState);
        repaint();
    }
    public void changeCarsNumber(final Long carsNumber, final String junction, final String road, final String lane){
        this.junctions.get(junction).roads.get(road).setLaneState(lane, carsNumber);
        repaint();
    }

}
