package ddop.optimizer.valuation;

import ddop.builds.CharBuild;
import ddop.builds.GuildBuffs;
import ddop.builds.ReaperBuild;
import ddop.builds.adventurerClass.BaseAttackBonusProgression;
import ddop.constants.Tier;
import ddop.optimizer.valuation.damage.spell.Spell;
import ddop.stat.AbilityScore;
import ddop.stat.StatSource;
import util.NumberFormat;
import util.StatTotals;

import java.util.ArrayList;
import java.util.Collection;

public class HealbardScorer extends SpecScorer {
	public HealbardScorer(int simCharacterLevel) {
		super(simCharacterLevel);

		this.characterLevel = 26;
		this.hitDie = 6;

		Spell reverb = new Spell("Reverberate", 11.5 * 3 * 8 * 0.5).setSonic(); // 1d10+6 base * 3 stacks * 8 ticks * 50% (assumed effectiveness since things die fast)
		reverb.cooldown = 15;
		this.addDamageSource(reverb);

		Spell greaterShout = new Spell("Greater Shout", 90 * 6).setSonic(); // 20d3+60 base * 6 targets (AoE, estimate)
		greaterShout.cooldown = 6 / 0.5; // 6 base / 50% (estimate, potion of the time that it's safe to get close)
		this.addDamageSource(greaterShout);
	}

	/* Filigree plans:
	 *
	 * Splendid Cacophony x4
	 * +12 Devotion
	 * +1 CHA			+2% doublestrike & doubleshot
	 * +1 CON			+20 Devotion
	 * +3 MRR			+1 Inspire Courage
	 *
	 * Hell & Back / Embraced by Light x2
	 * +2 CHA			+5 Heal Amp
	 * +2 CHA			+5 PRR
	 *
	 * Embraced by Light x3
	 * +12 Devotion		+10 Spellpower
	 *
	 * Hell & Back x3
	 * +1 CHA			+10 MRR
	 *
	 */
	
	private static final boolean GLOBE_OF_TRUE_IMPERIAL_BLOOD	= false,
								 YUGO_POT_CHA					= false,
								 REM_POT_CHA                    = true,
								 STAT_TOMES                     = true;

	private static final double	COOLDOWN_MASS_HOLD	= 6,
								COOLDOWN_CAPERING	= 12,
								COOLDOWN_OTTOS_IRR = 5.5;

	private static final double	TARGETS_MASS_HOLD	= 6,
								TARGETS_CAPERING	= 1,
								TARGETS_OTTOS_IRR	= 1;

	@Override
	BaseAttackBonusProgression getBABProgression() {
		return BaseAttackBonusProgression.MEDIUM;
	}
	
