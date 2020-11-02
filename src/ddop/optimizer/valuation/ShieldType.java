package ddop.optimizer.valuation;

import util.StatTotals;

public class ShieldType {
    public static final ShieldType NONE = new ShieldType("None",    0),
                                    ORB = new ShieldType("Orb",     0),
                                BUCKLER = new ShieldType("Buckler", 0),
                                  SMALL = new ShieldType("Small",   5),
                                  LARGE = new ShieldType("Large",   10),
                                  TOWER = new ShieldType("Tower",   15);
    public static final ShieldType[] all = new ShieldType[] { NONE, ORB, BUCKLER, SMALL, LARGE, TOWER };



    public final String name;
    public final int prrMrr;

    private ShieldType(String name, int prrMrr) {
        this.name = name;
        this.prrMrr = prrMrr;
    }



    public static ShieldType get(String shieldType, String itemName) {
        if(shieldType == null) return ShieldType.NONE;
        switch(shieldType) {

            case "orb":
                return ShieldType.ORB;
            case "buckler":
                return ShieldType.BUCKLER;
            case "small shield":
                return ShieldType.SMALL;
            case "large shield":
                return ShieldType.LARGE;
            case "tower shield":
                return ShieldType.TOWER;
            case "cosmetic orb":
            case "cosmetic shield":
                return ShieldType.NONE;
            default:
                System.err.println("Item: Unrecognised shield type \"" + shieldType + "\" for item \"" + itemName + "\".");
                return null;
        }
    }

    public static ShieldType get(StatTotals stats) {
        if(stats.getBoolean("small shield")) return SMALL;
        if(stats.getBoolean("large shield")) return LARGE;
        if(stats.getBoolean("tower shield")) return TOWER;
        return NONE;
    }
}