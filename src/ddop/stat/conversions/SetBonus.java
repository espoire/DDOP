package ddop.stat.conversions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.item.Item;
import ddop.item.loadout.EquipmentLoadout;
import ddop.stat.Stat;
import ddop.stat.StatMap;

import java.lang.reflect.Type;
import java.util.*;

public class SetBonus {
	private static final Map<String, SetBonus> all = new HashMap<>();
	
	private final String name;
	private final Map<Integer, List<Stat>> bonus = new HashMap<>();
	
	private SetBonus(String name) {
		this.name = name;
		SetBonus.all.put(this.name, this);
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
				.addBonus(2, new Stat("physical sheltering", "artifact", 25))
				.addBonus(2, new Stat("incite", "artifact", 75));
		new SetBonus("mind and matter")
				.addBonus(2, new Stat("physical sheltering", "artifact", 10))
				.addBonus(2, new Stat("magical sheltering", "artifact", 10))
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
				.addBonus(2, new Stat("physical sheltering", "artifact", 20))
				.addBonus(2, new Stat("magical sheltering", "artifact", 20));
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
				.addBonus(2, new Stat("magical sheltering", "artifact", 25))
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

		importSetBonusJsons();

		//.addBonus(5, new Stat("name", "type", 0))
	}

	private static void importSetBonusJsons() {
		Collection<String> jsons = file.Reader.getEntireDirectory(Settings.SET_BONUS_DEFINITIONS_JSON_DIRECTORY).values();

		for(String json : jsons) {
			Map<String, Map<Integer, List<String>>> map = loadJson(json);

			for(Map.Entry<String, Map<Integer, List<String>>> entry : map.entrySet()) {
				String name = entry.getKey();
				Map<Integer, List<String>> bonuses = entry.getValue();

				SetBonus setBonus = new SetBonus(name);

				for(Map.Entry<Integer, List<String>> subentry : bonuses.entrySet()) {
					int piecesRequirement = subentry.getKey();
					List<String> enchantments = subentry.getValue();

					for(String enchantment : enchantments) {
						setBonus.addBonus(piecesRequirement, Stat.parseStat(enchantment));
					}
				}
			}
		}
	}

	private static Map<String, Map<Integer, List<String>>> loadJson(String json) {
		Type type = new TypeToken<Map<String, Map<Integer, List<String>>>>() {}.getType();
		return new Gson().fromJson(json, type);
	}

	public static boolean isSetBonus(Stat s) { return SetBonus.isSetBonus(s.category); }
	public static boolean isSetBonus(String category) {
		return SetBonus.all.containsKey(category);
	}

	public static List<Stat> convertAll(List<Stat> baseStats) {
		List<Stat> ret = new ArrayList<>(baseStats);

		List<Stat> setBonuses = getBonuses(baseStats);
		ret.addAll(setBonuses);

		return ret;
	}

	/** Returns a list of all Stats which may descend from any members of a provided list of Stats. */
	public static List<Stat> getDescendants(List<Stat> stats) {
		return SetBonus.getBonusesImplementation(stats, true);
	}

	public static List<Stat> getBonuses(List<Stat> stats) {
		return SetBonus.getBonusesImplementation(stats, false);
	}

	private static List<Stat> getBonusesImplementation(Collection<Stat> stats, boolean suppressCountsRequirement) {
		List<Stat> ret = new ArrayList<>();

		for(SetBonus sb : SetBonus.all.values()) {
			int counts = getCounts(sb.name, stats);
			for(Integer countsRequired : sb.bonus.keySet()) {
				if(counts >= countsRequired || (suppressCountsRequirement && counts >= 1))
					ret.addAll(sb.bonus.get(countsRequired));
			}
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

	public static String listAttainment(EquipmentLoadout el) {
		Collection<Item> items = el.toItemList();
		Collection<Stat> stats = el.getStats();

		StringBuilder ret = new StringBuilder();

		for(SetBonus sb : SetBonus.all.values()) {
			int counts = getCounts(sb.name, stats);

			int highestTier = 0;
			for(Integer countsRequired : sb.bonus.keySet())
				if(countsRequired > highestTier)
					if(counts >= countsRequired)
						highestTier = countsRequired;

			if(highestTier > 0) {
				List<String> itemNames = sb.listMatchingItemNames(items);
				ret.append(sb.toString(highestTier, String.join(", ", itemNames)));
			}
		}

		String value = ret.toString();
		if(value.length() == 0) return "None";
		return value;
	}

	private List<String> listMatchingItemNames(Collection<Item> items) {
		List<String> ret = new ArrayList<>();

		for(Item i : items)
			for(Stat s : i.getStats())
				if(s.category.equals(this.name))
					ret.add(i.name);

		return ret;
	}

	private static int getCounts(String setName, Collection<Stat> stats) {
		int ret = 0;

		for(Stat s : stats)
			if(s.category.equals(setName))
				ret++;

		return ret;
	}

	public String toString() { return this.toString(Integer.MAX_VALUE, null); }
	private String toString(int highestTier, String itemNames) {
		StringBuilder ret = new StringBuilder(this.name + (itemNames == null ? "" : " (" + itemNames + ")") + "\n");

		List<Integer> bonusTiers = new ArrayList<>(this.bonus.keySet());
		bonusTiers.sort(null);

		for(Integer countsRequired : bonusTiers) {
			if(countsRequired > highestTier) continue;

			ret.append("  • ").append(countsRequired).append(":\n");

			for(Stat s : this.bonus.get(countsRequired)) {
				ret.append("    • ").append(s.toString()).append("\n");
			}
		}

		return ret.toString();
	}

	public static void printAllSetsDebug() {
		for(SetBonus s : all.values())
			System.out.println(s);
	}
}
