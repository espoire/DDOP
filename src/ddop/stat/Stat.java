package ddop.stat;

import ddop.stat.conversions.NamedStat;
import util.RomanNumeral;

import java.util.*;

public class Stat {
	public final String category;
	public final String bonusType;
	public final double magnitude;
	private final String source;
	
	public Stat(String category) {
		this(category, "Boolean", 1);
	}
	
	public Stat(String category, double magnitude) {
		this(category, getDefaultBonusTypeForCategory(category), magnitude);
	}
	
	public Stat(String category, String bonusType, double magnitude) {
		this(category, bonusType, magnitude, null);
	}

	private Stat(String category, String bonusType, double magnitude, String source) {
		this.category = category.toLowerCase();

		if(bonusType == null || !bonusType.equals("stacking"))
			if(this.isSetBonus() || this.isAugmentSlot() || this.isCraftablePlaceholder())
				bonusType = "stacking";

		this.bonusType = bonusType;
		this.magnitude = magnitude;
		this.source = (source != null ? source.toLowerCase() : null);
	}

	public Stat addSource(String source) {
		if(source == null && this.source == null) return this;
		return new Stat(this.category, this.bonusType, this.magnitude, source);
	}

	public String getSource() {
		return this.source;
	}


	public static final String[] SET_BONUSES = new String[] { // TODO make this generated

			"curse necromancer",
			"curse necromancer (legendary)",
			"heavy warfare",
			"heavy warfare (legendary)",
			"renegade champion",
			"renegade champion (legendary)",
			"seasons of change",
			"seasons of change (legendary)",

			"wayward warrior",
			"wayward warrior (legendary)",
			"pain and suffering",

			// Ravenloft
			"adherent of the mists set (heroic)",
			"adherent of the mists set (legendary)",
			"beacon of magic set (heroic)",
			"beacon of magic set (legendary)",
			"crypt raider set (heroic)",
			"crypt raider set (legendary)",
			"knight of the shadows set (heroic)",
			"knight of the shadows set (legendary)",
			"silent avenger set (heroic)",
			"silent avenger set (legendary)",

			// Sharn
			"arcsteel battlemage",
			"esoteric initiate",
			"flamecleansed fury",
			"guardian of the gates",
			"hruit's influence",
			"part of the family",
			"wallwatch",
			"legendary arcsteel battlemage",
			"legendary esoteric initiate",
			"legendary flamecleansed fury",
			"legendary guardian of the gates",
			"legendary hruit's influence",
			"legendary part of the family",
			"legendary wallwatch",

			// Soul Splitter
			"dreadkeeper",
			"feywild dreamer",
			"profane experiemnt",

			// U47 (VoD / LoB / MA)
			"legacy of lorikk",
			"legacy of levikk",
			"mind and matter",
			"legacy of tharne",
			"anger of the avalanche",
			"mantle of suulomades",
			"one with the swarm",
			"chained elementals",
			"tyrannical tinkerer",
			"masterful magewright",
			"fastidious fabricator",
			"astute alchemist",
			"conduit of the titans",

			// U48
			"seasons of the feywild",
			"eminence of winter",
			"eminence of spring",
			"eminence of summer",
			"eminence of autumn",
	};
	
	private boolean isSetBonus() {
		return util.Array.contains(SET_BONUSES, this.category);
	}
	private static boolean isSetBonus(String categoryOrEnchantment) {
		for(String setBonus : SET_BONUSES)
			if(categoryOrEnchantment.contains(setBonus))
				return true;

		return false;
	}

	public static final String[] AUGMENT_SLOTS = new String[] {
			"empty red augment slot",
			"empty orange augment slot",
			"empty yellow augment slot",
			"empty green augment slot",
			"empty blue augment slot",
			"empty purple augment slot",
			"empty colorless augment slot",
	};

	private boolean isAugmentSlot() {
		return util.Array.contains(AUGMENT_SLOTS, this.category);
	}

	public static final String[] CRAFTABLE_PLACEHOLDERS = new String[] {
			"upgradeable primary augment",
			"upgradeable secondary augment",
	};

	private boolean isCraftablePlaceholder() {
		return util.Array.contains(CRAFTABLE_PLACEHOLDERS, this.category);
	}

	private static final Map<String, String> DEFAULT_TYPES = generateDefaultTypesMap();
	private static String getDefaultBonusTypeForCategory(String category) {
		category = category.toLowerCase();

		String ret = DEFAULT_TYPES.get(category);

		if(ret == null) return "default";
		return ret;
	}

	private static Map<String, String> generateDefaultTypesMap() {
		Map<String, String> ret = new HashMap<>();

		ret.put("potency", "equipment");
		ret.put("combustion", "equipment");
		ret.put("corrosion", "equipment");
		ret.put("devotion", "equipment");
		ret.put("glaciation", "equipment");
		ret.put("impulse", "equipment");
		ret.put("magnetism", "equipment");
		ret.put("nullification", "equipment");
		ret.put("radiance", "equipment");
		ret.put("reconstruction", "equipment");
		ret.put("resonance", "equipment");

		ret.put("minor artifact", "stacking");

		return ret;
	}

	public String toString() {
		return Stat.toString(this.category, this.bonusType, this.magnitude);
	}
	
