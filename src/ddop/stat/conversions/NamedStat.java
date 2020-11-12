package ddop.stat.conversions;

import ddop.stat.Stat;

import java.util.*;

public class NamedStat {
    private static final Map<String, NamedStat> all = new HashMap<>();

    private final List<Stat> conversion = new ArrayList<>();

    private NamedStat(String name) {
        if(name != null && name.length() > 0) NamedStat.all.put(name, this);
    }

    private NamedStat addBonus(String category) { return this.addBonus(new Stat(category)); }
    private NamedStat addBonus(String category, double magnitude) { return this.addBonus(new Stat(category, magnitude)); }
    private NamedStat addBonus(String category, String bonusType, double magnitude) { return this.addBonus(new Stat(category, bonusType, magnitude)); }
    private NamedStat addBonus(Stat s) {
        if(s != null) this.conversion.add(s);
        return this;
    }

    public static List<Stat> convertAll(List<Stat> toConvert) {
        List<Stat> converted = new ArrayList<>();

        for(Stat s : toConvert) converted.addAll(convert(s));

        return converted;
    }

    /** Returns a List of elementary stats into which the provided compound stat decomposes.
     *
     * @param toConvert - The stat to convert.
     * @return Stat[].
     */
    public static List<Stat> convert(Stat toConvert) {
        NamedStat ns = NamedStat.all.get(toConvert.category);
        if(ns == null) {
            List<Stat> ret = new ArrayList<>();
            ret.add(toConvert);
            return ret;
        }

        List<Stat> ret = new ArrayList<>(ns.conversion.size());
        for(Stat template : ns.conversion) {
            Stat conversion = NamedStat.applyTemplate(toConvert, template);

            List<Stat> fullyConverted = convert(conversion);
            ret.addAll(fullyConverted);
        }

        return ret;
    }

    public static boolean isNamed(Stat s) { return NamedStat.isNamed(s.category); }
    public static boolean isNamed(String s) {
        return NamedStat.all.containsKey(s);
    }

    /** Returns a Stat instance for the given source stat and conversion template.
     * Uses the template's category.
     * Uses the template's bonus type, if available, otherwise the source's bonus type.
     * Uses the template's magnitude, if non-zero, otherwise the source's magnitude.
     *
     * @param source The original stat which triggered this conversion.
     * @param template The template stat provided in the NamedStat definition.
     * @return new Stat
     */
    private static Stat applyTemplate(Stat source, Stat template) {
        String bonusType = template.bonusType;
        if(bonusType == null) bonusType = source.bonusType;

        double magnitude = template.magnitude;
        if(magnitude == 0) magnitude = source.magnitude;

        return new Stat(template.category, bonusType, magnitude);
    }

