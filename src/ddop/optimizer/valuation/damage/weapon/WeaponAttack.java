package ddop.optimizer.valuation.damage.weapon;

import ddop.optimizer.valuation.damage.DamageSource;
import util.StatTotals;

import java.util.*;

public abstract class WeaponAttack extends DamageSource {
	protected static final int		BASE_CRITICAL_THREAT_RANGE = 1; // TODO weapon stats
	
	protected static final int		REAPER_SKULLS_ENEMY_AC				= 3;
	
	protected static final int		SIM_ENEMY_AC						= 150;
	protected static final double	SIM_ENEMY_FORT             			= 1.0,
									SIM_CANNITH_COMBAT_INFUSION_UPTIME	= 0.7,
									SIM_HELPLESS_UPTIME					= 0.15,
									SIM_RELENTLESS_FURY_UPTIME			= 0.5;


	protected Set<String> getBaseQueriedStatCategories() {
		Set<String> ret = new HashSet<>(Arrays.asList(
				"deadly",
				"deception",
				"sneak attack dice",
				"sneak attack damage",
				"critical damage",
				"prr reduction",
				"mrr reduction",
				"vulnerability",
				"percent to-hit",
				"critical threat range",
				"critical multiplier",
				"overwhelming critical",
				"relentless fury",
				"damage vs helpless",
				"offhand doublestrike",
				"offhand attack chance",
				"critical confirmation",
				"improved deception",
				"armor-piercing",
				"sneak attack accuracy",
				"accuracy",
				"cannith combat infusion",
				this.getWeaponAttackAbility(),
				this.getWeaponDamageAbility(),
				"weapon enhancement bonus",
				"[w]",
				"bonus w",
				"melee alacrity",
				"empty red augment slot",
				"empty orange augment slot",
				"empty purple augment slot",

				// On Hit
				"eternal holy burst",
				"soul of the elements",
				"noxious venom spike",
				"epic noxious venom spike",
				"alchemical water attunement",
				"alchemical earth attunement",
				"alchemical fire attunement",
				"alchemical air attunement",

				// On Vorpal
				"stormreaver's thunderclap"
		));

		ret.addAll(WeaponAttack.DAMAGE_MODS.keySet());

		return ret;
	}