	@Override
	StatSource getBuild() {
		CharBuild build = new CharBuild();

		build.addStat("light armor proficiency");
		build.addStat("medium armor proficiency");

		build.addStat("strength",     8 , "base");
		build.addStat("dexterity",    14, "base");
		build.addStat("constitution", 14, "base");
		build.addStat("intelligence", 8 , "base");
		build.addStat("wisdom",       8 , "base");
		build.addStat("charisma",     18, "base");

		build.addStat("charisma",     this.characterLevel / 4, "level up");

		build.addStat("charisma",     1, "spellsinger charisma");
		build.addStat("charisma",     4, "spellsinger capstone");
		build.addStat("charisma",     1, "warchanter charisma");
		build.addStat("charisma",     1, "fatesinger divine hymn");

		if(YUGO_POT_CHA)
			build.addStat("charisma", 2, "yugoloth");
		if(REM_POT_CHA)
			build.addStat("charisma", 2, "lasting");

		if(STAT_TOMES)
			build.addStat("well rounded", 8, "inherent");
		if(GLOBE_OF_TRUE_IMPERIAL_BLOOD)
			build.addStat("well rounded", 1, "exceptional");
		build.addStat("well rounded", 1, "fatesinger glitter of fame");

		build.addStat("striding", 25, "default"); // spell
		build.addStat("striding", this.getClassLevel(), "swashbuckler fast movement");

		build.addStat("all skills", this.getEpicLevel(), "epic skills passive feat");

		// Defenses
		build.addStat("hp", 11, "??? Unknown Feat Bonus");
		build.addStat("hp", 30, "Heroic Durability");
		build.addStat("hp", 5 * cap(1, this.characterLevel / 5, 3), "Improved Heroic Durability");
		build.addStat("hp", 10, "warchanter weapon training");
		build.addStat("hp", 10, "warchanter fighting spirit");
		build.addStat("dodge", 1, "swashbuckler cores");
		build.addStat("dodge", 3, "swashbuckler on your toes");
		build.addStat("dodge", 3, "fatesinger fourth harmonic chord");
		build.addStat("maximum dodge",  8, "medium armor"); //TODO armor stats
		build.addStat("maximum dodge",  1, "swashbuckler cores");
		build.addStat("concealment",   20, "default"); // blur spell
		build.addStat("ac", 15, "armor"); //TODO armor stats
		build.addStat("ac",  2, "warchanter rough and ready");
		build.addStat("ac",  3, "fatesinger fourth harmonic chord");
		build.addStat("prr",  2, "warchanter rough and ready");
		build.addStat("prr",  3, "fatesinger third harmonic chord");
		build.addStat("prr", 10, "fatesinger divine hymn");
		build.addStat("healing amplification", 10, "fatesinger third harmonic chord");
		build.addStat("force of personality", 1, "feat");
		build.addStat("resistance",   1, "fatesinger glitter of fame");
		build.addStat("resistance",   3, "fatesinger fourth harmonic chord");
		build.addStat("reflex saves", 1, "swashbuckler cores");
		build.addStat("spell resistance", 3, "fatesinger the fifth chord");
		build.addStat("deflect arrows", 1, "swashbuckler deflect arrows");

		// Spellcasting
		build.addStat("sp", 5 + 5 * this.characterLevel, "mental toughness feat");
		build.addStat("sp", 5 + 5 * this.characterLevel, "improved mental toughness feat");
		build.addStat("sp", 20, "spellsinger magical studies");
		build.addStat("sp", 80, "spellsinger magical training");
		build.addStat("sp", -90, "enchant weapon");
		build.addStat("sp", 150, "fatesinger levels");
		build.addStat("sp", 140, "embolden spell");
		build.addStat("potency",  45, "spellsinger ap");
		build.addStat("potency",  25, "spellsinger capstone");
		build.addStat("potency",  this.getEpicLevel() * 6, "epic power passive feat");
		build.addStat("potency",  60, "fatesinger levels");
		build.addStat("potency",  25, "fatesinger majesty");
		build.addStat("devotion", 25, "healing hands destiny");
		build.addStat("arcane lore",   1,  "mental toughness feat");
		build.addStat("arcane lore",   1,  "improved mental toughness feat");
		build.addStat("arcane lore",   5,  "spellsinger magical training");
		build.addStat("healing lore", 10,  "spellsinger cores");
		build.addStat("magical efficiency", this.getEpicLevel(), "epic power passive feat");

		// DCs
		build.addStat("enchantment focus", 1, "spell focus feat");
		build.addStat("enchantment focus", 1, "greater spell focus feat");
		build.addStat("enchantment focus", 1, "spellsinger yellow marigold crown");
		build.addStat("enchantment focus", 2, "spellsinger prodigy");
		build.addStat("spell focus mastery", 4,  "spellsinger cores");
		build.addStat("spell focus mastery", 2, "embolden spell epic feat");
		build.addStat("spell penetration", 2, "spell penetration feat");

		// Skills
		build.addStat("all skills", this.getEpicLevel(), "epic skills passive feat");
		build.addStat("all skills", 3, "fatesinger second harmonic chord");

		// Songs
		build.addStat("perform", 23,  "ranks");
		build.addStat("perform",  5,  "spellsinger cores");
		build.addStat("perform",  2,  "spellsinger prodigy");
		build.addStat("perform",  5,  "epic skill focus");
		build.addStat("bard songs", 20, "bard levels");
		build.addStat("bard songs", 2,  "spellsinger virtuoso");
		build.addStat("bard songs", 1,  "swashbuckler tavern shanties");
		build.addStat("bard songs", 12,  "fatesinger levels");
		build.addStat("bardic inspiration level", 20, "bard levels");
		build.addStat("bardic inspiration level", 8,  "spellsinger cores");
		build.addStat("bardic inspiration level", 1,  "spellsinger spell song trance");
		build.addStat("bardic inspiration level", 1,  "spellsinger raucus refrain");
		build.addStat("bardic inspiration level", 1,  "spellsinger song of arcane might");
		build.addStat("bardic inspiration level", 1,  "spellsinger frolic");
		build.addStat("bardic inspiration level", 1,  "spellsinger arcane aid");
		build.addStat("bardic inspiration level", 1,  "spellsinger sustaining song");
		build.addStat("bardic inspiration level", 1,  "spellsinger spell song vigor");
		build.addStat("bardic inspiration level", 1,  "spellsinger prodigy");
		build.addStat("bardic inspiration level", 12,  "fatesinger levels");

		// Ability Buffs
		build.addStat("well rounded", 2, "inspire excellence feat buff");

		// Defensive Buffs
		build.addStat("dodge", 4, "bard inspire heroics buff");
		build.addStat("dodge", 3, "fatesinger fourth harmonic chord buff");
		build.addStat("ac", 4, "bard inspire heroics buff");
		build.addStat("ac", 6, "fatesinger grandeur buff");
		build.addStat("ac", 3, "fatesinger fourth harmonic chord buff");
		build.addStat("prr", 3, "bard inspire greatness buff");
		build.addStat("prr", 6, "warchanter ironskin buff");
		build.addStat("prr", 6, "fatesinger grandeur buff");
		build.addStat("prr", 3, "fatesinger third harmonic chord buff");
		build.addStat("healing amplification",  10, "bard inspire greatness buff");
		build.addStat("healing amplification",  10, "fatesinger third harmonic chord buff");
		build.addStat("negative amplification", 10, "spellsinger music of the dead buff");
		build.addStat("negative amplification", 10, "fatesinger third harmonic chord buff");
		build.addStat("repair amplification",   10, "spellsinger music of the makers buff");
		build.addStat("repair amplification",   10, "fatesinger third harmonic chord buff");
		build.addStat("resistance", 4, "bard inspire heroics buff");
		build.addStat("resistance", 1, "warchanter song of heroism buff");
		build.addStat("resistance", 3, "fatesinger fourth harmonic chord buff");
		build.addStat("spell resistance",       3, "fatesinger the fifth chord buff");
		build.addStat("freedom of movement",    1, "spellsinger frolic buff");
		build.addStat("fear immunity",          1, "warchanter song of heroism buff");
		build.addStat("damage reduction",       6, "warchanter ironskin buff");
		build.addStat("resist elements",        6, "warchanter arcane shield buff");

		// Weapons Buffs
		build.addStat("accuracy", 1, "bard bardic ballad buff");
		build.addStat("accuracy", 1, "bard soothing song buff");
		build.addStat("accuracy", 2, "bard improved inspire courage buff");
		build.addStat("accuracy", 1, "enchant weapon buff");
		build.addStat("accuracy", 2, "warchanter song of heroism buff");
		build.addStat("accuracy", 3, "warchanter inspire bravery buff");
		build.addStat("accuracy", 3, "spellsinger first harmonic chord buff");
		build.addStat("deadly", 1, "bard bardic ballad buff");
		build.addStat("deadly", 1, "bard soothing song buff");
		build.addStat("deadly", 2, "bard improved inspire courage buff");
		build.addStat("deadly", 1, "enchant weapon buff");
		build.addStat("deadly", 3, "warchanter cores buff");
		build.addStat("deadly", 3, "spellsinger first harmonic chord buff");
		build.addStat("doublestrike", 6,  "warchanter recklessness buff");
		build.addStat("doubleshot",   6,  "warchanter recklessness buff");
		build.addStat("true seeing",  1, "fatesinger the fifth chord buff");

		// Spellcasting Buffs
		build.addStat("sp", 600, "spellsinger spell song vigor buff");
		build.addStat("sp",  10, "spellsinger prodigy buff");
		build.addStat("magical efficiency", 10, "spellsinger spell song trance buff");
		build.addStat("potency", 3, "bard bardic ballad buff");
		build.addStat("potency", 3, "bard soothing song buff");
		build.addStat("potency", 6, "bard improved inspire courage buff");
		build.addStat("potency", 9, "warchanter recklessness buff");
		build.addStat("potency", 9, "spellsinger first harmonic chord buff");
		build.addStat("arcane augmentation", 1,  "spellsinger song of arcane might buff");

		// DCs Buffs
		build.addStat("combat mastery",      1,  "fatesinger the fifth chord buff");
		build.addStat("spell focus mastery", 1,  "spellsinger spell song trance buff");
		build.addStat("spell focus mastery", 1,  "fatesinger the fifth chord buff");
		build.addStat("spell penetration",   3,  "spellsinger arcane aid buff");

		// Skills Buffs
		build.addStat("all skills", 3, "fatesinger second harmonic chord buff");
		build.addStat("all skills", 4, "bard inspire competence buff");

		// Sustaining Song
		// 2d3 healing per 2sec.

		// Inspire Greatness
		// 2x(CHA total) temp HPs per 6sec.

		return build.combine(new GuildBuffs(false, Tier.getTierByLevel(this.characterLevel)));
	}
	
