package pl.edu.pw.elka.terrain;

public class YJunction extends Junction{
    public YJunction(final String id, final Integer centerX, final Integer centerY) {
        super(id, centerX, centerY);
        super.roads.put("A", new Road("A", centerX, centerY));
        super.roads.put("B", new Road("B", centerX, centerY));
        super.roads.put("C", new Road("C", centerX, centerY));
    }
}
