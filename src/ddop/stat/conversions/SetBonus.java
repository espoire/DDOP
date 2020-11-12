package ddop.stat.conversions;

import ddop.stat.Stat;
import ddop.stat.StatMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetBonus {
	private static final Map<String, SetBonus> all = new HashMap<>();
	
	private final String name;
	private final Map<Integer, List<Stat>> bonus = new HashMap<>();
	
	private SetBonus(String name) {
		this.name = name;
		SetBonus.all.put(this.name, this);
	}

	public static boolean isSetBonus(Stat s) { return SetBonus.isSetBonus(s.category); }
	public static boolean isSetBonus(String s) {
		return SetBonus.all.containsKey(s);
	}

    private SetBonus addBonus(int piecesRequirement, Stat s) {
		if(this.bonus.containsKey(piecesRequirement)) {
			List<Stat> effects = this.bonus.get(piecesRequirement);
			effects.add(s);
		} else {
			List<Stat> effects = new ArrayList<>();
			effects.add(s);
			this.bonus.put(piecesRequirement, effects);
		}
		
		return this;
	}
	
	static {
		new SetBonus("adherent of the mists set (legendary)")
			.addBonus(5, new Stat("prr",    "profane", 20))
			.addBonus(5, new Stat("healing amplification",  "profane", 20))
			.addBonus(5, new Stat("negative amplification", "profane", 20))
			.addBonus(5, new Stat("repair amplification",   "profane", 20))
			.addBonus(5, new Stat("melee power",            "profane", 10))
			.addBonus(5, new Stat("ranged power",           "profane", 10))
			.addBonus(5, new Stat("universal spell power",  "profane", 20));
		new SetBonus("silent avenger set (legendary)")
			.addBonus(3, new Stat("doublestrike",       "artifact", 15))
			.addBonus(3, new Stat("doubleshot",         "artifact", 15))
			.addBonus(3, new Stat("sneak attack dice",  "artifact", 3))
			.addBonus(3, new Stat("armor-piercing",     "artifact", 25))
			.addBonus(3, new Stat("damage vs helpless", "artifact", 10));
		new SetBonus("legendary part of the family")
			.addBonus(3, new Stat("doublestrike",		"artifact", 15))
			.addBonus(3, new Stat("melee power",			"artifact", 25))
			.addBonus(3, new Stat("damage vs helpless",	"artifact", 15))
			.addBonus(3, new Stat("armor-piercing",		"artifact", 10));
		new SetBonus("legendary wallwatch")
			.addBonus(3, new Stat("doubleshot",			"artifact", 20))
			.addBonus(3, new Stat("ranged power",		"artifact", 20))
			.addBonus(3, new Stat("sneak attack dice",	"artifact",  3))
			.addBonus(3, new Stat("armor-piercing",		"artifact", 25));
		new SetBonus("legendary hruit's influence")
			.addBonus(3, new Stat("combustion",			"artifact", 50))
			.addBonus(3, new Stat("glaciation",			"artifact", 50))
			.addBonus(3, new Stat("magnetism",			"artifact", 50))
			.addBonus(3, new Stat("devotion",			"artifact", 50))
			.addBonus(3, new Stat("fire lore",			"artifact", 10))
			.addBonus(3, new Stat("ice lore",			"artifact", 10))
			.addBonus(3, new Stat("lightning lore",		"artifact", 10))
			.addBonus(3, new Stat("healing lore",		"artifact", 10))
			.addBonus(3, new Stat("wisdom",				"artifact",  4))
			.addBonus(3, new Stat("spell focus mastery","artifact",  3));
		new SetBonus("legendary flamecleansed fury")
			.addBonus(3, new Stat("combustion",			"artifact", 50))
			.addBonus(3, new Stat("impulse",			"artifact", 50))
			.addBonus(3, new Stat("radiance",			"artifact", 50))
			.addBonus(3, new Stat("devotion",			"artifact", 50))
			.addBonus(3, new Stat("fire lore",			"artifact", 10))
			.addBonus(3, new Stat("kinetic lore",		"artifact", 10))
			.addBonus(3, new Stat("radiance lore",		"artifact", 10))
			.addBonus(3, new Stat("healing lore",		"artifact", 10))
			.addBonus(3, new Stat("wisdom",				"artifact",  4))
			.addBonus(3, new Stat("charisma",			"artifact",  4))
			.addBonus(3, new Stat("spell focus mastery","artifact",  3));
		new SetBonus("flamecleansed fury")
				.addBonus(3, new Stat("combustion",			"artifact", 25))
				.addBonus(3, new Stat("impulse",				"artifact", 25))
				.addBonus(3, new Stat("radiance",			"artifact", 25))
				.addBonus(3, new Stat("devotion",			"artifact", 25))
				.addBonus(3, new Stat("fire lore",			"artifact", 5))
				.addBonus(3, new Stat("kinetic lore",		"artifact", 5))
				.addBonus(3, new Stat("radiance lore",		"artifact", 5))
				.addBonus(3, new Stat("healing lore",		"artifact", 5))
				.addBonus(3, new Stat("wisdom",				"artifact",  2))
				.addBonus(3, new Stat("charisma",			"artifact",  2))
				.addBonus(3, new Stat("spell focus mastery","artifact",  1));
		
		//TODO Guardian of the Gates, Esoteric Initiate, Arcsteel Battlemage

		// U47 (VoD, MA, LoB) Sets
		new SetBonus("legacy of lorikk")
				.addBonus(2, new Stat("devotion", "artifact", 50))
				.addBonus(2, new Stat("radiance", "artifact", 50))
				.addBonus(2, new Stat("healing lore", "artifact", 10))
				.addBonus(2, new Stat("radiance lore", "artifact", 10));
		new SetBonus("legacy of levikk")
				.addBonus(2, new Stat("percent ac", "artifact", 10))
				.addBonus(2, new Stat("prr", "artifact", 25))
				.addBonus(2, new Stat("incite", "artifact", 75));
		new SetBonus("mind and matter")
				.addBonus(2, new Stat("prr", "artifact", 10))
				.addBonus(2, new Stat("mrr", "artifact", 10))
				.addBonus(2, new Stat("mrr cap", "artifact", 20))
				.addBonus(2, new Stat("healing amplification", "artifact", 10))
				.addBonus(2, new Stat("negative amplification", "artifact", 10))
				.addBonus(2, new Stat("repair amplification", "artifact", 10));
		new SetBonus("legacy of tharne")
				.addBonus(2, new Stat("doublestrike", "artifact", 10))
				.addBonus(2, new Stat("doubleshot", "artifact", 10))
				.addBonus(2, new Stat("damage vs helpless", "artifact", 10))
				.addBonus(2, new Stat("sneak attack dice", "artifact", 3))
				.addBonus(2, new Stat("spot", "artifact", 5))
				.addBonus(2, new Stat("search", "artifact", 5));
		new SetBonus("anger of the avalanche")
				.addBonus(2, new Stat("ice lore", "artifact", 10))
				.addBonus(2, new Stat("sonic lore", "artifact", 10))
				.addBonus(2, new Stat("lightning lore", "artifact", 10))
				.addBonus(2, new Stat("glaciation", "artifact", 50))
				.addBonus(2, new Stat("magnetism", "artifact", 50))
				.addBonus(2, new Stat("resonance", "artifact", 50));
		new SetBonus("mantle of suulomades")
				.addBonus(2, new Stat("sneak attack dice", "artifact", 3))
				.addBonus(2, new Stat("doublestrike", "artifact", 10))
				.addBonus(2, new Stat("doubleshot", "artifact", 10))
				.addBonus(2, new Stat("prr", "artifact", 20))
				.addBonus(2, new Stat("mrr", "artifact", 20));
//		new SetBonus("one with the swarm") BEES!
		new SetBonus("chained elementals")
				.addBonus(2, new Stat("fire lore", "artifact", 10))
				.addBonus(2, new Stat("ice lore", "artifact", 10))
				.addBonus(2, new Stat("acid lore", "artifact", 10))
				.addBonus(2, new Stat("lightning lore", "artifact", 10))
				.addBonus(2, new Stat("combustion", "artifact", 50))
				.addBonus(2, new Stat("glaciation", "artifact", 50))
				.addBonus(2, new Stat("corrosion", "artifact", 50))
				.addBonus(2, new Stat("magnetism", "artifact", 50));
		new SetBonus("tyrannical tinkerer")
				.addBonus(2, new Stat("sneak attack dice", "artifact", 3))
				.addBonus(2, new Stat("melee power", "artifact", 15))
				.addBonus(2, new Stat("ranged power", "artifact", 15))
				.addBonus(2, new Stat("armor-piercing", "artifact", 25))
				.addBonus(2, new Stat("open lock", "artifact", 5))
				.addBonus(2, new Stat("disable device", "artifact", 5));
		new SetBonus("masterful magewright")
				.addBonus(2, new Stat("spell focus mastery", "artifact", 3))
				.addBonus(2, new Stat("intelligence", "artifact", 4))
				.addBonus(2, new Stat("wisdom", "artifact", 4))
				.addBonus(2, new Stat("charisma", "artifact", 4))
				.addBonus(2, new Stat("perform", "artifact", 5))
				.addBonus(2, new Stat("concentration", "artifact", 5));
		new SetBonus("fastidious fabricator")
				.addBonus(2, new Stat("percent ac", "artifact", 10))
				.addBonus(2, new Stat("mrr", "artifact", 25))
				.addBonus(2, new Stat("mrr cap", "artifact", 10))
				.addBonus(2, new Stat("balance", "artifact", 5))
				.addBonus(2, new Stat("repair", "artifact", 5));
		new SetBonus("astute alchemist")
				.addBonus(2, new Stat("fire lore", "artifact", 10))
				.addBonus(2, new Stat("void lore", "artifact", 10))
				.addBonus(2, new Stat("acid lore", "artifact", 10))
				.addBonus(2, new Stat("combustion", "artifact", 50))
				.addBonus(2, new Stat("nullification", "artifact", 50))
				.addBonus(2, new Stat("corrosion", "artifact", 50));
		new SetBonus("conduit of the titans")
				.addBonus(2, new Stat("rune arm focus", "artifact", 4))
				.addBonus(2, new Stat("lightning lore", "artifact", 10))
				.addBonus(2, new Stat("repair lore", "artifact", 10))
				.addBonus(2, new Stat("kinetic lore", "artifact", 10))
				.addBonus(2, new Stat("magnetism", "artifact", 50))
				.addBonus(2, new Stat("reconstruction", "artifact", 50))
				.addBonus(2, new Stat("impulse", "artifact", 50));

		// TODO Feywild sets


		//.addBonus(5, new Stat("name", "type", 0))
	}


	public static List<Stat> convertAll(List<Stat> baseStats) {
		List<Stat> ret = new ArrayList<>(baseStats);

		List<Stat> setBonuses = getBonuses(baseStats);
		ret.addAll(setBonuses);

		return ret;
	}

	public static List<Stat> getBonuses(List<Stat> stats) {
		List<Stat> ret = new ArrayList<>();
		
		for(SetBonus sb : SetBonus.all.values()) {
			int counts = getCounts(sb.name, stats);
			for(Integer countsRequired : sb.bonus.keySet())
				if(counts >= countsRequired)
					ret.addAll(sb.bonus.get(countsRequired));
		}
		
		return ret;
	}

	public static List<Stat> getBonuses(StatMap stats) {
		List<Stat> ret = new ArrayList<>();

		for(SetBonus sb : SetBonus.all.values()) {
			int counts = (int) stats.getCategoryTotal(sb.name);
			for(Integer countsRequired : sb.bonus.keySet())
				if(counts >= countsRequired)
					ret.addAll(sb.bonus.get(countsRequired));
		}

		return ret;
	}

	private static int getCounts(String setName, List<Stat> stats) {
		int ret = 0;
		for(Stat s : stats) {
			if(s.category.equals(setName)) ret++;
		}
		return ret;
	}
}
