package ddop.optimizer.valuation;

import ddop.builds.adventurerClass.BaseAttackBonusProgression;
import ddop.optimizer.valuation.damage.DamageSource;
import ddop.stat.AbilityScore;
import ddop.stat.StatSource;
import ddop.stat.list.AbstractStatList;
import util.NumberFormat;
import util.Pair;
import util.StatTotals;

import java.util.*;

public abstract class SpecScorer extends StatScorer {
	private static final int EPIC_HIT_DIE = 10;
	private static final double EPIC_BAB = 0.5;
	
	int characterLevel = 30;
	int hitDie         = 6;
	double bab         = 0.75;

	private final BaseAttackBonusProgression BABProgression;
	private final StatSource build;
	private final StatSource reaperBuild;
	
	SpecScorer(int level) {
		this.characterLevel = level;
		this.BABProgression = this.getBABProgression();
		this.build = this.getBuild();
		this.reaperBuild = this.getReaperBuild();
	}

	protected Set<String> getBaseQueriedStatCategories() {
		Set<String> ret = new HashSet<>();

		if(this.valuesDPS()) {
			for(Set<String> queried : this.getDamageSourcesQueriedStatCategories())
				ret.addAll(queried);

			ret.addAll(Arrays.asList(
					"true seeing",
					"ghost touch",
					"tendon slice"
			));
		}

		ret.addAll(Arrays.asList(
				"minor artifact",
				"strength",
				"dexterity",
				"constitution",
				"intelligence",
				"wisdom",
				"charisma",
				"cannith combat infusion",
				"hp",
				"percent hp",
				"unconsciousness range",
				"prr",
				"docent",
				"light armor",
				"medium armor",
				"heavy armor",
				"light armor proficiency",
				"medium armor proficiency",
				"heavy armor proficiency",
				"small shield",
				"large shield",
				"tower shield",
				"mrr",
				"mrr cap",
				"dr",
				"fortification",
				"fire resistance",
				"cold resistance",
				"electric resistance",
				"acid resistance",
				"sonic resistance",
				"negative resistance",
				"force resistance",
				"poison resistance",
				"light resistance",
				"law resistance",
				"chaos resistance",
				"evil resistance",
				"good resistance",
				"fire absorption",
				"cold absorption",
				"electric absorption",
				"acid absorption",
				"sonic absorption",
				"negative absorption",
				"force absorption",
				"poison absorption",
				"light absorption",
				"law absorption",
				"chaos absorption",
				"evil absorption",
				"good absorption",
				"mp reduction",
				"ac",
				"percent ac",
				"maximum dodge",
				"dodge",
				"concealment",
				"incorporeal",
				"healing amplification",
				"feat: wind through the trees",
				"improved quelling strikes",
				"spell saves",
				"fortitude saves",
				"insightful reflexes",
				"force of personality",
				"reflex saves",
				"will saves",
				"epic fortitude",
				"epic reflex",
				"epic will",
				"slippery mind",
				"percent sp",
				"sp",
				"gear sp"
		));

		return ret;
	}

	protected abstract Set<ArmorType> getAllowedArmorTypes();
	abstract BaseAttackBonusProgression getBABProgression();
	abstract StatSource getBuild();
	abstract StatSource getReaperBuild();
	
	private Collection<Set<String>> getDamageSourcesQueriedStatCategories() {
		Collection<Set<String>> ret = new ArrayList<>();

		for(DamageSource ds : this.getDamageSources())
			ret.add(ds.getQueriedStatCategories());

		return ret;
	}

	protected abstract Collection<DamageSource> getDamageSources();

