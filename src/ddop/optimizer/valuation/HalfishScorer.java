package ddop.optimizer.valuation;

import ddop.builds.CharBuild;
import ddop.builds.ReaperBuild;
import ddop.builds.adventurerClass.BaseAttackBonusProgression;
import ddop.optimizer.valuation.damage.weapon.InquisitiveAirSalt;
import ddop.optimizer.valuation.damage.weapon.InquisitiveAirSentNightshade;
import ddop.optimizer.valuation.damage.weapon.WeaponAttack;
import ddop.stat.AbilityScore;
import util.NumberFormat;
import util.StatTotals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HalfishScorer extends SpecScorer {
	private final WeaponAttack weaponAttack;
	
	public HalfishScorer(int simCharacterLevel) {
		super(simCharacterLevel);
		this.hitDie = 18;
		
		if(WEAPON_LGS_SALT) {
			this.weaponAttack = new InquisitiveAirSalt();
		} else {
			this.weaponAttack = new InquisitiveAirSentNightshade();
		}
		
		this.addDamageSource(this.weaponAttack);
	}
	
	private static final boolean GLOBE_OF_TRUE_IMPERIAL_BLOOD	= false,
								 DIAMOND_OF_FESTIVE_WISDOM		= false,
								 TOPAZ_OF_GREATER_EVOCATION		= false,
								 SAPPHIRE_OF_GOOD_LUCK_2		= true,
								 ELDRITCH_RESISTANCE_RITUAL		= false,
								 YUGO_POT_WIS					= true,
								 REM_POT_WIS					= true,
								 EVOCATION_FOCUS_FEAT			= false,
								 EXALTED_ANGEL					= false,
								 FUTURE_MAGISTER				= false,
								 LEGENDARY_DREADNOUGHT			= true,
								 SENTIENT_FILIGREES_5_SHATTERED_4_CRACKSHOT_2_SPINES = true,
								 RANGED_DPS_PAST_LIVES			= false,
								 WEAPON_LGS_SALT				= false,
								 WEAPON_SENT_NIGHTSHADE			= true;
	
	private static final int	COOLDOWN_GLACIAL_WRATH  = 4,
								COOLDOWN_IMPLOSION		= 60,
								COOLDOWN_SOUND_BURST	= 3,
								COOLDOWN_GREATER_COMMAND= 10,
								COOLDOWN_SALT			= 1;
	
	private static final double	TARGETS_GLACIAL_WRATH	= 5,
								TARGETS_IMPLOSION		= 5,
								TARGETS_SOUND_BURST     = 3.5,
								TARGETS_GREATER_COMMAND = 3.5;
	
	private static final double ASSUMED_UPTIME_DPS	= 0.5,
								ASSUMED_UPTIME_HEAL	= 0.5,
								ASSUMED_UPTIME_CC	= 0.0;

	@Override
	Set<ArmorType> getAllowedArmorTypes() {
		Set<ArmorType> ret = new HashSet<>();

		ret.add(ArmorType.CLOTH);
		ret.add(ArmorType.LIGHT);
		ret.add(ArmorType.MEDIUM);

		return ret;
	}

	@Override
	BaseAttackBonusProgression getBABProgression() {
		return BaseAttackBonusProgression.MEDIUM;
	}
	
	@Override
	CharBuild getBuild() {
		CharBuild build = new CharBuild();

		build.addStat("light armor proficiency");
		build.addStat("medium armor proficiency");

		build.addStat("strength",     8       + 5 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("dexterity",    17 +2    + 5 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("constitution", 12       + 5 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("intelligence", 10       + 5 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("wisdom",       18 +7 +7 +10 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0) + (DIAMOND_OF_FESTIVE_WISDOM ? 2 : 0) + (YUGO_POT_WIS ? 2 : 0) + (REM_POT_WIS ? 2 : 0));
		build.addStat("charisma",     8       + 5 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		
		build.addStat("striding", 31, "default"); // spell
		
		build.addStat("heal", 3, "inherent");
		build.addStat("all skills", this.getEpicLevel(), "epic skills passive feat");
		build.addStat("heal", 2, "house jorasco favor");
		build.addStat("heal", 2, "??? enhancement");

		build.addStat("percent to-hit",           25, "proficiency");
		build.addStat("percent to-hit",            5, "precision");
		build.addStat("accuracy",                 32);
		build.addStat("accuracy",                  2, "morale");
		build.addStat("deadly",                   32);
		build.addStat("deadly",                    2, "morale");
		build.addStat("ranged power",             79);
		build.addStat("ranged power",             45, "archer's focus");
		build.addStat("ranged alacrity",          11.667, "default"); // spell
		build.addStat("ranged alacrity",          27, "endless fusillade"); // simulated alacrity reduced by 18% uptime; real effect is fixed 213 attacks per minute
		build.addStat("critical threat range",     2);
		build.addStat("critical multiplier",       1);
		build.addStat("armor-piercing",           25);
		build.addStat("doubleshot",               10, "inquisitive");
		build.addStat("doubleshot",               10, "war soul");
		build.addStat("doubleshot",                2, "guild");
		build.addStat("bonus w",			  	   0.25, "guild");
		build.addStat("hp",                      375);
		build.addStat("hp", 					 100, "epic fvs");
		build.addStat("hp",                       20, "guild");
		build.addStat("unconsciousness range",    15, "guild");
		build.addStat("dr",                       10, "default");
		build.addStat("physical sheltering",      11);
		build.addStat("magical sheltering",        1);
		build.addStat("magical sheltering",       20, "quality");
		build.addStat("healing amplification",    60);
		build.addStat("dodge",                     4);
		build.addStat("maximum dodge",             4);
		build.addStat("maximum dodge",            10, "medium armor"); //TODO armor stats
		build.addStat("concealment",		   	  20, "default"); // War Soul lv6 core
		build.addStat("ac",                       30);
		build.addStat("ac",                       33, "armor"); //TODO armor stats
		build.addStat("fortification",            55);
		if(ELDRITCH_RESISTANCE_RITUAL) build.addStat("resistance", 1, "competence");
		if(SAPPHIRE_OF_GOOD_LUCK_2)    build.addStat("resistance", 2, "luck");
		build.addStat("resistance",                2, "morale");
		build.addStat("fortitude saves",          27);
		build.addStat("reflex saves",             27 + (YUGO_POT_WIS ? -4 : 0));
		build.addStat("will saves",               29);
		build.addStat("spell resistance",         32, "default"); // spell
		
		build.addStat("sp",           25, "guild");
		build.addStat("sp",           80, "magical training feat");
		build.addStat("sp",           140,"burst of glacial wrath feat");
		build.addStat("sp",           240,"unyielding sentinel cores");
		build.addStat("percent sp",   10, "endless faith twist");
		build.addStat("potency",      6,  "war soul righteous weapons");
		build.addStat("potency",      30, "scion of the plane of air universal");
		build.addStat("potency",      this.getEpicLevel() * 6, "epic power passive feat");
		build.addStat("devotion",     15, "guild");
		build.addStat("devotion",     10, "beacon of hope cores");
		build.addStat("devotion",     20, "fount of life feat");
		build.addStat("devotion",     25, "healing hands destiny");
		build.addStat("devotion",     30, "healing power twist");
		build.addStat("magnetism",    10, "scion of the plane of air");
		build.addStat("arcane lore",  5,  "magical training feat");
		build.addStat("healing lore", 2,  "beacon of hope cores");
		build.addStat("magical efficiency", this.getEpicLevel(), "epic power passive feat");
		build.addStat("spell focus mastery",	   1, "guild");
		build.addStat("spell focus mastery",	   2, "scion of air");
		build.addStat("evocation focus",	   	   4, "scion of air");
		if(TOPAZ_OF_GREATER_EVOCATION) build.addStat("evocation focus", 2, "augment");
		build.addStat("spell penetration",		   1, "guild");
		
		build.addStat("wisdom",				1, "aureon's instruction");
		build.addStat("spell penetration",	1, "aureon's instruction");

		build.addStat("slippery mind",		1); // Inquisitive lv18 core
		build.addStat("true seeing",		1); // Inquisitive lv12 core
		
		build.addStat("vulnerability",		1); // War Soul Smite Weakness
		
		build.addStat("ghost touch",		1); // Holy Strike feat
		
		if(WEAPON_LGS_SALT) {
			build.addStat("weapon enhancement bonus", 14, "default");
			build.addStat("bonus w",	5, "default");
			build.addStat("[w]",		5.5, "default");
		}
		if(WEAPON_SENT_NIGHTSHADE) {
			build.addStat("weapon enhancement bonus", 15, "default");
			build.addStat("bonus w",	5.5, "default"); // TODO: weapon stats
			build.addStat("[w]",		6.5, "default"); // TODO: weapon stats
		}
		
		if(LEGENDARY_DREADNOUGHT) {
			build.addStat("hp",							60, "legendary dreadnought core");
			build.addStat("damage vs helpless",			15, "legendary dreadnought core");
			build.addStat("ranged alacrity",			40, "endless fusillade"); // simulated alacrity reduced to 27% uptime; real effect is fixed 213 attacks per minute while up
			build.addStat("overwhelming critical",		 1, "legendary dreadnought devastating critical");
			build.addStat("ranged power",		 		70, "legendary dreadnought master's blitz");
			build.addStat("physical sheltering",		30, "legendary dreadnought master's blitz");
			build.addStat("constitution",				 6, "legendary dreadnought");
		}
		
		if(EXALTED_ANGEL) {
			build.addStat("wisdom",						6, "exalted angel");
			build.addStat("wisdom",						1, "exalted angel reborn in light");
			build.addStat("spell focus mastery",		3, "exalted angel core");
		}
		
		if(FUTURE_MAGISTER) {
			build.addStat("spell focus mastery",		6, "future magister");
			if(EVOCATION_FOCUS_FEAT) build.addStat("evocation focus", 3, "future magister master of evocation");
			build.addStat("spell penetration",			3, "magister piercing spellcraft");
		}
		
		if(EVOCATION_FOCUS_FEAT) {
			build.addStat("evocation focus",	1, "feat");
			build.addStat("evocation focus",	2, "draconic incarnation precise evocation");
			build.addStat("evocation focus",	3, "magister evocation specialist");
		}
		
		if(SENTIENT_FILIGREES_5_SHATTERED_4_CRACKSHOT_2_SPINES) {
			build.addStat("ranged power",			40, "sentient filigree");
			build.addStat("accuracy",				 5, "sentient filigree");
			build.addStat("deadly",					 7, "sentient filigree");
			build.addStat("doubleshot",				 8, "sentient filigree");
			build.addStat("armor-piercing",			 5, "sentient filigree");
			build.addStat("physical sheltering",	15, "sentient filigree");
			build.addStat("magical sheltering",		 7, "sentient filigree");
			build.addStat("reflex saves",			 2, "sentient filigree");
			build.addStat("fire absorption",		 5, "sentient filigree");
			build.addStat("diplomacy",				15, "sentient filigree");
			build.addStat("prr reduction",			 5, "sentient filigree");
			build.addStat("mrr reduction",			 5, "sentient filigree");
			build.addStat("mp reduction",			 5, "sentient filigree");
			build.addStat("rp reduction",			 5, "sentient filigree");
		}
		
		if(RANGED_DPS_PAST_LIVES) {
			build.addStat("doubleshot",					9, "primal past life stance");
//			build.addStat("accuracy",					6, "primal past life stance");
			build.addStat("weapon enhancement bonus",	3, "arcane past life stance");
			build.addStat("accuracy",					3, "fighter past life");
			build.addStat("deadly",						3, "monk past life");
			build.addStat("deadly",						6, "ranger past life");
			build.addStat("sneak attack damage",		3, "rogue past life");
		}
		
		/*Shattered 5		2.7 / 1.5
		 * Spines 2			3.4
		 * Crackshot 4		2.9
		 * 
		 * Total Set 2
		 * 40 RP
		 *  5 accuracy
		 *  7 deadly
		 *  8 % doubleshot
		 *  5 % armor-piercing
		 *  15 PRR
		 *  7 MRR
		 *  2 reflex saves
		 *  5 % fire abs
		 * 15 diplomacy
		 * 
		 */
		
		// TODO spell DCs
		
		return build;
	}
	
	@Override
	ReaperBuild getReaperBuild() {
		return new ReaperBuild("X:\\src\\DDOP\\saved builds\\halfish reaper build.txt", null);
	}
	
	@Override
	protected double scoreDCs(StatTotals stats) {
		int wisdom			= this.getAbilityScore(AbilityScore.WISDOM, stats);
		int wisMod			= this.getAbilityMod(AbilityScore.WISDOM, stats);
		
		int evocationDC	= stats.getInt("evocation focus");
		int enchantDC   = stats.getInt("enchantment focus");
		
		int glacialWrathDC		= 20 + wisMod + evocationDC; // fort
		int implosionDC			= 19 + wisMod + evocationDC; // fort
		int soundBurstDC  		= 19 + wisMod + evocationDC; // fort
		int greaterCommandDC	= 19 + wisMod + enchantDC;   // will
		
		double glacialWrathDCRate	= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (glacialWrathDC		- SIM_ENEMY_SAVES_FORT_LOW)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (glacialWrathDC		- SIM_ENEMY_SAVES_FORT_MED)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (glacialWrathDC		- SIM_ENEMY_SAVES_FORT_HIGH) * 0.05, 0.95);
		double implosionDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (implosionDC			- SIM_ENEMY_SAVES_FORT_LOW)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (implosionDC			- SIM_ENEMY_SAVES_FORT_MED)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (implosionDC			- SIM_ENEMY_SAVES_FORT_HIGH) * 0.05, 0.95);
		double soundBurstDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (soundBurstDC		- SIM_ENEMY_SAVES_FORT_LOW)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (soundBurstDC		- SIM_ENEMY_SAVES_FORT_MED)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (soundBurstDC		- SIM_ENEMY_SAVES_FORT_HIGH) * 0.05, 0.95);
		double greaterCommandDCRate	= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (greaterCommandDC	- SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (greaterCommandDC	- SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (greaterCommandDC	- SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 0.95);
		double saltRate				= this.weaponAttack.getHitAnyRate(stats);
		
		double glacialWrathScore	= VALUATION_STUN  * SIM_STUNNABLE_PORTION	* glacialWrathDCRate	* glacialWrathDCRate	* TARGETS_GLACIAL_WRATH		/ COOLDOWN_GLACIAL_WRATH	* ASSUMED_UPTIME_CC;
		double implosionScore		= VALUATION_DEATH * SIM_DEATHABLE_PORTION	* implosionDCRate		* implosionDCRate		* TARGETS_IMPLOSION			/ COOLDOWN_IMPLOSION		* ASSUMED_UPTIME_CC;
		double soundBurstScore		= VALUATION_STUN  * SIM_STUNNABLE_PORTION	* soundBurstDCRate		* soundBurstDCRate		* TARGETS_SOUND_BURST		/ COOLDOWN_SOUND_BURST		* ASSUMED_UPTIME_CC;
		double greaterCommandScore	= VALUATION_TRIP  * SIM_COMMANDABLE_PORTION	* greaterCommandDCRate	* greaterCommandDCRate	* TARGETS_GREATER_COMMAND	/ COOLDOWN_GREATER_COMMAND	* ASSUMED_UPTIME_CC;
		double saltScore			= VALUATION_SALT  * SIM_SALTABLE_PORTION	* saltRate				* saltRate											/ COOLDOWN_SALT				* ASSUMED_UPTIME_DPS; // Assumed attacking uptime
		if(!WEAPON_LGS_SALT) saltScore = 0;
		
		double killsPerSecondEquivalent	= (glacialWrathScore + implosionScore + soundBurstScore + greaterCommandScore) / 3 + saltScore;		// First 4 over 3, to take average (I won't CC when targets already CCd) but also to value options (I'll pick the best one).
		double dcsScore					= killsPerSecondEquivalent * SIM_ENEMY_HP * VALUATION_DCS;
		
		if(this.verbose) {
			System.out.println("ShintaoScorer DCs Debug Log\n"
					+ "+- Wisdom:    " + wisdom				+ " (+" + wisMod								+ ")\n"
					+ "+- GlaclWrath:" + glacialWrathDC		+ " ("  + NumberFormat.percent(glacialWrathDCRate)		+ ")\n"
					+ "+- Implosion: " + implosionDC		+ " ("  + NumberFormat.percent(implosionDCRate)		+ ")\n"
					+ "+- SoundBurst:" + soundBurstDC		+ " ("  + NumberFormat.percent(soundBurstDCRate)		+ ")\n"
					+ "+- GtrCommand:" + greaterCommandDC	+ " ("  + NumberFormat.percent(greaterCommandDCRate)	+ ")"
				    + (WEAPON_LGS_SALT ? "\n+- Salt:      " + NumberFormat.percent(saltRate) : ""));
		}
		
		return dcsScore;
	}
	
	@Override
	protected boolean hasDCs() { return true; }
	
	private static class HealingStats {
		private static final int empHealCost = 10;
		private static final int empHealPower = 75;
		private static final int quickenCost = 10;
		private static final int enlargeCost = 10;
		
		private final int devotion, lore;
		protected final double power, critSustain, critBurst, discount;
		protected final int SP;
		
		protected HealingStats(SpecScorer owner, StatTotals stats) {
			this.devotion = stats.getInt("devotion") + stats.getInt("heal") + owner.getAbilityMod(AbilityScore.WISDOM, stats);
			this.power = (this.devotion + empHealPower) / 100.0;
			this.lore = stats.getInt("healing lore");
			double crit = this.lore / 100.0;
			this.critSustain = (1 + crit);
			this.critBurst = (1 + crit * crit);  // Heal crit are much more valuable when they're reliable.
			this.SP = owner.getSP(stats);
			this.discount = (stats.get("magical efficiency")) / 100.0;
		}
		
		protected int metamagicsCost() {
			return empHealCost + quickenCost + enlargeCost;
		}
		
		public void printVerboseDebugLog() {
			System.out.println( "+- SP:        " + this.SP);
			System.out.println( "+- Devotion:  " + this.devotion);
			System.out.println( "+- Heal Lore: " + this.lore);
			System.out.println( "+- Efficiency:" + util.NumberFormat.percent(this.discount));
		}
	}
	
	private enum HealingRotationPortion {
		MAX_EFFICIENCY,
		BALANCED,
		MAX_THROUGHPUT
	}
	
	private static class HealingSpell {
		private final String name;
		protected int minHeal, maxHeal, spCost, numberOfHeals = 1;
		protected double powerScaling = 1.0, cooldown, animationTime, castPeriod;
		protected HealingStats castsWith;
		protected HealingRotationPortion role;
		
		protected HealingSpell(String name) {
			this.name = name;
		}
		
		protected HealingSpell setHealingRange(int minBaseHealing, int maxBaseHealing) {
			this.minHeal = minBaseHealing;
			this.maxHeal = maxBaseHealing;
			return this;
		}
		
		protected HealingSpell setCost(int sp) {
			this.spCost = sp;
			return this;
		}
		
		protected HealingSpell setPowerScaling(double spellPowerScalingCoefficient) {
			this.powerScaling = spellPowerScalingCoefficient;
			return this;
		}
		
		public HealingSpell setNumberOfHeals(int count) {
			this.numberOfHeals = count;
			return this;
		}
		
		protected HealingSpell setTimeLimitations(double cooldown, double animation, double castPeriod) {
			this.cooldown = cooldown;
			this.animationTime = animation;
			this.castPeriod = castPeriod;
			return this;
		}
		
		protected void setHealingStats(HealingStats hs) {
			this.castsWith = hs;
		}
		
		protected HealingSpell setRotationRole(HealingRotationPortion role) {
			this.role = role;
			return this;
		}
		
		protected int getCost() {
			int rawCost = this.spCost + this.castsWith.metamagicsCost();
			int discount = (int) (rawCost * this.castsWith.discount);
			return rawCost - discount;
		}
		
		protected double getHealing() {
			int min = (int) (this.minHeal * (1 + this.castsWith.power * this.powerScaling));
			int max = (int) (this.maxHeal * (1 + this.castsWith.power * this.powerScaling));
			double average = (min + max) / 2;
			return average * this.numberOfHeals * this.getCritValuation();
		}
		
		protected double getCritValuation() {
			if(this.numberOfHeals > 1) return this.castsWith.critSustain;
			switch(this.role) {
				case MAX_EFFICIENCY:
					return this.castsWith.critSustain;
				case BALANCED:
					return (this.castsWith.critSustain + this.castsWith.critBurst) / 2;
				case MAX_THROUGHPUT:
					return this.castsWith.critBurst;
			}
			return 0;
		}
		
		/** Healing per Second. How fast it heals.
		 * High scores here mean it's good at healing through constant massive damage.
		 * Not to be confused with HpET: Healing per Execution Time. */
		protected double getHPS() {
			return this.getHealing() / this.castPeriod;
		}
		
		/** Healing per Mana. How efficient it is.
		 * High scores here mean it's good for not running out of SP. */
		protected double getHPM() {
			return this.getHealing() / this.getCost();
		}
		
		/** Mana per Second. How fast using this spell continually will drain your SP bar. */
		protected double getMPS() {
			return this.getCost() / this.castPeriod;
		}
		
		public boolean isUsedIn(HealingRotationPortion portion) {
			switch(portion) {
				case MAX_THROUGHPUT:
					return true;
				case BALANCED:
					return this.role == HealingRotationPortion.BALANCED
							|| this.role == HealingRotationPortion.MAX_EFFICIENCY;
				case MAX_EFFICIENCY:
					return this.role == HealingRotationPortion.MAX_EFFICIENCY;
			}
			return false;
		}
		
		public String getTagLine() {
			return this.name;
		}
		
		public void printVerboseDebugLog() {
			System.out.println( "+- " + this.getTagLine() + ": " +
										util.NumberFormat.readableLargeNumber(this.getHealing()) +
										" (" + this.getCost() + " SP)");
		}
	}
	
	private static class HealingSLA extends HealingSpell {
		protected HealingSLA(String name) { super(name); }
		
		@Override
		protected int getCost() { return this.spCost; }
		@Override
		public String getTagLine() { return super.getTagLine() + " (SLA)";}
	}
	
	private static class HealingSpellbook {
		private final String name;
		private HealingStats castsWith;
		private final Collection<HealingSpell> spells = new ArrayList<>();
		
		public HealingSpellbook(String name) {
			this.name = name;
		}
		
		protected HealingSpellbook addSpell(HealingSpell hs) {
			hs.setHealingStats(this.castsWith);
			this.spells.add(hs);
			return this;
		}
		
		protected HealingSpellbook setHealingStats(HealingStats hs) {
			this.castsWith = hs;
			for(HealingSpell spell : this.spells) spell.setHealingStats(hs);
			return this;
		}
		
		public double getHPS(HealingRotationPortion portion) {
			double HPS = 0.0;
			for(HealingSpell spell : this.spells) if(spell.isUsedIn(portion)) {
				HPS += spell.getHPS();
			}
			return HPS;
		}
		
		public double getContinuousCastingDuration(HealingRotationPortion portion) {
			double MPS = 0.0;
			for(HealingSpell spell : this.spells) if(spell.isUsedIn(portion)) {
				MPS += spell.getMPS();
			}
			
			double SP = this.castsWith.SP;
			
			return SP / MPS;
		}
		
		public void printVerboseDebugLog() {
			System.out.println("== Healing Spellbook \"" + this.name + "\" Debug Log ==");
			
			this.castsWith.printVerboseDebugLog();
			System.out.println("|");
			for(HealingSpell spell : this.spells) {
				spell.printVerboseDebugLog();
			}
		}
	}
	
	@Override
	protected double scoreHealing(StatTotals stats) {
		HealingStats hs = new HealingStats(this, stats);
		
		HealingSpell closeWounds = new HealingSLA("Close Wounds")
			.setHealingRange(7, 9)
			.setCost(2)
			.setTimeLimitations(1.0, 0.2, 1.0)
			.setRotationRole(HealingRotationPortion.BALANCED);
		
		HealingSpell renewal = new HealingSLA("Renewal")
				.setHealingRange(24, 29)
				.setCost(5)
				.setNumberOfHeals(4)
				.setTimeLimitations(4.0, 0.5, 7.0)
				.setRotationRole(HealingRotationPortion.MAX_EFFICIENCY);
		
		HealingSpell beaconOfGrace = new HealingSLA("Beacon of Grace")
				.setHealingRange(20, 20)
				.setCost(6)
				.setTimeLimitations(6.0, 0.6667, 6.0)
				.setRotationRole(HealingRotationPortion.BALANCED);
		
		HealingSpell healSpell = new HealingSpell("Heal")
				.setHealingRange(150, 150)
				.setCost(40)
				.setPowerScaling(0.50)
				.setTimeLimitations(6.0, 0.6667, 6.0)
				.setRotationRole(HealingRotationPortion.MAX_THROUGHPUT);
		
		HealingSpellbook favoredSoulSpells = new HealingSpellbook("Favored Soul")
				.addSpell(closeWounds)
				.addSpell(renewal)
				.addSpell(beaconOfGrace)
				.addSpell(healSpell)
				.setHealingStats(hs);
		
		double VALUATION_HEALING_LOW_INTENSITY_PORTION = 0.4;
		double VALUATION_HEALING_SUSTAIN_PORTION = 0.3;
		double VALUATION_HEALING_MAX_THROUGHPUT_PORTION = 0.3;
		
		double VALUATION_HEALING_LOW_INTENSITY_RELATIVE_HPS_REQUIREMENT = 0.25;
		double VALUATION_HEALING_SUSTAIN_RELATIVE_HPS_REQUIREMENT = 0.5;
		double VALUATION_HEALING_MAX_THROUGHPUT_RELATIVE_HPS_REQUIREMENT = 1;
		
		double maxHPS = favoredSoulSpells.getHPS(HealingRotationPortion.MAX_THROUGHPUT);
		double sustain = favoredSoulSpells.getContinuousCastingDuration(HealingRotationPortion.MAX_THROUGHPUT);
		double healsPerBar = 1; // TODO
		
		double hpsScore = maxHPS;
		double sustainScore = sustain;
		double efficiencyScore = healsPerBar;
		
		double score = Math.sqrt(hpsScore * sustainScore * efficiencyScore) * 3;
		
		if(verbose) {
			favoredSoulSpells.printVerboseDebugLog();
			System.out.println("|");
			System.out.println("+- Max HPS:   " + (int) maxHPS);
			System.out.println("+- Sustain:   " + (int) sustain);
//			System.out.println("+- Capacity:  " + (int) healsPerBar); TODO
		}
		
		return score;
	}
	
	@Override
	protected boolean valuesHealing() { return true; }
	
	private static final int[] FAVORED_SOUL_BASE_SP = new int[] {
		 0,  100,  150,  200,  250,  300,
			 355,  415,  480,  550,  625,
			 705,  790,  880,  975, 1075,
			1180, 1290, 1405, 1525, 1650
	};
	
	@Override
	protected int getBaseSPFromLevel(int level) { return FAVORED_SOUL_BASE_SP[level]; }
	
	@Override
	protected AbilityScore getCastingAbility() { return AbilityScore.WISDOM; }
	
	@Override
	protected double getGearSPMultiplier() { return 2; }
}