	@Override
	ReaperBuild getReaperBuild() {
		return new ReaperBuild("X:\\src\\DDOP\\saved builds\\conductrice reaper build.txt", null);
	}

	@Override
	protected double scoreDCs(StatTotals stats) {
		int charisma = this.getAbilityScore(AbilityScore.CHARISMA, stats);
		int chaMod   = this.getAbilityMod  (AbilityScore.CHARISMA, stats);

		int casterLevel = this.getClassLevel() + (this.characterLevel >= 20 ? 5 : 0) + stats.getInt("arcane augmentation");
		int spellPenetration = stats.getInt("spell penetration");

		int enchantDC = stats.getInt("enchantment focus");
		int perform   = stats.getInt("perform") + chaMod;

		int massHoldDC = 16 + chaMod + enchantDC + 5 /* hypnotism */; // will
		int caperingDC = 10 + perform;                                // will

		double massHoldDCRate = SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (massHoldDC - SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 0.95)
							  + SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (massHoldDC - SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 0.95)
							  + SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (massHoldDC - SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 0.95);
		double caperingDCRate = SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (caperingDC - SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 0.95)
							  + SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (caperingDC - SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 0.95)
							  + SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (caperingDC - SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 0.95);
		double spellPenetrationRate	= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (casterLevel + spellPenetration - SIM_ENEMY_SR_LOW)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (casterLevel + spellPenetration - SIM_ENEMY_SR_MED)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (casterLevel + spellPenetration - SIM_ENEMY_SR_HIGH) * 0.05, 0.95);
		double netSpellPen = (1 - SIM_ENEMY_HAS_SR_PORTION) + SIM_ENEMY_HAS_SR_PORTION * spellPenetrationRate;

