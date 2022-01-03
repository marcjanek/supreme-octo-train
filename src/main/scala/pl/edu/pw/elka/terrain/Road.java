package pl.edu.pw.elka.terrain;

import java.util.HashMap;
import java.util.Map;

class Road {
    private Map<String, Lane> lanes;
    final private String id;

    final Integer centerX;
    final Integer centerY;

    Road(final String roadId, final Integer centerX, final Integer centerY){
        this.id = roadId;
        this.centerX = centerX;
        this.centerY = centerY;
        lanes = new HashMap<>();
        final Integer textPading = 20;
        switch (roadId){
            case "A":
                lanes.put("L", new Lane("L", centerX, centerY+14, centerX - textPading, centerY+22));
                lanes.put("P1", new Lane("P1", centerX, centerY, centerX - textPading, centerY + 8));
                lanes.put("P2", new Lane("P2", centerX, centerY-12, centerX - textPading, centerY-12 + 8));
                break;
            case "B":
                lanes.put("L", new Lane("L", centerX-16, centerY, centerX-16, centerY - textPading + 17));
                lanes.put("P1", new Lane("P1", centerX, centerY, centerX, centerY - textPading+5));
                lanes.put("P2", new Lane("P2", centerX+16, centerY, centerX+16 - 3, centerY - textPading+17));
                break;
            case "C":
                lanes.put("L", new Lane("L", centerX, centerY-14, centerX + textPading - 4, centerY-14+8));
                lanes.put("P1", new Lane("P1", centerX, centerY, centerX + textPading - 4, centerY+8));
                lanes.put("P2", new Lane("P2", centerX, centerY+12, centerX + textPading - 4, centerY+12 + 8));
                break;
            case "D":
                lanes.put("L", new Lane("L", centerX + 16, centerY, centerX + 16, centerY + textPading));
                lanes.put("P1", new Lane("P1", centerX, centerY, centerX, centerY + textPading + 12));
                lanes.put("P2", new Lane("P2", centerX - 16, centerY, centerX - 16, centerY + textPading));
                break;
            default:
                System.out.println(roadId);
        }
    }

    public Map<String, Lane> getLanes() {
        return this.lanes;
    }
    public void setLaneState(final String lane, final Long carsNumber){
        Lane p1 = this.lanes.get(lane);
        p1.numberOfCars = carsNumber;
    }
    public void setLaneState(final String lane, final TrafficLight newState){
        if(lane.equals("P2")){
            Lane p1 = this.lanes.get("P1");
            p1.trafficLight = newState;
        } else if(lane.equals("P1")){
            Lane p1 = this.lanes.get("P2");
            p1.trafficLight = newState;
        }
        Lane p1 = this.lanes.get(lane);
        p1.trafficLight = newState;
    }
}