	@Override
	protected double getDamage(StatTotals stats, boolean verbose) {
		double weaponW = this.getWeaponW(stats);
		double weaponBonusWs = this.getBonusWs(stats);
		double weaponDamage = weaponW * weaponBonusWs;

		int weaponEnhancementBonus = this.getWeaponEnhancementBonus(stats);
		int weaponDamageStatMod = this.getWeaponDamageStatMod(stats);
		
		int		deadly = stats.getInt("deadly") + weaponEnhancementBonus + (int) (weaponDamageStatMod * this.getWeaponDamageStatScaling());
		double	sneakDamage = (int) (1.5 * stats.getInt("deception")) + 3.5 * stats.getInt("sneak attack dice") + stats.getInt("sneak attack damage");
		int		damageOnCrit = stats.getInt("critical damage");
		
		int weaponPower = this.getWeaponPower(stats);

		int prrReduction = stats.getInt("prr reduction");
		int mrrReduction = stats.getInt("mrr reduction");
		boolean vulnerability = stats.getBoolean("vulnerability");

		double physicalDamageMultiplier	= 1 + 0.01 * prrReduction * (vulnerability ? 1.2 : 1);
		double magicalDamageMultiplier	= 1 + 0.01 * mrrReduction * (vulnerability ? 1.2 : 1);
		
		double grazingHitDamage  = weaponDamage            * (1 + weaponPower / 100.0)	* physicalDamageMultiplier;
		double physicalDamage    = (weaponDamage + deadly) * (1 + weaponPower / 100.0)	* physicalDamageMultiplier;
		double bonusCritDamage   = damageOnCrit            * (1 + weaponPower / 100.0)	* physicalDamageMultiplier;
		double sneakAttackDamage = sneakDamage             * (1 + weaponPower /  66.67)	* physicalDamageMultiplier;
		double redAugments = stats.getInt("empty red augment slot") + stats.getInt("empty orange augment slot") + stats.getInt("empty purple augment slot");

		double nonScalingBonusDamage = this.getWeaponBonusDamage(stats)	// TODO weapon stats
				+ WeaponAttack.getNonScalingBonusDice(stats)
				+ redAugments * 8 * 3.5; // 8d6 ML28 augments
		double scalingBonusDamage = this.getScalingBonusDamage(stats);
		double onHitBonusDamage = (nonScalingBonusDamage + scalingBonusDamage) * magicalDamageMultiplier;

		double onVorpalBonusDamage = this.getVorpalBonusDamage(stats) * magicalDamageMultiplier;
		
		double fortification	= this.getModifiedEnemyFortification(stats);
		double sneakRate		= this.getSneakAttackOcurrenceRate(stats);
		
		double totalToHit					= this.getTotalToHit(stats);
		double totalCriticalConfirmation	= this.getTotalCriticalConfirmation(stats);
		
		int enemyAC = this.getEnemyAC(this.skulls);
		
		double hitChance     = this.getHitChance(stats);
		double confirmChance = cap(0.05, stats.get("percent to-hit") / 100.0 + (totalCriticalConfirmation + 10.5) / (2 * enemyAC), 0.95);
		
		int		threatRange		= BASE_CRITICAL_THREAT_RANGE + stats.getInt("critical threat range");
		double	threatChance	= 0.05 * threatRange;

		double  vorpalRate      =         0.05 * confirmChance * (1 - fortification);
		double	critRate		= threatChance * confirmChance * (1 - fortification);
		double	hitRate			= hitChance - critRate;
		double	grazingRate		= 0.95 - hitChance;
		
		double averageSneakDamage	= sneakAttackDamage * sneakRate;
		int critMult				= 2 + stats.getInt("critical multiplier");
		int overwhelmingCrit		= stats.getInt("overwhelming critical");
		double averageCritMult		= critMult + overwhelmingCrit * (0.1 / threatChance);
		double critPhysical			= (physicalDamage + bonusCritDamage) * averageCritMult;
		double critDamage			= critPhysical     + averageSneakDamage + onHitBonusDamage;
		double hitDamage			= physicalDamage   + averageSneakDamage + onHitBonusDamage;
		double grazingDamage		= grazingHitDamage + averageSneakDamage;

		double relentlessFuryMultiplier = 1 + (stats.getBoolean("relentless fury") ? 0.05 * SIM_RELENTLESS_FURY_UPTIME : 0);
		double damagePerAttack = (onVorpalBonusDamage * vorpalRate +
								           critDamage * critRate +
										    hitDamage * hitRate +
				                        grazingDamage * grazingRate) * relentlessFuryMultiplier;

		double attacksPerSwing          = this.getWeaponAttacksPerSwing(stats);
		double damagePerSwing	        = damagePerAttack * attacksPerSwing;
		double damagePerSwingHelpless	= damagePerSwing * (1.2 + stats.get("damage vs helpless") / 100);
		double damagePerSwingCombined   = (damagePerSwing * (1 - SIM_HELPLESS_UPTIME) + damagePerSwingHelpless * SIM_HELPLESS_UPTIME);

		double swingsPerSecond	= this.getActionsPerSecond(stats);
		double dps              = swingsPerSecond * damagePerSwing;
		double dpsVsHelpless	= swingsPerSecond * damagePerSwingHelpless;
		double dpsCombined      = swingsPerSecond * damagePerSwingCombined;

		if(verbose) {
			System.out.println("WeaponAttack DPS Debug Log\n"
					+ "+- Weapon:    " + shortFloatText(weaponBonusWs, 5) + "[" + shortFloatText(weaponW, 4) + "] + " + deadly + " = " + (weaponBonusWs * weaponW + deadly) + "\n"
					+ "+- Critical:  " + getCritProfileText(threatRange, critMult, overwhelmingCrit) + "\n"
					+ "+- WeaponPwr: " + weaponPower + " (" + toPercentText(1 + weaponPower / 100.0) + ")\n"
					+ "+- Physical:  " + (int) physicalDamage + " (" + (int) critPhysical + " crit)\n"
					+ "+- Sneak:     " + (int) sneakAttackDamage + "\n"
					+ "+- Bonus:     " + (int) onHitBonusDamage	+ (onVorpalBonusDamage != 0 ? " (+" + (int) onVorpalBonusDamage + " on vorpal)" : "") + "\n"
					+ "+- To-Hit:    " + (int) totalToHit + " (" + toPercentText(0.05)	+ " miss, " + toPercentText(grazingRate) + " grazing, " + toPercentText(hitRate) + " hit, " + toPercentText(critRate) + " crit (includes " + toPercentText(vorpalRate) + " vorpal))\n"
				    + "+- Ave. Dmg:  " + (int) damagePerAttack + "\n"
					+ "+- Hits/Sec:  " + shortFloatText(hitChance * swingsPerSecond * attacksPerSwing, 4) + " (" + toPercentText(hitChance) + " hit * " + shortFloatText(swingsPerSecond, 4) + " SpS * " + shortFloatText(attacksPerSwing, 4) + " DS + OH)\n"
				    + "+- DPS:       " + (int) dps + " (" + (int) dpsVsHelpless + " helpless)\n"
				    + "+- DpAtk:     " + (int) damagePerSwing);
		}
		
		return dpsCombined;
	}

