package ddop.optimizer.valuation.damage.weapon;

import util.StatTotals;

public class InquisitiveAirSalt extends RangedWeaponAttack {
	@Override
	protected double getWeaponBonusDamage(StatTotals stats) {
		double frosty		= 12 * 3.5;			// 12d6   ice
		double evilBurst	= 12 * 5.5  * 3/20;	// 12d10  negat on crit
		double icyBlast		= 12 * 60.5 * 1/20; // 12d120 ice   on vorpal
		double reverb		= 14 * 3.5;         // 15d6   sonic
		double holyStrike	= 10 * 3.5;			// 10d6   evilbane
		
		return frosty + evilBurst + icyBlast + reverb + holyStrike;
	}

	@Override
	protected double getScalingBonusDamage(StatTotals stats) {
		double baseLawOnYourSide	= 14 * 4.5;		// 14d8  law,   scaling 200% RAP
		double baseScionOfAir		=  2 * 10.5;	//  2d10 elect, scaling      elec SpP
		
		int rangedAttackPower	= this.getRangedPower(stats);
		int electricSpellPower	= this.getElectricSpellPower(stats);
		
		double modifiedLawOnYourSide	= baseLawOnYourSide * (1 + rangedAttackPower  / 50.0);
		double modifiedScionOfAir		= baseScionOfAir    * (1 + electricSpellPower / 100.0);
		
		return modifiedLawOnYourSide + modifiedScionOfAir;
	}

	private int getElectricSpellPower(StatTotals stats) {
		int spellcraft = stats.getInt("spellcraft")
				+  0	// ranks
				+ 10	// epic levels
				+  5	// unknown - guild buffs?
				+  2	// good hope
				+ getMod(stats.getInt("intelligence"));
		int magnetism = stats.getInt("magnetism")
				+ 10;	// scion of air
		int universal = stats.getInt("universal spell power")
				+ 30	// scion of air
				+ 60	// epic levels
				+  1	// tome (remnants)
				+  3	// reaper item bonuses
				+  6	// filigree
				+ 46;	// ?
		
		return spellcraft + magnetism + universal;
	}

	@Override
	protected double getAlacrityScaling() {
		return 0.26
				* (1 - 0.18); // No scaling during Fusillade, 18sec * 6 uses / 10min
	}

	@Override
	protected int getBaseSwingsPerMinute() {
		return 154;
	}

	@Override
	protected String getWeaponAttackAbility() {
		return "wisdom";
	}

	@Override
	protected String getWeaponDamageAbility() {
		return "wisdom";
	}

	@Override
	protected double getWeaponDamageStatScaling() {
		return 1.5;
	}

}