		double massHoldScore	= VALUATION_STUN  * SIM_HOLDABLE_PORTION  * massHoldDCRate	* netSpellPen * TARGETS_MASS_HOLD	/ COOLDOWN_MASS_HOLD;
		double caperingScore	= VALUATION_DANCE * SIM_DANCEABLE_PORTION * caperingDCRate	* netSpellPen * TARGETS_CAPERING	/ COOLDOWN_CAPERING;
		double ottosIrrScore	= VALUATION_DANCE * SIM_DANCEABLE_PORTION					* netSpellPen * TARGETS_OTTOS_IRR	/ COOLDOWN_OTTOS_IRR;

		double killsPerSecondEquivalent	= (massHoldScore + caperingScore + ottosIrrScore) / 2;		// Over 2, to take average (I won't CC when targets already CCd) but also to value options (I'll pick the best one).
		double dcsScore					= killsPerSecondEquivalent * SIM_ENEMY_HP * VALUATION_DCS;

		if(this.verbose) {
			System.out.println("HealbardScorer DCs Debug Log\n"
					+ "+- Charisma:  " + charisma			+ " (+"	+ chaMod + ")\n"
					+ "+- Caster Lv: " + casterLevel		+ "\n"
					+ "+- Spell Pen: " + spellPenetration   + " ("	+ NumberFormat.percent(spellPenetrationRate) + " / " + NumberFormat.percent(netSpellPen) + ")\n"
					+ "+- Perform:   " + perform			+ "\n"
					+ "+- Mass Hold: " + massHoldDC			+ " ("  + NumberFormat.percent(massHoldDCRate) + ")\n"
					+ "+- Capering:  " + caperingDC			+ " ("  + NumberFormat.percent(caperingDCRate) + ")\n"
					+ "+- Ottos Irr: No Save\n");
		}

