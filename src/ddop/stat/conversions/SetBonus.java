package ddop.stat.conversions;

import ddop.stat.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetBonus {
	private static final List<SetBonus> all = new ArrayList<SetBonus>();
	
	private final String name;
	private Map<Integer, List<Stat>> bonus = new HashMap<Integer, List<Stat>>();
	
	private SetBonus(String name) {
		this.name = name;
		SetBonus.all.add(this);
	}
	
	private SetBonus addBonus(int piecesRequirement, Stat s) {
		if(this.bonus.containsKey(piecesRequirement)) {
			List<Stat> effects = this.bonus.get(piecesRequirement);
			effects.add(s);
		} else {
			ArrayList<Stat> effects = new ArrayList<Stat>();
			effects.add(s);
			this.bonus.put(piecesRequirement, effects);
		}
		
		return this;
	}
	
	static {
		new SetBonus("adherent of the mists set (legendary)")
			.addBonus(5, new Stat("physical sheltering",    "profane", 20))
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
			.addBonus(3, new Stat("melee power",		"artifact", 25))
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
		
		//.addBonus(5, new Stat("name", "type", 0))
	}

	public static List<Stat> getBonuses(List<Stat> stats) {
		List<Stat> ret = new ArrayList<>();
		
		for(SetBonus sb : SetBonus.all) {
			int counts = getCounts(sb.name, stats);
			for(Integer countsRequired : sb.bonus.keySet()) {
				if(counts >= countsRequired) {
					for(Stat s : sb.bonus.get(countsRequired)) {
						ret.add(s);
					}
				}
			}
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
