package ddop.item.loadout;

public class StoredLoadouts {
    public static NamedLoadout getShintaoPreSharnGear() {
        String[] itemNames = new String[] {
                "visions of precision",
                "legendary executioner's helm",
                "fleetfoot necklace",
                "echo of ravenkind",
                "legendary disciple of the dawn",
                "legendary lore-fueled packbanner",
                "the invisible cloak of strahd",
                "legendary braided cutcord",
                "orcish privateer's boots",
                "cc gloves devotion iaccuracy resistance",
                "legendary perfect pinnacle",
                "legendary ring of prowess",
        };
        
        return new NamedLoadout("Shintao Pre-Sharn", itemNames);
    }
    
    public static NamedLoadout getShintaoSharnGear() {
        String[] itemNames = new String[] {
                "legendary collective sight con iwis version",
                "cc trinket ds truesee icon version",
                "legendary turncoat",
                "legendary family recruit sigil",
                "legendary hammerfist",
                "radiant ring of taer valaestas",
                "legendary celestial ruby ring dex version",
                "slavers belt hp resistance tendon slice qwis version",
                "the invisible cloak of strahd",
                "legendary umber brim",
                "azure guard",
                "legendary moonrise bracers",
        };
    
        return new NamedLoadout("Shintao Sharn Pre-Raid", itemNames);
    }
    
    public static NamedLoadout getHalfishPreSharnGear() {
        String[] itemNames = new String[] {
                "attunement's gaze",
                "cc helm iwis accuracy sp version",
                "legendary ward-inscribed pendant",
                "cc trinket resistance hp idevotion version",
                "breastplate of the celestial sage",
                "band of diani ir'wynarn",
                "legendary hallowed trail",
                "legendary silverthread belt",
                "legendary sunken slippers",
                "cc gloves devotion iaccuracy healamp varsion",
                "legendary chieftain",
                "cc ring magnetism icon elecabs version",
                "epic dynamistic quiver",
        };
    
        return new NamedLoadout("", itemNames);
    }
    
    public static NamedLoadout getShintaoSoulSplitterGear() {
        String[] itemNames = new String[] {
                "legendary collective sight con iwis version",
                "legendary turncoat",
                "radiant ring of taer valaestas",
                "legendary celestial ruby ring dex version",
                "legendary hammerfist",
                "the cornerstone champion qwis version",
                "legendary family recruit sigil",
                "legendary umber brim",
                "azure guard",
                "silver dragonscale capelet idex version",
                "cc trinket ds resistance icon version",
                "legendary moonrise bracers",
        };

        return new NamedLoadout("Shintao Soul Splitter", itemNames);
    }

    public static NamedLoadout getHealbardNoSetGear() {
        String[] itemNames = new String[] {
                "cc goggles enchantment ienchantment ispellpen ml23 version",
                "cc gloves perform heal iperform ml25 version",
                "epic voice of the master",
                "cc scalemail fortification falselife parrying ml26 version",
                "sunken slippers qcha version",
                "sightless",
                "cc neck con isp icon ml26 version",
                "epic sanctuary",
                "epic holistic stave",
                "countenance",
                "bracers of the eagle",
                "silverthread belt",
                "cc ring soniclore resonance icha ml26 version",
                "circle of malevolence",
        };

        return new NamedLoadout("26 Healbard - No Set", itemNames);
    }

    public static NamedLoadout getHealbardCandidateGear() {
        String[] itemNames = new String[] {
        };

        return new NamedLoadout("26 Healbard - Candidate", itemNames);
    }
    
//    public static NamedLoadout getGear() {
//        String[] itemNames = new String[] {
//
//        };
//
//        return new NamedLoadout("", itemNames);
//    }
}