	//region Constants
	protected static final int	SIM_ENEMY_HP			   = 9000,
								SIM_ENEMY_SAVES_FORT_LOW	= 78,
								SIM_ENEMY_SAVES_FORT_MED	= 98,	// Tested Sharn r1, rest guessed/assumed.
								SIM_ENEMY_SAVES_FORT_HIGH	= 118,
								SIM_ENEMY_SAVES_WILL_LOW	= 65,
								SIM_ENEMY_SAVES_WILL_MED	= 90,
								SIM_ENEMY_SAVES_WILL_HIGH	= 115,
								SIM_ENEMY_SAVES_REFL_LOW	= 45,
								SIM_ENEMY_SAVES_REFL_MED	= 70,
								SIM_ENEMY_SAVES_REFL_HIGH	= 90,
								SIM_ENEMY_SR_LOW	        = 45,
								SIM_ENEMY_SR_MED	        = 60,
								SIM_ENEMY_SR_HIGH	        = 70,
								SIM_DAMAGE_SIZE            = 4800;
	protected static final double	SIM_ENEMY_SAVES_LOW_PORTION		= 0.15,
									SIM_ENEMY_SAVES_MED_PORTION		= 0.7,
									SIM_ENEMY_SAVES_HIGH_PORTION	= 0.15,
									SIM_ENEMY_HAS_SR_PORTION        = 0.3,
									SIM_ENEMY_DCS_VERY_LOW_PORTION	= 0.05,
									SIM_ENEMY_DCS_LOW_PORTION		= 0.15,
									SIM_ENEMY_DCS_MEDIUM_PORTION	= 0.6,
									SIM_ENEMY_DCS_HIGH_PORTION		= 0.15,
									SIM_ENEMY_DCS_VERY_HIGH_PORTION	= 0.05,
									SIM_ENEMY_CRIT_MULTIPLIER	= 2,
									SIM_ENEMY_CRIT_RATE			= 0.15,
									SIM_DR_BYPASSED_PORTION		= 0.3,
									SIM_MAGIC_DAMAGE_PORTION	= 0.7,
									SIM_PHYSICAL_DAMAGE_RANGED_PORTION	= 0.4,
									SIM_PHYSICAL_DAMAGE_MELEE_PORTION	= 1 - SIM_PHYSICAL_DAMAGE_RANGED_PORTION,
									SIM_BLURRY_PORTION			= 0.05,
									SIM_GHOSTLY_PORTION			= 0.10,
									SIM_DAMAGE_PHYSICAL_BYPASSES_AC_PORTION				= 0.33,
									SIM_DAMAGE_PHYSICAL_BYPASSES_DODGE_PORTION			= 0.33,
									SIM_DAMAGE_PHYSICAL_BYPASSES_CONCEALMENT_PORTION	= 0.8,
									SIM_DAMAGE_PHYSICAL_BYPASSES_INCORPOREAL_PORTION	= 0.9,
									SIM_DAMAGE_MAGICAL_BYPASSES_AC_PORTION				= 0.4,
									SIM_DAMAGE_MAGICAL_BYPASSES_DODGE_PORTION			= 0.4,
									SIM_DAMAGE_MAGICAL_BYPASSES_CONCEALMENT_PORTION		= 1,
									SIM_DAMAGE_MAGICAL_BYPASSES_INCORPOREAL_PORTION		= 1,
									SIM_DEATHABLE_PORTION		= 0.35,
									SIM_STUNNABLE_PORTION		= 0.3,
									SIM_COMMANDABLE_PORTION		= 0.3,
									SIM_HOLDABLE_PORTION		= 0.3,
									SIM_DANCEABLE_PORTION		= 0.3,
									SIM_SALTABLE_PORTION		= 0.9,
									SIM_KUKANDOABLE_PORTION		= 0.35,
									SIM_JADEABLE_PORTION		= 0.65,
									SIM_CANNITH_COMBAT_INFUSION_UPTIME	= 0.7;
	private static final int	SIM_ENEMY_TO_HIT_R1        = 312, // Sharn r1,          measured with tiny sample size
								SIM_ENEMY_TO_HIT_RAID      = 710, // Sharn normal raid, estimated from anecdote "250 AC doesn't do shit"
								SIM_ENEMY_FORT_BYPASS      = 175,
								SIM_ENEMY_DCS_VERY_LOW     = 55,
								SIM_ENEMY_DCS_LOW          = 70,
								SIM_ENEMY_DCS_MEDIUM       = 90,
								SIM_ENEMY_DCS_HIGH         = 100,  // Sharn normal raid, 119 saves.
								SIM_ENEMY_DCS_VERY_HIGH    = 125;  // Sharn r1 raid, 120 does not save.
	private static final double SIM_SAVES_SPELL_PORTION    = 0.5;
	private static final double SIM_MAGIC_DAMAGE_PORTION_FIRE     = 0.14,
								SIM_MAGIC_DAMAGE_PORTION_COLD     = 0.17,
								SIM_MAGIC_DAMAGE_PORTION_ACID     = 0.11,
								SIM_MAGIC_DAMAGE_PORTION_ELECTRIC = 0.14,
								SIM_MAGIC_DAMAGE_PORTION_SONIC    = 0.04,
								SIM_MAGIC_DAMAGE_PORTION_NEGATIVE = 0.005,
								SIM_MAGIC_DAMAGE_PORTION_FORCE    = 0.14,
								SIM_MAGIC_DAMAGE_PORTION_POISON   = 0.04,
								SIM_MAGIC_DAMAGE_PORTION_LIGHT    = 0.04,
								SIM_MAGIC_DAMAGE_PORTION_LAW      = 0.005,
								SIM_MAGIC_DAMAGE_PORTION_CHAOS    = 0.04,
								SIM_MAGIC_DAMAGE_PORTION_EVIL     = 0.08,
								SIM_MAGIC_DAMAGE_PORTION_GOOD     = 0.01,
								SIM_MAGIC_DAMAGE_PORTION_BANE     = 0.04;
	
	protected static final double VALUATION_UNCONSCIOUSNESS = 0.2,
								VALUATION_HP             = 1,
								VALUATION_FORTIFICATION  = 2.5, // Value crit damage as being more threatening than usual, due to damage spikiness.
								VALUATION_HEAL_AMP       = 0.20, // Multiplier for heal amp worth. Generally below 1, since heals overheal.
								VALUATION_DEATH          = 1,
								VALUATION_STUN           = 0.35,
								VALUATION_JADE           = 0.45,
								VALUATION_TRIP           = 0.2,
								VALUATION_DANCE          = 0.22,
								VALUATION_SALT			 = 0.1,
								VALUATION_DCS            = 3.0, // 1 for the DPS-equivalent, 1 for DPS benefit to party, 1 for defenses benefit.
								VALUATION_WIND_THROUGH_THE_TREES    = 1.015,
								VALUATION_IMPROVED_QUELLING_STRIKES = 1.02, // TODO modify for weapon attack rate
								VALUATION_TENDON_SLICE = 0.05,
								VALUATION_SAVES_IMPORTANCE = 0.6,
								VALUATION_AVOIDANCE      = 0.9; // Multiplier for avoidance worth. Generally below 1, as avoidance cannot be trusted.

