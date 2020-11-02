package ddop.builds;

import ddop.constants.Tier;

public class GuildBuffs extends StatCollection {
    public GuildBuffs(boolean includeLegacy, Tier tier) {
        this.addStat("heal", 3);

        // Chronoscope
        this.addStat("reflex saves", tier.getRank());
        // 40% enhacement movement speed in town

        // Sellswords' Tavern
        // [1/2/3] well-rounded to hirelings

        // Bath House - 14
        this.addStat("healing amplification", 20);
        this.addStat("unconsciousness range", 5 * tier.getRank());
//        this.addStat("reduced damage taken while helpless", 10);

        // Floating Rock Garden - 15
        this.addStat("strength", 2);
        this.addStat("wisdom",   2);

        // Paradoxical Puzzle Box - 16
        this.addStat("dexterity",    2);
        this.addStat("intelligence", 2);

        // Old Sully's Grog Cellar - 17
        this.addStat("constitution", 2);
        this.addStat("charisma",     2);

        // Throne Room - 18
        this.addStat("bluff",      tier.getRank());
        this.addStat("diplomacy",  tier.getRank());
        this.addStat("haggle",     tier.getRank());
        this.addStat("intimidate", tier.getRank());
        this.addStat("listen",     tier.getRank());

        // Danger Room - 22
        this.addStat("disable device", tier.getRank());
        this.addStat("hide",           tier.getRank());
        this.addStat("open lock",      tier.getRank());
        this.addStat("search",         tier.getRank());
        this.addStat("spot",           tier.getRank());

        // Forbidden Library - 23
        this.addStat("concentration",    tier.getRank());
        this.addStat("heal",             tier.getRank());
        this.addStat("repair",           tier.getRank());
        this.addStat("spellcraft",       tier.getRank());
        this.addStat("use magic device", tier.getRank());

        // Otto's Irresistable Dance Hall - 26
        this.addStat("balance",       tier.getRank());
        this.addStat("jump",          tier.getRank());
        this.addStat("move silently", tier.getRank());
        this.addStat("perform",       tier.getRank());
        this.addStat("swim",          tier.getRank());
        this.addStat("tumble",        tier.getRank());

        // Proving Ground - 34
        this.addStat("will saves", 2);
        this.addStat("ac",            2 * tier.getRank());
        this.addStat("fortification", 5 * tier.getRank());
        this.addStat("doubleshot", 2);
        this.addStat("critical damage", 2 * tier.getRank());
        this.addStat("combat mastery", 1);
        this.addStat("qp dc",          1);
        this.addStat("accuracy", 2);

        // Collegium of the Twelve - 35
        this.addStat("wild empathy charges", 2);
        this.addStat("augment summoning",    3);
        this.addStat("armor-piercing",       5);
        this.addStat("devotion",      5 * tier.getRank());
        this.addStat("nullification", 5 * tier.getRank());
        this.addStat("enchantment saves", 1);
        this.addStat("sp",               25);
        this.addStat("spell penetration", 1);

        // Bash the Breakables Cargo Bay - 36
        // +1 Loot Boost

        // Black Abbot's Shadow - 33
        this.addStat("turn undead charges",  1);
        this.addStat("lay on hands charges", 1);
        this.addStat("smite evil charges",   1);
        this.addStat("saves vs evil", 1, "enhancement");
        this.addStat("ac vs evil",    4, "deflection");

        // Concert Hall - 38
        this.addStat("enchantment saves",    1);
        this.addStat("bard song charges",    1);
        this.addStat("action boost charges", 1);

        // Archwizard - 39
        this.addStat("spell focus mastery", 1);

        // Game Hunter - 42
        this.addStat("fortitude saves", tier.getRank());
        this.addStat("damage vs helpless", 5);

        // Fencing Master - 43
//        this.addStat("maximum dodge",           2); Only works if no armor?
//        this.addStat("maximum dexterity bonus", 2); Does not work?

        // Ninja Assassin - 44
        this.addStat("[w]",               0.25);
//        this.addStat("flanking accuracy", 6); // NYI

        // Hag Apothecary - 45
        this.addStat("hp", 20);
        this.addStat("poison saves",  1);
        this.addStat("disease saves", 1);

        // Shrine of Experience V - 60
        this.addStat("experience", 5);

        // Grand Reliquary IV - 150
        this.addStat("elemental resistance", 5 * tier.getRank());
        this.addStat("corrosion",       5 * tier.getRank());
        this.addStat("glaciation",      5 * tier.getRank());
        this.addStat("magnetism",       5 * tier.getRank());
        this.addStat("combustion",      5 * tier.getRank());
        this.addStat("resonance",       5 * tier.getRank());
        this.addStat("acid absorption",        15);
        this.addStat("cold absorption",        15);
        this.addStat("electricity absorption", 15);
        this.addStat("fire absorption",        15);
        this.addStat("sonic absorption",       15);
    }

    public GuildBuffs addStat(String category, double magnitude) {
        return (GuildBuffs) this.addStat(category, magnitude, "guild");
    }
}