	public static String toString(String category, double magnitude) {
		return Stat.toString(category, null, magnitude);
	}
	
	public static String toString(String category, String bonusType, double magnitude) {
		category = "\"" + category + "\"";
		if(bonusType != null && bonusType.equals("Boolean")) return category;
		
		String mag = "" + magnitude;
		if(magnitude == (int) magnitude) mag = "" + (int) magnitude;
		
		if(bonusType == null || bonusType.equals("Default")) return category + " " + mag;
		return category + " " + mag + " (" + bonusType + ")";
	}

	public static Collection<Stat> getStats(List<String> enchantments) {
		return Stat.getStats(enchantments, null);
	}

	public static Collection<Stat> getStats(List<String> enchantments, String source) {
		Collection<Stat> ret = new ArrayList<>();
		
		if(enchantments != null) for(String enchantment : enchantments) {
			Stat s = Stat.parseStat(enchantment);
			
			List<Stat> converted = NamedStat.convert(s);

			for(Stat conv : converted) {
				if(source != null) conv = conv.addSource(source);
				ret.add(conv);
			}
		}
		
		return ret;
	}
	
	private static final String ARABIC_NUMERAL_REGEX = "^[0-9]+(\\.[0-9]+)?$";
	private static final String QUALIFIED_ARABIC_NUMERAL_REGEX = "^[-+]?[0-9]+(\\.[0-9]+)?%*$";
	private static final String UNINFORMATIVE_QUALIFIERS_REGEX = "[-+%]";
	private static final String UNINFORMATIVE_TOKEN_REGEX = "^[^A-Za-z0-9]*$";
	private static final HashMap<String, String> BONUS_TYPE_TOKENS = new HashMap<>();
	static {
		BONUS_TYPE_TOKENS.put("enhanced",    "enhancement");
		BONUS_TYPE_TOKENS.put("enhancement", "enhancement");
		BONUS_TYPE_TOKENS.put("equipped",    "equipment");
		BONUS_TYPE_TOKENS.put("equipment",   "equipment");
		BONUS_TYPE_TOKENS.put("ins",         "insight");
		BONUS_TYPE_TOKENS.put("insight",     "insight");
		BONUS_TYPE_TOKENS.put("insightful",  "insight");
		BONUS_TYPE_TOKENS.put("quality",     "quality");
		BONUS_TYPE_TOKENS.put("profane",     "profane");
		BONUS_TYPE_TOKENS.put("exceptional", "exceptional");
		BONUS_TYPE_TOKENS.put("competence",  "competence");
		BONUS_TYPE_TOKENS.put("inherent",    "competence");
		BONUS_TYPE_TOKENS.put("artifact",    "artifact");
		BONUS_TYPE_TOKENS.put("legendary",   "legendary");
	}
	
	public static Stat parseStat(String enchantment) {
		if(Stat.isSetBonus(enchantment))
			return new Stat(enchantment, "stacking", 1);

		String[] tokens = enchantment.split("[ ]+");
		
		// Remove uninformative tokens. DDOWiki sometimes formats enchantments as "Armor-Piercing - +23%". Should reduce to "Armor-Piercing 23"
		for(int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if(token == null) continue;
			if(token.matches(QUALIFIED_ARABIC_NUMERAL_REGEX)) {
				tokens[i] = token.replaceAll(UNINFORMATIVE_QUALIFIERS_REGEX, "");
			}
			if(token.matches(UNINFORMATIVE_TOKEN_REGEX)) {
				tokens[i] = null;
			}
		}
		
		Double magnitude = null;
		for(int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if(token == null) continue;
			if(RomanNumeral.isRomanNumeral(token)) {
				magnitude = (double) RomanNumeral.parseRomanNumeral(token);
				tokens[i] = null;
				break;
			} else if(token.matches(ARABIC_NUMERAL_REGEX)) {
				magnitude = Double.parseDouble(token);
				tokens[i] = null;
				break;
			}
		}
		
		String bonusType = null;
		String consumedBonusTypeToken = null;
		for(int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if(token == null) continue;
			if(util.Array.contains(BONUS_TYPE_TOKENS.keySet(), token)) {
				bonusType = BONUS_TYPE_TOKENS.get(token);
				consumedBonusTypeToken = token;
				tokens[i] = null;
				break;
			}
		}
		
		StringBuilder categoryBuilder = new StringBuilder();
		boolean firstToken = true;
		for (String token : tokens) {
			if (token == null) continue;

			if (!firstToken) categoryBuilder.append(" ");
			firstToken = false;

			categoryBuilder.append(token);
		}
		String category = categoryBuilder.toString();

		Stat ret;
		if(magnitude != null) {
			if(bonusType != null) {
				ret = new Stat(category, bonusType, magnitude);
			} else {
				ret = new Stat(category, magnitude);
			}
		} else {
			ret = new Stat(category);
		}
		
		return ret;
	}
	
	private static final String[] STACKING_BONUS_TYPES = new String[] {
		"stacking",
		"mythic",
		"reaper",
		"in-reaper",
		"untyped"
	};

	public boolean stacks() {
		return Stat.stacks(this.bonusType);
	}

	public static boolean stacks(String bonusType) {
		return util.Array.contains(STACKING_BONUS_TYPES, bonusType);
	}
}