	private static final int BASE_UNCONSCIOUSNESS = 10;
	//endregion
	
	@Override
	protected double score(AbstractStatList stats, Double scoreToNormalizeTo) {
		StatTotals totals = stats.add(this.build).add(this.reaperBuild).getStatTotals();
		
		double DPS = 1;
		if(this.valuesDPS()) {
			DPS = this.scoreDPS(totals);
		}

		double healingScore = 1;
		if(this.valuesHealing()) {
			healingScore = this.scoreHealing(totals);
			if(this.verbose) System.out.println("+- Score:     " + NumberFormat.readableLargeNumber(healingScore) + "\n");
		}

		double DCs = 0;
		if(this.hasDCs()) {
			DCs = this.scoreDCs(totals) * Math.pow(1.2, this.skulls);
			if(this.verbose) System.out.println("+- Score:     " + NumberFormat.readableLargeNumber(DCs) + "\n");
		}
		double defenses = this.scoreDefenses(totals);
		double saves    = this.scoreSaves   (totals);
		
		double offensiveScore = DPS;
		if(this.valuesHealing()) {
			offensiveScore = DPS * 0.1 + healingScore * 0.9;
		}
		
		double offenseScoreTotal = (offensiveScore + DCs);
		double defenseScoreTotal = (defenses       + DCs);

		List<Pair<String, Double>> penalties = this.getPenalties(totals);

		double penaltyMultiplier = 1.0;
		for(Pair<String, Double> penalty : penalties) penaltyMultiplier *= penalty.getValue();


		double score = offenseScoreTotal * Math.sqrt(defenseScoreTotal) * saves * penaltyMultiplier;
		if(scoreToNormalizeTo == null) scoreToNormalizeTo = score;
		
		if(this.verbose) {
			String debugLog = "SpecScorer Overall Score Debug Log\n";
			if(this.valuesDPS())     debugLog += "DPS Score: " + NumberFormat.readableLargeNumber(DPS)      + "\n";
			if(this.valuesHealing()) debugLog += "Healing:   " + NumberFormat.readableLargeNumber(healingScore) + "\n";
			if(this.hasDCs()) debugLog += "DCs:       " + NumberFormat.readableLargeNumber(DCs) + "\n";
			debugLog += "Defenses:  " + NumberFormat.readableLargeNumber(defenses) + "\n";
			debugLog += "Saves:     " + NumberFormat.percent(saves)                + "\n";

			if(penalties.size() > 0) {
				debugLog += "Penalties:     " + NumberFormat.percent(penaltyMultiplier - 1) + "\n";
				for(Pair<String, Double> penalty : penalties)
					debugLog += "               " + penalty.getKey() + " (" + NumberFormat.percent(penalty.getValue() - 1) + ")\n";
			}

			debugLog += "Total:     " + NumberFormat.readableLargeNumber(score)    + "(" + NumberFormat.percent(score / scoreToNormalizeTo) + ")\n";
			
			System.out.println(debugLog);
		}
		
		return score;
	}

	protected List<Pair<String, Double>> getPenalties(StatTotals stats) {
		List<Pair<String, Double>> ret = new ArrayList<>();

		int minorArtifactCount = stats.getInt("minor artifact");
		if(minorArtifactCount < 1) ret.add(new Pair<>("Not wearing a minor artifact.", 0.8));
		if(minorArtifactCount > 1) ret.add(new Pair<>("Wearing" + minorArtifactCount + " minor artifacts.", 1 - (0.2 * minorArtifactCount)));

		return ret;
	}

	int getClassLevel() { return Math.min(20, this.characterLevel);      }
	int getEpicLevel()  { return Math.max(0,  this.characterLevel - 20); }

	protected int getAbilityMod(AbilityScore ability, StatTotals stats) {
		int score = this.getAbilityScore(ability, stats);
		return abilityMod(score);
	}
	
	protected int getAbilityScore(AbilityScore ability, StatTotals stats) {
		if(ability == null) return 0;
		
		switch(ability) {
			case STRENGTH:
				return this.getStr(stats);
			case DEXTERITY:
				return this.getDex(stats);
			case CONSTITUTION:
				return this.getCon(stats);
			case INTELLIGENCE:
				return this.getInt(stats);
			case WISDOM:
				return this.getWis(stats);
			case CHARISMA:
				return this.getCha(stats);
			default:
				throw new IllegalStateException("Unexpected value: " + ability);
		}
	}
	