		return dcsScore;
	}

	@Override
	protected boolean hasDCs() { return true; }
	
	private static class HealingStats {
		private static final int quickenCost = 10;
		private static final int enlargeCost = 10;
		
		private final int devotion, lore;
		protected final double power, critSustain, critBurst, discount;
		protected final double critMultiplier;
		protected final int SP;
		
		protected HealingStats(SpecScorer owner, StatTotals stats) {
			this.devotion = stats.getInt("devotion") + stats.getInt("heal") + owner.getAbilityMod(AbilityScore.WISDOM, stats);
			this.power = this.devotion / 100.0;
			this.lore = stats.getInt("healing lore");
			double crit = this.lore / 100.0;
			this.critSustain = (1 + crit);
			this.critBurst = (1 + crit * crit);  // Heal crit are much more valuable when they're reliable.
			this.critMultiplier = (1 + stats.getInt("Healing Critical Multiplier") / 100.0);
			this.SP = owner.getSP(stats);
			this.discount = (stats.get("magical efficiency")) / 100.0;
		}
		
		protected int metamagicsCost() {
			return quickenCost + enlargeCost;
		}
		
		public void printVerboseDebugLog() {
			System.out.println( "+- SP:         " + this.SP);
			System.out.println( "+- Devotion:   " + this.devotion);
			System.out.println( "+- Heal Crit:  " + this.lore + "% / x" + NumberFormat.readableLargeNumber(1 + this.critMultiplier));
			System.out.println( "+- Efficiency: " + NumberFormat.percent(this.discount));
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

		protected HealingSpell setHealing(int healing) {
			return this.setHealingRange(healing, healing);
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
			double average = (min + max) / 2.0;
			return average * this.numberOfHeals * this.getCritValuation();
		}
		
		protected double getCritValuation() {
			HealingRotationPortion role = this.role;
			if(this.numberOfHeals > 1) role = HealingRotationPortion.MAX_EFFICIENCY;

			double valuation = 0;

			switch(this.role) {
				case MAX_EFFICIENCY:
					valuation = this.castsWith.critSustain;
					break;
				case BALANCED:
					valuation = (this.castsWith.critSustain + this.castsWith.critBurst) / 2;
					break;
				case MAX_THROUGHPUT:
					valuation = this.castsWith.critBurst;
					break;
			}

			return 1 + valuation * this.castsWith.critMultiplier;
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
										NumberFormat.readableLargeNumber(this.getHealing()) +
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

		HealingSpell healSpell = new HealingSpell("Heal")
				.setHealing(150)
				.setCost(40)
				.setPowerScaling(0.50)
				.setTimeLimitations(6.0, 0.6667, 6.0)
				.setRotationRole(HealingRotationPortion.BALANCED);

		HealingSpell cureCriticalSpell = new HealingSpell("Cure Critical Wounds")
				.setHealingRange(32, 52)
				.setCost(20)
				.setTimeLimitations(6.0, 0.6667, 6.0)
				.setRotationRole(HealingRotationPortion.MAX_THROUGHPUT);
		
		HealingSpell renewal = new HealingSLA("Renewal")
				.setHealingRange(24, 29)
				.setCost(5)
				.setNumberOfHeals(4)
				.setTimeLimitations(4.0, 0.5, 7.0)
				.setRotationRole(HealingRotationPortion.MAX_EFFICIENCY);

		HealingSpell sustainingSong = new HealingSLA("Sustaining Song")
				.setHealingRange(2, 6)
				.setTimeLimitations(2.0, 0, 2.0)
				.setRotationRole(HealingRotationPortion.MAX_EFFICIENCY);

		HealingSpell inspireGreatness = new HealingSLA("Inspire Greatness")
				.setHealing(this.getCha(stats))
				.setTimeLimitations(6.0, 0, 6.0)
				.setNumberOfHeals(2)
				.setRotationRole(HealingRotationPortion.MAX_EFFICIENCY);
		
		HealingSpellbook bardSpells = new HealingSpellbook("Bard")
				.addSpell(healSpell)
				.addSpell(cureCriticalSpell)
				.addSpell(renewal)
				.addSpell(sustainingSong)
				.addSpell(inspireGreatness)
				.setHealingStats(hs);
		
		double maxHPS = bardSpells.getHPS(HealingRotationPortion.MAX_THROUGHPUT);
		double sustain = bardSpells.getContinuousCastingDuration(HealingRotationPortion.MAX_THROUGHPUT);

		double score = Math.sqrt(maxHPS * Math.pow(sustain, 0.85));
		
		if(verbose) {
			bardSpells.printVerboseDebugLog();
			System.out.println("|");
			System.out.println("+- Max HPS:   " + (int) maxHPS);
			System.out.println("+- Sustain:   " + (int) sustain);
		}
		
		return score;
	}

	protected boolean valuesDPS() { return true; }

	@Override
	protected boolean valuesHealing() { return true; }

	@Override
	protected int getBaseSPFromLevel(int level) { return 25 + 25 * level; }
	
	@Override
	protected AbilityScore getCastingAbility() { return AbilityScore.CHARISMA; }
}