	public double getHitsPerSecond(StatTotals stats) {
		return this.getHitChance(stats) * this.getActionsPerSecond(stats) * this.getWeaponAttacksPerSwing(stats);
	}

	private static final double d6 = 3.5;
	private static final double perCent  = 1.0 / 100.0;

	private static final Map<String, Double> DAMAGE_MODS = WeaponAttack.generateDamageMods();
	private static Map<String, Double> generateDamageMods() {
		Map<String, Double> ret = new HashMap<>();

		ret.put("eternal holy burst", 4.16);
		ret.put("soul of the elements", 15.0);
		ret.put("noxious venom spike", 1 * d6);
		ret.put("epic noxious venom spike", 2 * d6);

		// X% on hit/spell to deal stacking cold DoT.
		// DPS value assume to be the same as fire/air until more data becomes available.
		ret.put("alchemical water attunement", 24.2);
		ret.put("alchemical earth attunement", 24.2);

		// Experimental:
		// 1000 crossbow bolts (assumed 950 hits): 10 fire procs, 13 lightning procs,
		// ~1300-2700 damage each. No apparent damage difference between elements.
		// No spellpower scaling observed (very different spellpower across the two elements, same apparent damage).
		// No cooldown observed (under 2 seconds).
		// Summary:
		// 1.21% on hit, 2000 [type] damage.
		ret.put("alchemical fire attunement", 2000 * 1.21 * perCent);
		ret.put("alchemical air attunement",  2000 * 1.21 * perCent);

		return ret;
	}

	private static double getNonScalingBonusDice(StatTotals stats) {
		double total = 0;

		for(Map.Entry<String, Double> mod : DAMAGE_MODS.entrySet())
			if(stats.getBoolean(mod.getKey()))
				total += mod.getValue();

		return total;
	}

	private static final Map<String, Double> VORPAL_DAMAGE_MODS = WeaponAttack.generateVorpalDamageMods();
	private static Map<String, Double> generateVorpalDamageMods() {
		Map<String, Double> ret = new HashMap<>();

		ret.put("stormreaver's thunderclap", 2657.75 * 3/4); // 1325 lightning + 1325 sonic (DC 100 reflex save halves sonic -- assuming all mobs save and none have evasion)

		return ret;
	}