	protected int getStr(StatTotals stats) {
		return stats.getInt("strength") + (int) (stats.get("cannith combat infusion") > 0 ? 4 * SIM_CANNITH_COMBAT_INFUSION_UPTIME : 0);
	}
	
	protected int getDex(StatTotals stats) {
		return stats.getInt("dexterity") + (int) (stats.get("cannith combat infusion") > 0 ? 4 * SIM_CANNITH_COMBAT_INFUSION_UPTIME : 0);
	}
	
	protected int getCon(StatTotals stats) {
		return stats.getInt("constitution") + (int) (stats.get("cannith combat infusion") > 0 ? 4 * SIM_CANNITH_COMBAT_INFUSION_UPTIME : 0);
	}
	
	protected int getInt(StatTotals stats) {
		return stats.getInt("intelligence");
	}
	
	protected int getWis(StatTotals stats) {
		return stats.getInt("wisdom");
	}
	
	protected int getCha(StatTotals stats) {
		return stats.getInt("charisma");
	}

	protected boolean valuesDPS() { return true; }
	
	private double scoreDPS(StatTotals stats) {
		double combinedDPS = this.getDPS(stats);
		
		double trueSeeingMultiplier     = 1 - (stats.get("true seeing")     > 0 ? 0                                 : 0.6 * SIM_BLURRY_PORTION);
		double ghostTouchMultiplier     = 1 - (stats.get("ghost touch")     > 0 ? 0                                 : 0.7 * SIM_GHOSTLY_PORTION);
		double tendonSliceMultiplier    = 1 + (stats.get("tendon slice")    > 0 ? VALUATION_TENDON_SLICE            : 0);
		double miscMultiplier = trueSeeingMultiplier * ghostTouchMultiplier * tendonSliceMultiplier;
		
		double score = combinedDPS * miscMultiplier;
		
		if(this.verbose) {
			System.out.println("+- Score:     " + NumberFormat.readableLargeNumber(score) + "\n");
		}
		
		return score;
	}

	private double getDPS(StatTotals stats) {
		if(this.getDamageSources().size() == 0) return 0;
		
		double totalDPS = 0;
		double remainingActiveTime = 1;
		for(DamageSource ds : this.getDamageSources()) {
			ds.skulls = this.skulls;
			double DpET = ds.getDpET(stats, this.verbose);
			double activeTime = ds.getActiveTime(stats);
			
			double DPS = DpET * activeTime;
			if(activeTime > remainingActiveTime) {
				DPS = DpET * remainingActiveTime;
			}
			
			totalDPS += DPS;
			remainingActiveTime -= activeTime;
			if(activeTime <= 0) break;
		}
		
		return totalDPS;
	}
	
	protected double scoreDefenses(StatTotals stats) {
		double hpScore          = this.scoreHP(stats);
		double reductionScore   = this.scoreDamageReducers(stats);
		double avoidanceScore   = this.scoreAvoidance(stats);
		double healAmpScore     = this.scoreHealAmp(stats);
		double miscEffectsScore = this.scoreMiscEffects(stats);
		
		double score = hpScore * reductionScore * avoidanceScore * healAmpScore * miscEffectsScore;
		
		if(this.verbose) {
			System.out.println("SpecScorer Defenses Debug Log\n"
					+ "+- HP:        " + this.getHP(stats) + " (+" + this.getUnconsciousnessRange(stats) + " unconscious)\n"
					+ "+- PRR:       " + this.getPRR(stats) + "\n"
					+ "+- MRR:       " + this.getMRR(stats) + " (" + (this.getRawMRR(stats) > this.getMRRCap(stats) ? (this.getRawMRR(stats) - this.getMRRCap(stats)) + " over cap" : (this.getMRRCap(stats) > 1000 ? "Uncapped" : "Cap: " + this.getMRRCap(stats))) + ")\n"
					+ "+- Heal Amp.: " + this.getHealAmp(stats) + "\n"
				    + "+- AC:        " + this.getAC(stats) + " (" + NumberFormat.percent(this.getACAvoidance(stats)) + ")\n"
				    + "+- Dodge:     " + this.getDodge(stats) + "%" + (this.getDodge(stats) > this.getMaxDodge(stats) ? " (" + (this.getDodge(stats) - this.getMaxDodge(stats)) + "% over cap)" : "") + "\n"
				    + "+- Blurry:    " + this.getConcealment(stats) + "%\n"
				    + "+- Ghostly:   " + this.getIncorporeal(stats) + "%\n"
				    + "+- Score:     " + NumberFormat.readableLargeNumber(score) + "\n");
		}
		
		return score;
	}
	
	private int getHPFromLevel() {
		return this.hitDie * this.getClassLevel() + SpecScorer.EPIC_HIT_DIE * this.getEpicLevel();
	}

	private int getHPFromCon(StatTotals stats) {
		return this.getAbilityMod(AbilityScore.CONSTITUTION, stats) * this.characterLevel;
	}
	
	private int getHPBeforeMultiplier(StatTotals stats) {
		return this.getHPFromLevel() + this.getHPFromCon(stats) + stats.getInt("hp");
	}
	
	private double getHPMultiplier(StatTotals stats) {
		return 1 + stats.get("percent hp") / 100;
	}
	
