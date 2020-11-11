package ddop.optimizer.valuation;

import util.StatTotals;

public class ArmorType {
    public static final ArmorType NONE = new ArmorType("None",   null,                          0.0, 50),
                                 CLOTH = new ArmorType("Cloth",  null,                          0.0, 50),
                                 LIGHT = new ArmorType("Light",  "light armor proficiency",     1.0, 100),
                                MEDIUM = new ArmorType("Medium", "medium armor proficiency",    1.5, null),
                                 HEAVY = new ArmorType("Heavy",  "heavy armor proficiency",     2.0, null),
                                DOCENT = new ArmorType("Docent", null,                          0.0, 50); // TODO
    public static final ArmorType[] all = new ArmorType[] { NONE, CLOTH, LIGHT, MEDIUM, HEAVY, DOCENT };



    public final String name, proficiency;
    private final double BABPRRProgression;
    public final Integer MRRCap;
    
    private ArmorType(String name, String proficiency, double BABPRRProgression, Integer MRRCap) {
        this.name = name;
        this.proficiency = proficiency;
        this.BABPRRProgression = BABPRRProgression;
        this.MRRCap = MRRCap;
    }



    public String getRequiredProficiency() { return this.proficiency; }

    public int getPRRAtBAB(int bab) {
        return (int) (bab * this.BABPRRProgression);
    }
    
    public boolean hasMRRCap() { return this.MRRCap != null; }



    public static ArmorType get(String armorType, String itemName) {
        if(armorType == null) return ArmorType.NONE;
        switch(armorType) {
            case "clothing":
            case "outfit":
            case "robe":
                return ArmorType.CLOTH;
            case "light armor":
            case "leather":
            case "leather armor":
                return ArmorType.LIGHT;
            case "medium armor":
            case "hide":
            case "scalemail":
            case "breastplate":
            case "chainmail":
                return ArmorType.MEDIUM;
            case "heavy armor":
            case "full plate":
            case "banded mail":
                return ArmorType.HEAVY;
            case "docent":
                return ArmorType.DOCENT;
            case "cosmetic armor":
                return ArmorType.NONE;
            default:
                System.err.println("Item: Unrecognised armor type \"" + armorType + "\" for item \"" + itemName + "\".");
                return null;
        }
    }

    public static ArmorType get(StatTotals stats) {
        if(stats.getBoolean("docent")) return DOCENT;
        if(stats.getBoolean("heavy armor")) return HEAVY;
        if(stats.getBoolean("medium armor")) return MEDIUM;
        if(stats.getBoolean("light armor")) return LIGHT;
        return NONE;
    }
}