	private double getVorpalBonusDamage(StatTotals stats) {
		double total = 0;

		for(Map.Entry<String, Double> mod : VORPAL_DAMAGE_MODS.entrySet())
			if(stats.getBoolean(mod.getKey()))
				total += mod.getValue();

		return total;
	}

	/** Returns the odds that at least one hit will occur per swing in the range [0 .. 1]. */
	public double getHitAnyRate(StatTotals stats) {
		double attacksPerSwing			= this.getWeaponAttacksPerSwing(stats);
		double hitChance				= this.getHitChance(stats);
		double missChance				= 1 - hitChance;
		double missEntireSwingChance	= Math.pow(missChance, attacksPerSwing);
		double hitAnyChance				= 1 - missEntireSwingChance;
		
		return hitAnyChance;
	}
	
	public double getWeaponAttacksPerSwing(StatTotals stats) {
		double doubles				= this.getWeaponDoubles(stats);
		double offhandChance		= this.getWeaponOffhandChance(stats);
		double offhandDoublestrike	= this.getWeaponOffhandDoublestrikeChance(stats);
		return 1 + offhandChance + doubles + offhandDoublestrike;
	}

	private double getWeaponOffhandDoublestrikeChance(StatTotals stats) {
		double offhandChance = this.getWeaponOffhandChance(stats);
		return stats.getInt("offhand doublestrike") / 100.0 * offhandChance;
	}

	private double getWeaponOffhandChance(StatTotals stats) {
		return stats.getInt("offhand attack chance") / 100.0;
	}

	private double getTotalCriticalConfirmation(StatTotals stats) {
		double totalToHit = this.getTotalToHit(stats);
		return stats.getInt("critical confirmation") + totalToHit;
	}

	private double getSneakAttackOcurrenceRate(StatTotals stats) {
		double fortification = this.getModifiedEnemyFortification(stats);
		return (1 - fortification) * (stats.getBoolean("improved deception") ? 1 : 0.5);
	}

	private double getModifiedEnemyFortification(StatTotals stats) {
		return cap(0, SIM_ENEMY_FORT - stats.getInt("armor-piercing") / 100.0, 1);
	}

	private int getSneakAttackAccuracy(StatTotals stats) {
		return stats.getInt("sneak attack accuracy") + stats.getInt("deception");
	}

	private double getTotalToHit(StatTotals stats) {
		int weaponEnhancementBonus	= this.getWeaponEnhancementBonus(stats);
		int weaponAttackStatMod 	= this.getWeaponAttackStatMod(stats);
		int	sneakAttackAccuracy		= this.getSneakAttackAccuracy(stats);
		double sneakRate			= this.getSneakAttackOcurrenceRate(stats);
		return stats.getInt("accuracy") + weaponEnhancementBonus + weaponAttackStatMod + sneakAttackAccuracy * sneakRate;
	}
	
	public double getHitChance(StatTotals stats) {
		int		enemyAC		= this.getEnemyAC(this.skulls);
		double	totalToHit	= this.getTotalToHit(stats);
		return cap(0.05, stats.get("percent to-hit") / 100.0 + (totalToHit + 10.5) / (2 * enemyAC), 0.95);
	}
	
	public int getEnemyAC(int skulls) { return SIM_ENEMY_AC + REAPER_SKULLS_ENEMY_AC * skulls; }
	
	protected abstract double getWeaponDoubles(StatTotals stats);
	protected abstract double getWeaponBonusDamage(StatTotals stats);
	protected abstract double getScalingBonusDamage(StatTotals stats);
	protected abstract int getWeaponPower(StatTotals stats);