	protected int getHP(StatTotals stats) {
		return (int) (this.getHPMultiplier(stats) * this.getHPBeforeMultiplier(stats));
	}
	
	protected int getUnconsciousnessRange(StatTotals stats) {
		return stats.getInt("unconsciousness range") + BASE_UNCONSCIOUSNESS;
	}
	
	protected double scoreHP(StatTotals stats) {
		return this.getHP(stats) * VALUATION_HP + this.getUnconsciousnessRange(stats) * VALUATION_UNCONSCIOUSNESS;
	}
	
	private int getPRR(StatTotals stats) {
		int armorPrr = this.getArmorPrr(stats);
		int shieldPrr = this.getShieldPrrMrr(stats);
		return stats.getInt("prr") + armorPrr + shieldPrr;
	}

	private int getArmorPrr(StatTotals stats) {
		ArmorType worn = ArmorType.get(stats);
		String requiredProficiency = worn.getRequiredProficiency();

		if(requiredProficiency != null)
			if(stats.getBoolean(requiredProficiency))
				return worn.getPRRAtBAB(this.getBAB());
		return 0;
	}

	private int getShieldPrrMrr(StatTotals stats) {
		ShieldType worn = ShieldType.get(stats);
		return worn.prrMrr;
	}
	
	private int getBAB() {
		return this.BABProgression.getBABAtLevel(this.getClassLevel()) +
				BaseAttackBonusProgression.LOW.getBABAtLevel(this.getEpicLevel());
	}
	
	private int getMRR(StatTotals stats) {
		int rawMRR = this.getRawMRR(stats);
		int mrrCap = this.getMRRCap(stats);
		return Math.min(rawMRR, mrrCap);
	}
	
	private int getRawMRR(StatTotals stats) {
		int shieldMrr = this.getShieldPrrMrr(stats);
		return stats.getInt("mrr") + shieldMrr;
	}
	
	private int getMRRCap(StatTotals stats) {
		ArmorType worn = ArmorType.get(stats);

		if(worn.hasMRRCap()) return stats.getInt("mrr cap") + worn.MRRCap;
		return Integer.MAX_VALUE;
	}
	
	private double getDR(StatTotals stats) {
		return stats.getDouble("dr") +
				stats.getDouble("self healing when hit") * (1 + this.getHealAmpPercent(stats)) * this.getReaperSelfHealingReduction();
	}

	private double getReaperSelfHealingReduction() {
		return 0.44 - 0.04 * this.skulls;
	}

	private int getFortification(StatTotals stats) {
		int adjustedFortification = stats.getInt("fortification") - SIM_ENEMY_FORT_BYPASS;
		if(adjustedFortification < 0)   return 0;
		if(adjustedFortification > 100) return 100;
		return adjustedFortification;
	}

	private double getElementalMultiplier(String type, StatTotals stats) {
		type = type.trim().toLowerCase();

		double resistanceMultiplier = 1.0 - stats.getDouble(type + " resistance") / SIM_DAMAGE_SIZE;
		double absorptionMultiplier = 1.0 - stats.getInt(type + " absorption") / 100.0;

		if(resistanceMultiplier < 0.0) resistanceMultiplier = 0.0;

		return resistanceMultiplier * absorptionMultiplier;
	}

	private double getAllElementalMultiplier(StatTotals stats) {
		double fireMultiplier     = getElementalMultiplier("fire",     stats);
		double coldMultiplier     = getElementalMultiplier("cold",     stats);
		double elecMultiplier     = getElementalMultiplier("electric", stats);
		double acidMultiplier     = getElementalMultiplier("acid",     stats);
		double sonicMultiplier    = getElementalMultiplier("sonic",    stats);
		double negativeMultiplier = getElementalMultiplier("negative", stats);
		double forceMultiplier    = getElementalMultiplier("force",    stats);
		double poisonMultiplier   = getElementalMultiplier("poison",   stats);
		double lightMultiplier    = getElementalMultiplier("light",    stats);
		double lawMultiplier      = getElementalMultiplier("law",      stats);
		double chaosMultiplier    = getElementalMultiplier("chaos",    stats);
		double evilMultiplier     = getElementalMultiplier("evil",     stats);
		double goodMultiplier     = getElementalMultiplier("good",     stats);
		// Bane, but nothing reduces it.

		double elementalMultiplier = fireMultiplier * SIM_MAGIC_DAMAGE_PORTION_FIRE +
									 coldMultiplier * SIM_MAGIC_DAMAGE_PORTION_COLD +
									 elecMultiplier * SIM_MAGIC_DAMAGE_PORTION_ELECTRIC +
									 acidMultiplier * SIM_MAGIC_DAMAGE_PORTION_ACID +
									 sonicMultiplier * SIM_MAGIC_DAMAGE_PORTION_SONIC +
									 negativeMultiplier * SIM_MAGIC_DAMAGE_PORTION_NEGATIVE +
									 forceMultiplier * SIM_MAGIC_DAMAGE_PORTION_FORCE +
									 poisonMultiplier * SIM_MAGIC_DAMAGE_PORTION_POISON +
									 lightMultiplier * SIM_MAGIC_DAMAGE_PORTION_LIGHT +
									 lawMultiplier * SIM_MAGIC_DAMAGE_PORTION_LAW +
									 chaosMultiplier * SIM_MAGIC_DAMAGE_PORTION_CHAOS +
									 evilMultiplier * SIM_MAGIC_DAMAGE_PORTION_EVIL +
									 goodMultiplier * SIM_MAGIC_DAMAGE_PORTION_GOOD;

		return elementalMultiplier;
	}

