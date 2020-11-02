package ddop.optimizer.valuation.damage.weapon;

import ddop.optimizer.valuation.damage.DamageSource;
import util.StatTotals;

public abstract class WeaponAttack extends DamageSource {
	protected static final int		BASE_CRITICAL_THREAT_RANGE = 1; // TODO weapon stats
	
	protected static final int		REAPER_SKULLS_ENEMY_AC				= 3;
	
	protected static final int		SIM_ENEMY_AC						= 150;
	protected static final double	SIM_ENEMY_FORT             			= 1.0,
									SIM_CANNITH_COMBAT_INFUSION_UPTIME	= 0.7,
									SIM_HELPLESS_UPTIME					= 0.15,
									SIM_RELENTLESS_FURY_UPTIME			= 0.5;
	
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
		double nonScalingBonusDamage = this.getWeaponBonusDamage(stats)	// TODO weapon stats
				+ (stats.getBoolean("eternal holy burst")   ? 4.16 : 0)
				+ (stats.getBoolean("soul of the elements") ? 15   : 0)
				+ (stats.getBoolean("Stormreaver's Thunderclap:") ? 1000 / 20 : 0);
		double scalingBonusDamage = this.getScalingBonusDamage(stats);
		double bonusDamage = (nonScalingBonusDamage + scalingBonusDamage) * magicalDamageMultiplier;
		
		double fortification	= this.getModifiedEnemyFortification(stats);
		double sneakRate		= this.getSneakAttackOcurrenceRate(stats);
		
		double totalToHit					= this.getTotalToHit(stats);
		double totalCriticalConfirmation	= this.getTotalCriticalConfirmation(stats);
		
		int enemyAC = this.getEnemyAC(this.skulls);
		
		double hitChance     = this.getHitChance(stats);
		double confirmChance = cap(0.05, stats.get("percent to-hit") / 100.0 + (totalCriticalConfirmation + 10.5) / (2 * enemyAC), 0.95);
		
		int		threatRange		= BASE_CRITICAL_THREAT_RANGE + stats.getInt("critical threat range");
		double	threatChance	= 0.05 * threatRange;
		double	critRate		= threatChance * confirmChance * (1 - fortification);
		double	hitRate			= hitChance - critRate;
		double	grazingRate		= 0.95 - hitChance;
		
		double averageSneakDamage	= sneakAttackDamage * sneakRate;
		int critMult				= 2 + stats.getInt("critical multiplier");
		int overwhelmingCrit		= stats.getInt("overwhelming critical");
		double averageCritMult		= critMult + overwhelmingCrit * (0.1 / threatChance);
		double critPhysical			= (physicalDamage + bonusCritDamage) * averageCritMult;
		double critDamage			= critPhysical     + averageSneakDamage + bonusDamage;
		double hitDamage			= physicalDamage   + averageSneakDamage + bonusDamage;
		double grazingDamage		= grazingHitDamage + averageSneakDamage;
		
		double damagePerAttack = critDamage * critRate + hitDamage * hitRate + grazingDamage * grazingRate;
		double attacksPerSwing = this.getWeaponAttacksPerSwing(stats);

		double relentlessFuryMultiplier = 1 + (stats.getBoolean("relentless fury") ? 0.05 * SIM_RELENTLESS_FURY_UPTIME : 0);
		
		double regularDamage	= damagePerAttack * attacksPerSwing * relentlessFuryMultiplier;
		double helplessDamage	= regularDamage * 1.5 * (1 + stats.get("damage vs helpless") / 100);
		double combinedDamage = (regularDamage * (1 - SIM_HELPLESS_UPTIME) + helplessDamage * SIM_HELPLESS_UPTIME);
		
		double score = combinedDamage;
		
		if(verbose) {
			double swingsPerSecond	= this.getActionsPerSecond(stats);
			double attacksPerSecond	= swingsPerSecond * attacksPerSwing;
			
			double damagePerSecond	= swingsPerSecond * regularDamage;
			double dpsVsHelpless	= swingsPerSecond * helplessDamage;
			
			System.out.println("WeaponAttack DPS Debug Log\n"
					+ "+- Weapon:    " + shortFloatText(weaponBonusWs, 5) + "[" + shortFloatText(weaponW, 4) + "] + " + deadly + " = " + (weaponBonusWs * weaponW + deadly) + "\n"
					+ "+- Critical:  " + getCritProfileText(threatRange, critMult, overwhelmingCrit) + "\n"
					+ "+- WeaponPwr: " + weaponPower + " (" + toPercentText(1 + weaponPower / 100.0) + ")\n"
					+ "+- Physical:  " + (int) physicalDamage		+ " (" + (int) critPhysical + " crit)\n"
					+ "+- Sneak:     " + (int) sneakAttackDamage		+ "\n"
					+ "+- Bonus:     " + (int) bonusDamage	+ "\n"
					+ "+- To-Hit:    " + (int) totalToHit + " (" + toPercentText(hitChance)	+ " hit, " + toPercentText(confirmChance) + " confirm)\n"
				    + "+- Ave. Dmg:  " + (int) damagePerAttack + "\n"
					+ "+- Hits/Sec:  " + shortFloatText(attacksPerSecond * hitChance, 4) + " (" + toPercentText(hitChance) + " hit * " + shortFloatText(swingsPerSecond, 4) + " SpS * " + shortFloatText(attacksPerSwing, 4) + " DS + OH)\n"
				    + "+- DPS:       " + (int) damagePerSecond + " (" + (int) dpsVsHelpless + " helpless)\n"
				    + "+- DpAtk:     " + (int) score);
		}
		
		return combinedDamage;
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
	
	private double getWeaponAttacksPerSwing(StatTotals stats) {
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
	
	public int getEnemyAC(int skulls) { return SIM_ENEMY_AC + REAPER_SKULLS_ENEMY_AC * this.skulls; }
	
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
		if(value < minimum) return minimum;
		if(value > maximum) return maximum;
		return value;
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
