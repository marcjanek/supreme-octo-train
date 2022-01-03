package pl.edu.pw.elka.terrain;

public class XJunction extends Junction{
    public XJunction(final String id, final Integer centerX, final Integer centerY) {
        super(id, centerX, centerY);
        super.roads.put("A", new Road("A", centerX + 65, centerY - 26));
        super.roads.put("B", new Road("B", centerX + 32, centerY + 48));
        super.roads.put("C", new Road("C", centerX - 65, centerY + 26));
        super.roads.put("D", new Road("D", centerX - 32, centerY - 48));
    }
}