	protected double scoreDamageReducers(StatTotals stats) {
		double physicalPortion = (1 - SIM_MAGIC_DAMAGE_PORTION);

		double prrReduction       = 1 / (1 + this.getPRR(stats) / 100.0);												// Starts at 1, ~0.4   in Sharn gear
		double drReduction        = (1 - (double) this.getDR(stats) / SIM_DAMAGE_SIZE * (1 - SIM_DR_BYPASSED_PORTION)); // Starts at 1, ~0.993 in Sharn gear
		double enemyCrit          = (SIM_ENEMY_CRIT_RATE * (SIM_ENEMY_CRIT_MULTIPLIER - 1) * VALUATION_FORTIFICATION);
		double enemyCritAfterFort = enemyCrit * (1 - this.getFortification(stats) / 100.0);
		double fortReduction      = (1 + enemyCritAfterFort) / (1 + enemyCrit);                                         // Starts at 1, ~0.73 at uncrittable

		int enemyMpReduction = stats.getInt("mp reduction");
		int enemyRpReduction = stats.getInt("mp reduction");
		double enemyMeleeReductionReduction		= 1 / (1 + enemyMpReduction / 100.0);
		double enemyRangedReductionReduction	= 1 / (1 + enemyRpReduction / 100.0);
		double enemyWeaponPowerReductionReduction	=  (SIM_PHYSICAL_DAMAGE_MELEE_PORTION	* enemyMeleeReductionReduction +
														SIM_PHYSICAL_DAMAGE_RANGED_PORTION	* enemyRangedReductionReduction) / 2;
		
		double physicalReduction = prrReduction * drReduction * fortReduction * enemyWeaponPowerReductionReduction;
		
		double mrrReduction = 1 / (1 + this.getMRR(stats) / 100.0);
		double elementalMultiplier = getAllElementalMultiplier(stats);

		double magicalReduction = mrrReduction * elementalMultiplier;

		double resultingDamage = physicalPortion * physicalReduction + SIM_MAGIC_DAMAGE_PORTION * magicalReduction;
		
		return 1 / resultingDamage;
	}

	private int getAC(StatTotals stats) {
		int baseAc = this.getAbilityMod(AbilityScore.DEXTERITY, stats) +
					   this.getAbilityMod(AbilityScore.WISDOM, stats) + // TODO move wisdom to monk
					   stats.getInt("ac");
		int percentAc = stats.getInt("percent ac");
		double acMultiplier = 1 + (percentAc / 100.0);

		return (int) (baseAc * acMultiplier);
	}
	
	private double getACAvoidance(StatTotals stats) {
		int ac = this.getAC(stats);
		double enemyHitACr1   = cap(0.05, (SIM_ENEMY_TO_HIT_R1   + 10.5) / (2 * ac), 0.95);
		double enemyHitACRaid = cap(0.05, (SIM_ENEMY_TO_HIT_RAID + 10.5) / (2 * ac), 0.95);
		double enemyHitAC     = 0.5 * (enemyHitACr1 + enemyHitACRaid);
		
		return 1 - enemyHitAC;
	}
	
	private int getMaxDodge(StatTotals stats) {
		return stats.getInt("maximum dodge");
	}
	
	private int getDodge(StatTotals stats) {
		int dodge = this.getRawDodge(stats);
		int dodgeCap = this.getMaxDodge(stats);
		return Math.min(dodge, dodgeCap);
	}
	
	private int getRawDodge(StatTotals stats) {
		return stats.getInt("dodge");
	}
	
	private double getDodgeAvoidance(StatTotals stats) {
		return this.getDodge(stats) / 100.0;
	}
	
	private int getConcealment(StatTotals stats) {
		double concealmentFactor = 1;
		if(this.skulls > 0) concealmentFactor = 0.75;
		return (int) (stats.getInt("concealment") * concealmentFactor);
	}
	
	private double getConcealmentAvoidance(StatTotals stats) {
		return this.getConcealment(stats) / 100.0;
	}
	
	private int getIncorporeal(StatTotals stats) {
		double incorpFactor = 1;
		if(this.skulls > 0) incorpFactor = 0.75;
		return (int) (stats.getInt("incorporeal") * incorpFactor);
	}
	
	private double getIncorporealAvoidance(StatTotals stats) {
		return this.getIncorporeal(stats) / 100.0;
	}
	
