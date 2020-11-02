package ddop.optimizer.valuation.damage.weapon;

import util.StatTotals;

public class InquisitiveAirSentNightshade extends RangedWeaponAttack {
	@Override
	protected double getWeaponBonusDamage(StatTotals stats) {
		double poisonous	=  9 * 3.5;				// 9d6    poison
		double sovVorpal	=      300 * 1 / 20;	// 300    bane		on vorpal
		double augment		=  1 * 6.5;         	// 1d12   red augment
		double holyStrike	= 10 * 3.5;				// 10d6   evilbane
		
		return poisonous + augment + holyStrike;
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