	private String getCritProfileText(int threatRange, int critMult, int overwhelmingCrit) {
		int minCrit = 21 - threatRange;
		if(minCrit > 20) return "-";
		
		if(overwhelmingCrit != 0) {
			if(minCrit >= 19) {
				return (minCrit == 19 ? "19-"         : "") + "20/x" + (critMult + overwhelmingCrit);
			} else {
				return (minCrit <  18 ? minCrit + "-" : "") + "18/x" + critMult + ", 19-20/x" + (critMult + overwhelmingCrit);
			}
		} else {
			return (minCrit < 20 ? minCrit + "-" : "") + "20/x" + critMult;
		}
	}

	protected double getDoublestrike(StatTotals stats) {
		double strike = stats.getInt("doublestrike") / 100.0 + (stats.getBoolean("cannith combat infusion") ? 0.05 * SIM_CANNITH_COMBAT_INFUSION_UPTIME : 0);
		return cap(0, strike, 1);
	}
	
	protected double getDoubleshot(StatTotals stats) {
		double shot = stats.getInt("doubleshot") / 100.0;
		return cap(0, shot, 1);
	}

	private int getWeaponAttackStatMod(StatTotals stats) {
		String weaponMod = this.getWeaponAttackAbility();
		return getMod(stats.getInt(weaponMod));
	}

	private int getWeaponDamageStatMod(StatTotals stats) {
		String weaponMod = this.getWeaponDamageAbility();
		return getMod(stats.getInt(weaponMod));
	}

	private int getWeaponEnhancementBonus(StatTotals stats) {
		return stats.getInt("weapon enhancement bonus");
	}

	private double getWeaponW(StatTotals stats) {
		return stats.get("[w]");
	}

	protected double getBonusWs(StatTotals stats) {
		return stats.get("bonus w");
	}

	protected int getMeleePower(StatTotals stats) {
		return stats.getInt("melee power");
	}

	protected int getRangedPower(StatTotals stats) {
		return stats.getInt("ranged power");
	}
	
	@Override
	protected double getCooldown(StatTotals stats) { return 0; }
	
	@Override
	protected double getExecuteTime(StatTotals stats) {
		double swingsPerMinute = this.getBaseSwingsPerMinute() * (1 + stats.getDouble("melee alacrity") / 100.0 * this.getAlacrityScaling());
		double swingsPerSecond = swingsPerMinute / 60;
		double secondsPerSwing = 1 / swingsPerSecond;
		return secondsPerSwing;
	}

	/** Must return this attack type's alacrity scaling, as a multiplier. Typically around 1.2. For example, if 10% alacrity produces 12% mroe attacks per time, the sacling is 1.2. */
	protected abstract double getAlacrityScaling();
	/** Must return this attack type's base number of swings per minute, at 0% alacrity. */
	protected abstract int getBaseSwingsPerMinute();
	/** Must return the name of the ability that will be used for weapon to-hit. For most melee weapons, this will be "strength". */
	protected abstract String getWeaponAttackAbility();
	/** Must return the name of the ability that will be used for weapon damage. For most melee weapons, this will be "strength". */
	protected abstract String getWeaponDamageAbility();
	/** Must return this attack's damage stat scaling coefficient. For most weapons, this is 1. For two-handed melee weapons, this is 1.5. */
	protected abstract double getWeaponDamageStatScaling();
	
	protected static int getMod(int score) { return (score - 10) / 2; }
	
	protected static double cap(double minimum, double value, double maximum) {
		return Math.max(minimum, Math.min(value, maximum));
	}
	
	/** Returns a string representation of the given <code>double</code> as a percentage. For example, 0.92 will return <code>"92%"</code>. */
	protected static String toPercentText(double value) {
		int percent = (int) (value * 100);
		return percent + "%";
	}
	
	protected static String shortFloatText(double value) { return shortFloatText(value, 6); }
	protected static String shortFloatText(double value, int maxChars) {
		String ret = ("" + value);
		return ret.substring(0, Math.min(maxChars, ret.length()));
	}
}
