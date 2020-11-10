package ddop.item.sources.crafted;

import ddop.stat.Stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CannithCraftedItemSource {
    private static class CannithAffixes {
        private final List<Stat> prefixes, suffixes, extras, materials, sockets;

        private CannithAffixes(Stat[] prefixes, Stat[] suffixes, Stat[] extras, Stat[] materials, Stat[] sockets) {
            this.prefixes  = new ArrayList<>(Arrays.asList(prefixes));
            this.suffixes  = new ArrayList<>(Arrays.asList(suffixes));
            this.extras    = new ArrayList<>(Arrays.asList(extras));
            this.materials = new ArrayList<>(Arrays.asList(materials));
            this.sockets   = new ArrayList<>(Arrays.asList(sockets));
        }
    }

    private static final CannithAffixes TRINKET = new CannithAffixes(
            new Stat[] {
                    new Stat("strength", 15),
                    new Stat("dexterity", 15),
                    new Stat("constitution", 15),
                    new Stat("intelligence", 15),
                    new Stat("wisdom", 15),
                    new Stat("charisma", 15),
                    new Stat("acid absorption", 38),
                    new Stat("cold absorption", 38),
                    new Stat("electric absorption", 38),
                    new Stat("fire absorption", 38),
                    new Stat("sonic absorption", 38),
                    new Stat("fortification", 159),
                    new Stat("dodge", 15),
                    new Stat("sheltering", 38),
                    new Stat("spell resistance", 34),
                    new Stat("protection", 12),
                    new Stat("diversion", 23),
                    new Stat("incite", 45),
                    new Stat("wizardry", 310),
                    new Stat("striding", 30),
                    new Stat("seeker", 15),
                    new Stat("doubleshot", 9),
                    new Stat("doublestrike", 17),
                    new Stat("melee alacrity", 15),
                    new Stat("ranged alacrity", 15),
                    new Stat("acid resistance", 57),
                    new Stat("cold resistance", 57),
                    new Stat("electric resistance", 57),
                    new Stat("fire resistance", 57),
                    new Stat("sonic resistance", 57),
                    new Stat("light resistance", 57),
                    new Stat("negative resistance", 57),
                    new Stat("poison resistance", 57),
                    new Stat("disease ward", 12), // TODO implement conversion
                    new Stat("poison ward", 12), // TODO implement conversion
                    new Stat("resistance", 12),
                    new Stat("enchantment resistance", "default", 12),
                    new Stat("illusion resistance", 12),
                    new Stat("spell saves", 12),
                    new Stat("balance", 22),
                    new Stat("bluff", 22),
                    new Stat("concentration", 22),
                    new Stat("diplomacy", 22),
                    new Stat("disable device", 22),
                    new Stat("haggle", 22),
                    new Stat("heal", 22),
                    new Stat("hide", 22),
                    new Stat("intimidate", 22),
                    new Stat("listen", 22),
                    new Stat("move silently", 22),
                    new Stat("open lock", 22),
                    new Stat("perform", 22),
                    new Stat("repair", 22),
                    new Stat("search", 22),
                    new Stat("spellsight", 22),
                    new Stat("tumble", 22),
                    new Stat("acid lore", 23),
                    new Stat("cold lore", 23),
                    new Stat("lightning lore", 23),
                    new Stat("fire lore", 23),
                    new Stat("sonic lore", 23),
                    new Stat("kinetic lore", 23),
                    new Stat("radiance lore", 23),
                    new Stat("healing lore", 23),
                    new Stat("void lore", 23),
                    new Stat("repair lore", 23),
                    new Stat("abjuration focus", 6),
                    new Stat("conjuration focus", 6),
                    new Stat("enchantment focus", 6),
                    new Stat("evocation focus", 6),
                    new Stat("illusion focus", 6),
                    new Stat("necromancy focus", 6),
                    new Stat("transmutation focus", 6),
                    new Stat("spell penetration", 6),
                    new Stat("combustion", 159),
                    new Stat("corrosion", 159),
                    new Stat("devotion", 159),
                    new Stat("glaciation", 159),
                    new Stat("impulse", 159),
                    new Stat("magnetism", 159),
                    new Stat("nullification", 159),
                    new Stat("radiance", 159),
                    new Stat("reconstruction", 159),
                    new Stat("resonance", 159),
                    new Stat("shatter", 17),
                    new Stat("stunning", 17),
                    new Stat("vertigo", 17),
                    new Stat("assassinate", 7),
                    new Stat("combat mastery", 12),
                    new Stat("blindness immunity"),
                    new Stat("feather fall"),
                    new Stat("lesser arcane spell dexterity"),
                    new Stat("persuasion"),
                    new Stat("sacred"),
                    new Stat("silver flame"),
                    new Stat("underwater action"),
            },
            new Stat[] {
                    new Stat("", 1),

            },
            new Stat[] {

                    new Stat("", 1),
            },
            new Stat[] {

                    new Stat("", 1),
            },
            new Stat[] {

                    new Stat("", 1),
            }
    );
}