	private double scoreAvoidance(StatTotals stats) {
		double physicalPortion = (1 - SIM_MAGIC_DAMAGE_PORTION);

		double acAvoidance    = this.getACAvoidance(stats);
		double dodgeAvoidance = this.getDodgeAvoidance(stats);
		double concealmentAvoidance = this.getConcealmentAvoidance(stats);
		double incorporealAvoidance = this.getIncorporealAvoidance(stats);
		
		
		double physicalReduction =	(1 - (1 - SIM_DAMAGE_PHYSICAL_BYPASSES_AC_PORTION)          * acAvoidance) *
									(1 - (1 - SIM_DAMAGE_PHYSICAL_BYPASSES_DODGE_PORTION)       * dodgeAvoidance) *
									(1 - (1 - SIM_DAMAGE_PHYSICAL_BYPASSES_CONCEALMENT_PORTION) * concealmentAvoidance) *
									(1 - (1 - SIM_DAMAGE_PHYSICAL_BYPASSES_INCORPOREAL_PORTION) * incorporealAvoidance);
		double magicalReduction =	(1 - (1 - SIM_DAMAGE_MAGICAL_BYPASSES_AC_PORTION)           * acAvoidance) *
									(1 - (1 - SIM_DAMAGE_MAGICAL_BYPASSES_DODGE_PORTION)        * dodgeAvoidance) *
									(1 - (1 - SIM_DAMAGE_MAGICAL_BYPASSES_CONCEALMENT_PORTION)  * concealmentAvoidance) *
									(1 - (1 - SIM_DAMAGE_MAGICAL_BYPASSES_INCORPOREAL_PORTION)  * incorporealAvoidance);
		
		double resultingDamage = physicalPortion * physicalReduction + SIM_MAGIC_DAMAGE_PORTION * magicalReduction;
		
		return 1 / resultingDamage * VALUATION_AVOIDANCE;
	}
	
	private int getHealAmp(StatTotals stats) {
		return stats.getInt("healing amplification");
	}
	
	private double getHealAmpPercent(StatTotals stats) {
		return this.getHealAmp(stats) / 100.0;
	}
	
	private double scoreHealAmp(StatTotals stats) {
		return 1 + this.getHealAmpPercent(stats) * VALUATION_HEAL_AMP;
	}
	
	private double scoreWindThroughTheTrees(StatTotals stats) {
		if(stats.get("feat: wind through the trees") > 0) return VALUATION_WIND_THROUGH_THE_TREES;
		return 1;
	}
	
	private double scoreImprovedQuellingStrikes(StatTotals stats) {
		if(stats.get("improved quelling strikes") > 0) return VALUATION_IMPROVED_QUELLING_STRIKES;
		return 1;
	}
	
	private double scoreMiscEffects(StatTotals stats) {
		double windThroughTheTreesScore     = this.scoreWindThroughTheTrees(stats);
		double improvedQuellingStrikesScore = this.scoreImprovedQuellingStrikes(stats);
		return windThroughTheTreesScore * improvedQuellingStrikesScore;
	}
	
	private double scoreSaves(StatTotals stats) {
		int dex = this.getDex(stats);
		int con = this.getCon(stats);
		int inte = this.getInt(stats);
		int wis = this.getWis(stats);
		int cha = this.getCha(stats);
		int dexMod = this.getAbilityMod(AbilityScore.DEXTERITY,    stats);
		int intMod = this.getAbilityMod(AbilityScore.INTELLIGENCE, stats);
		int conMod = this.getAbilityMod(AbilityScore.CONSTITUTION, stats);
		int wisMod = this.getAbilityMod(AbilityScore.WISDOM,       stats);
		int chaMod = this.getAbilityMod(AbilityScore.CHARISMA,     stats);
		
		int spellSaves = stats.getInt("spell saves");
		int fortSaves  = conMod + stats.getInt("fortitude saves");

		int reflMod = (stats.getBoolean("insightful reflexes") ? Math.max(dexMod, intMod) : dexMod);
		int reflSaves  = reflMod + stats.getInt("reflex saves");

		int willMod = (stats.getBoolean("force of personality") ? Math.max(wisMod, chaMod) : wisMod);
		int willSaves  = willMod + stats.getInt("will saves");

		double fortRate = getSaveSpread(fortSaves, stats.getBoolean("epic fortitude"),	1);
		double reflRate = getSaveSpread(reflSaves, stats.getBoolean("epic reflex"),     1);
		double willRate = getSaveSpread(willSaves, stats.getBoolean("epic will"),		1 + (stats.getBoolean("slippery mind") ? 1 : 0));
		double fortSpellRate = getSaveSpread(fortSaves + spellSaves, stats.getBoolean("epic fortitude"),	1);
		double reflSpellRate = getSaveSpread(reflSaves + spellSaves, stats.getBoolean("epic reflex"),		1);
		double willSpellRate = getSaveSpread(willSaves + spellSaves, stats.getBoolean("epic will"),		1 + (stats.getBoolean("slippery mind") ? 1 : 0));
		double fortCombinedRate = (1 - SIM_SAVES_SPELL_PORTION) * fortRate + SIM_SAVES_SPELL_PORTION * fortSpellRate;
		double reflCombinedRate = (1 - SIM_SAVES_SPELL_PORTION) * reflRate + SIM_SAVES_SPELL_PORTION * reflSpellRate;
		double willCombinedRate = (1 - SIM_SAVES_SPELL_PORTION) * willRate + SIM_SAVES_SPELL_PORTION * willSpellRate;
		double allSavesRate = (fortCombinedRate + reflCombinedRate + willCombinedRate) / 3.0;
		
		double score = VALUATION_SAVES_IMPORTANCE * allSavesRate + (1 - VALUATION_SAVES_IMPORTANCE);
		
		if(this.verbose) {
			System.out.println("SpecScorer Saves Debug Log\n"
					+ (stats.getBoolean("insightful reflexes") && intMod > dexMod ?
						"+- INT:       " + inte + " (+" + intMod + ")\n" :
						"+- DEX:       " + dex  + " (+" + dexMod + ")\n")
					+ "+- CON:       " + con + " (+" + conMod + ")\n"
					+ (stats.getBoolean("force of personality") && chaMod > wisMod ?
						"+- CHA:       " + cha + " (+" + chaMod + ")\n" :
						"+- WIS:       " + wis + " (+" + wisMod + ")\n")
					+ "+- Saves:     " + fortSaves + "/" + reflSaves + "/" + willSaves + " (" + NumberFormat.percent(allSavesRate) + ")\n"
				    + "+- Score:     " + NumberFormat.percent(score) + "\n");
		}
		
		return score;
	}
	