    static {
        new NamedStat("hit points")         .addBonus("hp",                  null,       0);
        new NamedStat("false life")         .addBonus("hp",                  null,       0);
        new NamedStat("lifeforce")          .addBonus("hp",                  null,       0);
        new NamedStat("vitality")           .addBonus("hp",                  "vitality", 0);
        new NamedStat("lesser false life")  .addBonus("hp",                  null,       5);
        new NamedStat("improved false life").addBonus("hp",                  null,       20);
        new NamedStat("greater false life") .addBonus("hp",                  null,       30);
        new NamedStat("superior false life").addBonus("hp",                  null,       40);
        new NamedStat("epic false life")    .addBonus("hp",                  null,       45);
        new NamedStat("elemental energy")         .addBonus("hp", "elemental energy",          10);
        new NamedStat("improved elemental energy").addBonus("hp", "improved elemental energy", 15);
        new NamedStat("greater elemental energy") .addBonus("hp", "greater elemental energy",  20);
        new NamedStat("don't count me out!").addBonus("unconsciousness range","enhancement", 400);
        new NamedStat("strength of purpose").addBonus("unconsciousness range","enhancement", 128)
                                            .addBonus("regeneration", 		  "enhancement",  16);
        new NamedStat("sheltering")         .addBonus("prr", null, 0)
                                            .addBonus("mrr", null, 0);
        new NamedStat("physical sheltering").addBonus("prr", null, 0);
        new NamedStat("magical sheltering") .addBonus("mrr", null, 0);
        new NamedStat("magical resistance:").addBonus("mrr", null, 0);
        new NamedStat("fortification (+50%)").addBonus("fortification", null, 50);
        new NamedStat("blurry")             .addBonus("concealment",         "default",  20);
        new NamedStat("smoke screen")       .addBonus("concealment",         "default",  20);
        new NamedStat("lesser displacement").addBonus("concealment",         "default",  25);
        new NamedStat("combat mastery")     .addBonus("stunning",            null,       0)
                                            .addBonus("vertigo",             null,       0)
                                            .addBonus("shatter",             null,       0);
        new NamedStat("natural armor")      .addBonus("ac", "natural armor", 0);
        new NamedStat("natural armor bonus").addBonus("ac", "natural armor", 0);
        new NamedStat("protection")         .addBonus("ac", "deflection",    0);
        new NamedStat("template:protection").addBonus("ac", "deflection",    0);
        new NamedStat("hardened exterior")  .addBonus("ac", "profane",       0);
        new NamedStat("heightened awareness (10)").addBonus("ac", "insight", 10);
        new NamedStat("greater reinforced fists") .addBonus("reinforced fists", "default", 2);
        new NamedStat("superior reinforced fists").addBonus("reinforced fists", "default", 3);
        new NamedStat("well rounded").addBonus("strength",     null, 0)
                                     .addBonus("dexterity",    null, 0)
                                     .addBonus("constitution", null, 0)
                                     .addBonus("intelligence", null, 0)
                                     .addBonus("wisdom",       null, 0)
                                     .addBonus("charisma",     null, 0);
        new NamedStat("blood rage").addBonus("strength",     "blood rage", 4)
                                   .addBonus("constitution", "blood rage", 4);
        new NamedStat("litany of the dead ability bonus").addBonus("well rounded", "profane", 1);
        new NamedStat("litany of the dead ii - ability bonus").addBonus("well rounded", "profane", 2);
        new NamedStat("litany of the dead combat bonus") .addBonus("accuracy",	  "profane", 1)
                                                         .addBonus("deadly",	      "profane", 1);
        new NamedStat("litany of the dead ii - combat bonus")	.addBonus("accuracy",	"profane", 4)
                                                                .addBonus("deadly",	"profane", 4);
        new NamedStat("attack bonus").addBonus("accuracy", null, 0);
        new NamedStat("ghostly")                  .addBonus("incorporeal",   "default", 10)
                                                  .addBonus("ghost touch")
                                                  .addBonus("hide",          "enhancement", 5)
                                                  .addBonus("move silently", "enhancement", 5);
        new NamedStat("ethereal").addBonus("ghost touch");
        new NamedStat("resistance").addBonus("fortitude saves", null,      0)
                                   .addBonus("reflex saves",    null,      0)
                                   .addBonus("will saves",      null,      0);
        new NamedStat("parrying")  .addBonus("fortitude saves", "insight", 0)
                                   .addBonus("reflex saves",    "insight", 0)
                                   .addBonus("will saves",      "insight", 0)
                                   .addBonus("ac",              "insight", 0);
        new NamedStat("riposte")   .addBonus("fortitude saves", "insight", 0)
                                   .addBonus("reflex saves",    "insight", 0)
                                   .addBonus("will saves",      "insight", 0)
                                   .addBonus("ac",              "insight", 0);
        new NamedStat("heroism")        .addBonus("accuracy",		"morale", 2)
                                        .addBonus("resistance",	"morale", 2)
                                        .addBonus("all skills",	"morale", 2);
        new NamedStat("improved heroism").addBonus("accuracy",		"morale", 3)
                                        .addBonus("resistance",	"morale", 3)
                                        .addBonus("all skills",	"morale", 3);
        new NamedStat("greater heroism").addBonus("accuracy",		"morale", 4)
                                        .addBonus("resistance",	"morale", 4)
                                        .addBonus("all skills",	"morale", 4);
        new NamedStat("good luck").addBonus("resistance", "luck", 0)
                                  .addBonus("all skills", "luck", 0);
        new NamedStat("fortitude save").addBonus("fortitude saves", null, 0);
        new NamedStat("reflex save")   .addBonus("reflex saves",    null, 0);
        new NamedStat("will save")     .addBonus("will saves",      null, 0);
        new NamedStat("spell save")    .addBonus("spell saves",     null, 0);
        new NamedStat("seeker").addBonus("critical confirmation",	null, 0)
                               .addBonus("critical damage",		null, 0);
        new NamedStat("all skills") .addBonus("balance",			null, 0)
                                    .addBonus("bluff",				null, 0)
                                    .addBonus("concentration",		null, 0)
                                    .addBonus("diplomacy",			null, 0)
                                    .addBonus("disable device",	null, 0)
                                    .addBonus("haggle",			null, 0)
                                    .addBonus("heal",				null, 0)
                                    .addBonus("hide",				null, 0)
                                    .addBonus("intimidate",		null, 0)
                                    .addBonus("jump",				null, 0)
                                    .addBonus("listen",			null, 0)
                                    .addBonus("move silently",		null, 0)
                                    .addBonus("open lock",			null, 0)
                                    .addBonus("perform",			null, 0)
                                    .addBonus("repair",			null, 0)
                                    .addBonus("search",			null, 0)
                                    .addBonus("spellcraft",		null, 0)
                                    .addBonus("spot",				null, 0)
                                    .addBonus("swim",				null, 0)
                                    .addBonus("tumble",			null, 0)
                                    .addBonus("use magic device",	null, 0);
        new NamedStat("alluring skills bonus")	.addBonus("bluff",				null, 0)
                                                .addBonus("diplomacy",			null, 0)
                                                .addBonus("haggle",			null, 0)
                                                .addBonus("intimidate",		null, 0)
                                                .addBonus("perform",			null, 0);
        new NamedStat("wizardry").addBonus("gear sp", null, 0);
        new NamedStat("power")   .addBonus("gear sp", null, 0);
        new NamedStat("magi")    .addBonus("gear sp", null, 100);
        new NamedStat("archmagi").addBonus("gear sp", null, 200);
        new NamedStat("universal spell power") .addBonus("potency", null,        0);
        new NamedStat("spellcasting implement").addBonus("potency", "implement", 0);
        new NamedStat("potency").addBonus("combustion",		null, 0)
                                .addBonus("corrosion",			null, 0)
                                .addBonus("devotion",			null, 0)
                                .addBonus("glaciation",		null, 0)
                                .addBonus("impulse",			null, 0)
                                .addBonus("magnetism",			null, 0)
                                .addBonus("nullification",		null, 0)
                                .addBonus("radiance",			null, 0)
                                .addBonus("reconstruction",	null, 0)
                                .addBonus("resonance",			null, 0);
        new NamedStat("power of the frozen storm").addBonus("glaciation", "equipment", 0)
                                                  .addBonus("magnetism",  "equipment", 0);
        new NamedStat("universal spell lore").addBonus("arcane lore", null, 0);
        new NamedStat("arcane lore")
                .addBonus("fire lore",      null, 0)
                .addBonus("acid lore",      null, 0)
                .addBonus("healing lore",   null, 0)
                .addBonus("kinetic lore",   null, 0)
                .addBonus("ice lore",       null, 0)
                .addBonus("lightning lore", null, 0)
                .addBonus("void lore",      null, 0)
                .addBonus("radiance lore",  null, 0)
                .addBonus("repair lore",    null, 0)
                .addBonus("sonic lore",     null, 0);
        new NamedStat("frozen storm lore").addBonus("ice lore",       "equipment", 0)
                                          .addBonus("lightning lore", "equipment", 0);
        new NamedStat("spell focus mastery")
            .addBonus("abjuration focus",		null, 0)
            .addBonus("conjuration focus",	null, 0)
            .addBonus("divination focus",		null, 0)
            .addBonus("enchantment focus",	null, 0)
            .addBonus("evocation focus",		null, 0)
            .addBonus("illision focus",		null, 0)
            .addBonus("necromancy focus",		null, 0)
            .addBonus("transmutation focus",	null, 0);
        new NamedStat("deific focus").addBonus("spell focus mastery", "sacred", 0);
        new NamedStat("lifesealed").addBonus("deathblock",          "default", 1)
                                   .addBonus("negative absorption", "default", 50);
        new NamedStat("build combat mastery").addBonus("combat mastery", null, 0)
                                             .addBonus("qp dc",          null, 0);
        new NamedStat("electricity resistance").addBonus("electric resistance", null, 0);
        new NamedStat("lesser fire resistance").addBonus("fire resistance", null, 5);
        new NamedStat("improved fire resistance").addBonus("fire resistance", null, 20);
        new NamedStat("greater fire resistance").addBonus("fire resistance", null, 30);
        new NamedStat("superior fire resistance").addBonus("fire resistance", null, 40);
        new NamedStat("brilliant silver scales").addBonus("cold resistance", "enhancement", 83);
        new NamedStat("inherent elemental resistance").addBonus("acid resistance",        "competence", 0)
                                                      .addBonus("cold resistance",        "competence", 0)
                                                      .addBonus("electric resistance",    "competence", 0)
                                                      .addBonus("fire resistance",        "competence", 0); // sonic NOT in this set [sic]
        new NamedStat("elemental resistance").addBonus("acid resistance",        null, 0)
                                             .addBonus("cold resistance",        null, 0)
                                             .addBonus("electric resistance",    null, 0)
                                             .addBonus("fire resistance",        null, 0)
                                             .addBonus("sonic resistance",       null, 0);
        new NamedStat("elemental absorption").addBonus("acid absorption",        null, 0)
                                             .addBonus("cold absorption",        null, 0)
                                             .addBonus("electricity absorption", null, 0)
                                             .addBonus("fire absorption",        null, 0)
                                             .addBonus("sonic absorption",       null, 0);
        new NamedStat("chitinous covering: fire absorption").addBonus("fire absorption", null, 0);
        new NamedStat("shining silver scales (cold absorption").addBonus("cold absorption", "enhancement", 51);
        new NamedStat("devil's bones").addBonus("fire absorption", "enhancement", 31)
                                      .addBonus("evil absorption", "enhancement", 31);
        new NamedStat("hound's bones").addBonus("acid absorption", "enhancement", 31)
                                      .addBonus("evil absorption", "enhancement", 31);
        new NamedStat("immunity to fear").addBonus("fear immunity");
        new NamedStat("devil's blood").addBonus("curse immunity")
                                      .addBonus("poison immunity")
                                      .addBonus("fear immunity");
        new NamedStat("hound's blood").addBonus("charm immunity")
                                      .addBonus("poison immunity")
                                      .addBonus("petrification immunity");
        new NamedStat("songblade").addBonus("perform", "enhancement", 2);
        new NamedStat("alchemical conservation").addBonus("ki", 					"enhancement", 1)
                                                .addBonus("extra action boost", 	"default",     1)
                                                .addBonus("turn undead attempt", 	"default",     1)
                                                .addBonus("bard songs", 			"default",     1);
        new NamedStat("completed weapon").addBonus("[w]", "completed weapon", 0.5);
		new NamedStat("stormreaver's thunderclap:").addBonus("stormreaver's thunderclap", "default", 1);
		new NamedStat("fire vulnerability")  .addBonus("vulnerability");
		new NamedStat("cold vulnerability")  .addBonus("vulnerability");
		new NamedStat("acid vulnerability")  .addBonus("vulnerability");
		new NamedStat("fetters of unreality").addBonus("vulnerability");
        new NamedStat("invulnerability")        .addBonus("dr", "enhancement",   2.5); // DR 5/magic
        new NamedStat("life shield")            .addBonus("dr", "life shield",   1.5); // 15 temp HPs, 10% on get-hit.
        new NamedStat("demonic shield")         .addBonus("dr", "demonic shield",6.0); // 30 temp HPs, 20% on get-hit.
		new NamedStat("angelic grace")          .addBonus("dr", "angelic grace", 7.5); // 150 temp HPs, 5% on get-hit, 10 sec cooldown.
		new NamedStat("the golden curse")       .addBonus("dr", "goldskin",      10.0); // DR 30/adamantine for next 20 hits, 2% on get-hit.
        new NamedStat("improved demonic shield").addBonus("dr", "demonic shield",24.0); // 120 temp HPs, 20% ? on get-hit.
		new NamedStat("healers bounty") .addBonus("self healing when hit", "healers bounty", 1.8); // 90 healing, 2% on get-hit.


//		new NamedStat("").addBonus("", "", 0);
//		new NamedStat("").addBonus("", "", 0);
    }
}
