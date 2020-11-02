package ddop.stat.conversions;

import ddop.stat.Stat;

import java.util.*;

public class NamedStat {
	private static final Map<String, NamedStat> all = new HashMap<>();

	private final List<Stat> conversion = new ArrayList<>();
	
	private NamedStat(String name) {
		if(name != null && name.length() > 0) NamedStat.all.put(name, this);
	}
	
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
		new NamedStat("hit points")         .addBonus(new Stat("hp",                  null,       0));
		new NamedStat("false life")         .addBonus(new Stat("hp",                  null,       0));
		new NamedStat("vitality")           .addBonus(new Stat("hp",                  "vitality", 0));
		new NamedStat("elemental energy")         .addBonus(new Stat("hp", "elemental energy",          10));
		new NamedStat("improved elemental energy").addBonus(new Stat("hp", "improved elemental energy", 15));
		new NamedStat("greater elemental energy") .addBonus(new Stat("hp", "greater elemental energy",  20));
		new NamedStat("don't count me out!").addBonus(new Stat("unconsciousness range","enhancement", 400));
		new NamedStat("strength of purpose").addBonus(new Stat("unconsciousness range","enhancement", 128))
											.addBonus(new Stat("regeneration", 		  "enhancement",  16));
		new NamedStat("sheltering")         .addBonus(new Stat("physical sheltering", null,       0))
								            .addBonus(new Stat("magical sheltering",  null,       0));
		new NamedStat("blurry")             .addBonus(new Stat("concealment",         "default",  20));
		new NamedStat("smoke screen")       .addBonus(new Stat("concealment",         "default",  20));
		new NamedStat("lesser displacement").addBonus(new Stat("concealment",         "default",  25));
		new NamedStat("combat mastery")     .addBonus(new Stat("stunning",            null,       0))
											.addBonus(new Stat("vertigo",             null,       0))
											.addBonus(new Stat("shatter",             null,       0));
		new NamedStat("natural armor")      .addBonus(new Stat("ac", "natural armor", 0));
		new NamedStat("protection")         .addBonus(new Stat("ac", "deflection",    0));
		new NamedStat("greater reinforced fists") .addBonus(new Stat("reinforced fists", "default", 2));
		new NamedStat("superior reinforced fists").addBonus(new Stat("reinforced fists", "default", 3));
		new NamedStat("well rounded")             .addBonus(new Stat("strength",     null, 0))
												  .addBonus(new Stat("dexterity",    null, 0))
												  .addBonus(new Stat("constitution", null, 0))
												  .addBonus(new Stat("intelligence", null, 0))
												  .addBonus(new Stat("wisdom",       null, 0))
												  .addBonus(new Stat("charisma",     null, 0));
		new NamedStat("litany of the dead ii - ability bonus").addBonus(new Stat("well rounded", "profane", 2));
		new NamedStat("litany of the dead ii - combat bonus")	.addBonus(new Stat("accuracy",	"profane", 4))
																.addBonus(new Stat("deadly",	"profane", 4));
		new NamedStat("ghostly")                  .addBonus(new Stat("incorporeal",   "default", 10))
												  .addBonus(new Stat("ghost touch",   "boolean", 1))
												  .addBonus(new Stat("hide",          "enhancement", 5))
												  .addBonus(new Stat("move silently", "enhancement", 5));
		new NamedStat("ethereal").addBonus(new Stat("ghost touch",   "boolean", 1));
		new NamedStat("resistance").addBonus(new Stat("fortitude saves", null,      0))
								   .addBonus(new Stat("reflex saves",    null,      0))
								   .addBonus(new Stat("will saves",      null,      0));
		new NamedStat("parrying")  .addBonus(new Stat("fortitude saves", "insight", 0))
								   .addBonus(new Stat("reflex saves",    "insight", 0))
								   .addBonus(new Stat("will saves",      "insight", 0))
								   .addBonus(new Stat("ac",              "insight", 0));
		new NamedStat("riposte")   .addBonus(new Stat("fortitude saves", "insight", 0))
								   .addBonus(new Stat("reflex saves",    "insight", 0))
								   .addBonus(new Stat("will saves",      "insight", 0))
								   .addBonus(new Stat("ac",              "insight", 0));
		new NamedStat("heroism")        .addBonus(new Stat("accuracy",		"morale", 2))
								        .addBonus(new Stat("resistance",	"morale", 2))
								        .addBonus(new Stat("all skills",	"morale", 2));
		new NamedStat("greater heroism").addBonus(new Stat("accuracy",		"morale", 4))
										.addBonus(new Stat("resistance",	"morale", 4))
										.addBonus(new Stat("all skills",	"morale", 4));
		new NamedStat("fortitude save").addBonus(new Stat("fortitude saves", null, 0));
		new NamedStat("reflex save")   .addBonus(new Stat("reflex saves",    null, 0));
		new NamedStat("will save")     .addBonus(new Stat("will saves",      null, 0));
		new NamedStat("seeker").addBonus(new Stat("critical confirmation",	null, 0))
							   .addBonus(new Stat("critical damage",		null, 0));
		new NamedStat("all skills") .addBonus(new Stat("balance",			null, 0))
									.addBonus(new Stat("bluff",				null, 0))
									.addBonus(new Stat("concentration",		null, 0))
									.addBonus(new Stat("diplomacy",			null, 0))
									.addBonus(new Stat("disable device",	null, 0))
									.addBonus(new Stat("haggle",			null, 0))
									.addBonus(new Stat("heal",				null, 0))
									.addBonus(new Stat("hide",				null, 0))
									.addBonus(new Stat("intimidate",		null, 0))
									.addBonus(new Stat("jump",				null, 0))
									.addBonus(new Stat("listen",			null, 0))
									.addBonus(new Stat("move silently",		null, 0))
									.addBonus(new Stat("open lock",			null, 0))
									.addBonus(new Stat("perform",			null, 0))
									.addBonus(new Stat("repair",			null, 0))
									.addBonus(new Stat("search",			null, 0))
									.addBonus(new Stat("spellcraft",		null, 0))
									.addBonus(new Stat("spot",				null, 0))
									.addBonus(new Stat("swim",				null, 0))
									.addBonus(new Stat("tumble",			null, 0))
									.addBonus(new Stat("use magic device",	null, 0));
		new NamedStat("alluring skills bonus")	.addBonus(new Stat("bluff",				null, 0))
									   			.addBonus(new Stat("diplomacy",			null, 0))
									   			.addBonus(new Stat("haggle",			null, 0))
									   			.addBonus(new Stat("intimidate",		null, 0))
									   			.addBonus(new Stat("perform",			null, 0));
		new NamedStat("wizardry").addBonus(new Stat("gear sp", null, 0));
		new NamedStat("power")   .addBonus(new Stat("gear sp", null, 0));
		new NamedStat("magi")    .addBonus(new Stat("gear sp", null, 100));
		new NamedStat("archmagi").addBonus(new Stat("gear sp", null, 200));
		new NamedStat("potency").addBonus(new Stat("combustion",		null, 0))
								.addBonus(new Stat("corrosion",			null, 0))
								.addBonus(new Stat("devotion",			null, 0))
								.addBonus(new Stat("glaciation",		null, 0))
								.addBonus(new Stat("impulse",			null, 0))
								.addBonus(new Stat("magnetism",			null, 0))
								.addBonus(new Stat("nullification",		null, 0))
								.addBonus(new Stat("radiance",			null, 0))
								.addBonus(new Stat("reconstruction",	null, 0))
								.addBonus(new Stat("resonance",			null, 0));
		new NamedStat("arcane lore")
				.addBonus(new Stat("fire lore",      null, 0))
				.addBonus(new Stat("acid lore",      null, 0))
				.addBonus(new Stat("healing lore",   null, 0))
				.addBonus(new Stat("kinetic lore",   null, 0))
				.addBonus(new Stat("ice lore",       null, 0))
				.addBonus(new Stat("lightning lore", null, 0))
				.addBonus(new Stat("void lore",      null, 0))
				.addBonus(new Stat("radiance lore",  null, 0))
				.addBonus(new Stat("repair lore",    null, 0))
				.addBonus(new Stat("sonic lore",     null, 0));
		new NamedStat("spell focus mastery")
			.addBonus(new Stat("abjuration focus",		null, 0))
			.addBonus(new Stat("conjuration focus",	null, 0))
			.addBonus(new Stat("divination focus",		null, 0))
			.addBonus(new Stat("enchantment focus",	null, 0))
			.addBonus(new Stat("evocation focus",		null, 0))
			.addBonus(new Stat("illision focus",		null, 0))
			.addBonus(new Stat("necromancy focus",		null, 0))
			.addBonus(new Stat("transmutation focus",	null, 0));
		new NamedStat("deific focus").addBonus(new Stat("spell focus mastery", "sacred", 0));
		new NamedStat("lifesealed").addBonus(new Stat("deathblock",					"default", 1))
								   .addBonus(new Stat("negative energy absorption", "defualt", 50));
		new NamedStat("build combat mastery").addBonus(new Stat("combat mastery", null, 0))
											 .addBonus(new Stat("qp dc",          null, 0));
		new NamedStat("inherent elemental resistance").addBonus(new Stat("acid resistance",        "competence", 0))
													  .addBonus(new Stat("cold resistance",        "competence", 0))
													  .addBonus(new Stat("electricity resistance", "competence", 0))
													  .addBonus(new Stat("fire resistance",        "competence", 0)); // sonic NOT in this set [sic]
		new NamedStat("elemental resistance").addBonus(new Stat("acid resistance",        null, 0))
											 .addBonus(new Stat("cold resistance",        null, 0))
											 .addBonus(new Stat("electricity resistance", null, 0))
											 .addBonus(new Stat("fire resistance",        null, 0))
											 .addBonus(new Stat("sonic resistance",       null, 0));
		new NamedStat("songblade").addBonus(new Stat("perform", "enhancement", 2));

//		new NamedStat("").addBonus(new Stat("", "", 0));
	}
}