	// Static helper methods below
	
	private static int abilityMod(int abilityScore) {
		return (abilityScore - 10) / 2;
	}
	
	protected static double cap(double minimum, double value, double maximum) {
		return Math.min(Math.max(value, minimum), maximum);
	}

	private static double getSaveSpread(int save, boolean noFailOn1, int bestRollOfN) {
		double rate = getSaveRate(save, SIM_ENEMY_DCS_VERY_LOW,		noFailOn1, bestRollOfN) * SIM_ENEMY_DCS_VERY_LOW_PORTION
					+ getSaveRate(save, SIM_ENEMY_DCS_LOW,			noFailOn1, bestRollOfN) * SIM_ENEMY_DCS_LOW_PORTION
					+ getSaveRate(save, SIM_ENEMY_DCS_MEDIUM,		noFailOn1, bestRollOfN) * SIM_ENEMY_DCS_MEDIUM_PORTION
					+ getSaveRate(save, SIM_ENEMY_DCS_HIGH,			noFailOn1, bestRollOfN) * SIM_ENEMY_DCS_HIGH_PORTION
					+ getSaveRate(save, SIM_ENEMY_DCS_VERY_HIGH,	noFailOn1, bestRollOfN) * SIM_ENEMY_DCS_VERY_HIGH_PORTION;
		return rate;
	}
	
	private static double getSaveRate(int save, int dc, boolean noFailOn1, int bestRollOfN) {
		return SpecScorer.getSaveRate(dc - save, noFailOn1, bestRollOfN);
	}
	
	private static double getSaveRate(int saveDeficit, boolean noFailOn1, int bestRollOfN) {
		double minChance             = (noFailOn1 ? 0 : 0.05);
		double failChance            = cap(minChance, (saveDeficit -1) * 0.05, 0.95);
		double failWithMultipleRolls = Math.pow(failChance, bestRollOfN);
		
		return 1 - failWithMultipleRolls;
	}

	/** Returns a value for this build's DC-based abilities. Intended to be overridden in a child class. */
	protected double scoreDCs(StatTotals stats) { return 0; }
	/** Must return <code>true</code> if scoreDCs is implemented. */
	protected abstract boolean hasDCs();
	
	protected int getSP(StatTotals stats) {
		return (int) (this.getSPMultiplier(stats) *
							   (this.getBaseSPFromLevel(this.getClassLevel()) +
							   this.getBonusSPFromLevelAndCastingAbility(this.getClassLevel(), stats) +
							   this.getBaseSP(stats) +
							   (int) (this.getGearSP(stats) * this.getGearSPMultiplier())));
	}
	
	private double getSPMultiplier(StatTotals stats) {
		return 1 + stats.get("percent sp") / 100;
	}
	
	/** Intended to be overridden in all caster subclasses. */
	protected int getBaseSPFromLevel(int level) { return 0; }
	
	/** Intended to be overridden in all caster subclasses. */
	protected AbilityScore getCastingAbility() { return null; }
	
	protected int getBonusSPFromLevelAndCastingAbility(int level, StatTotals stats) {
		return (9 + level) * this.getAbilityMod(this.getCastingAbility(), stats);
	}
	
	protected int getBaseSP(StatTotals stats) {
		return stats.getInt("sp");
	}
	
	protected int getGearSP(StatTotals stats) {
		return stats.getInt("gear sp");
	}
	
	/** Intended to be overridden in Sorcerer/FvS subclasses. */
	protected double getGearSPMultiplier() { return 1; }
	
	/** Returns a value for this build's DC-based abilities. Intended to be overridden in a child class. */
	protected double scoreHealing(StatTotals stats) { return 0; }
	/** Must return <code>true</code> if scoreHealing is implemented. */
	protected abstract boolean valuesHealing();
}
