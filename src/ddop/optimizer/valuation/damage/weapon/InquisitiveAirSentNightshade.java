package ddop.optimizer.valuation.damage.weapon;

import util.StatTotals;

import java.util.Arrays;
import java.util.Set;

public class InquisitiveAirSentNightshade extends RangedWeaponAttack {
	@Override
	public Set<String> getQueriedStatCategories() {
		Set<String> ret = super.getBaseQueriedStatCategories();

		ret.addAll(Arrays.asList(
				"spellcraft",
				"intelligence",
				"magnetism",
				"universal spell power"
		));

		return ret;
	}

	@Override
	protected double getWeaponBonusDamage(StatTotals stats) {
		double poisonous	=  9 * 3.5;				// 9d6    poison
		double sovVorpal	=      300.0 * 1 / 20;	// 300    bane		on vorpal
		double augment		=  8 * 3.5;         	// 8d6    red augment
		double holyStrike	= 10 * 3.5;				// 10d6   evil bane
		
		return poisonous + sovVorpal + augment + holyStrike;
	}

	@Override
	protected double getScalingBonusDamage(StatTotals stats) {
		double baseLawOnYourSide	= 14 * 4.5;		// 14d8  law,   scaling 200% RAP
		double baseScionOfAir		=  2 * 10.5;	//  2d10 elect, scaling      elect SpP
		
		int rangedAttackPower	= this.getRangedPower(stats);
		int electricSpellPower	= this.getElectricSpellPower(stats);
		
		double modifiedLawOnYourSide	= baseLawOnYourSide * (1 + rangedAttackPower  / 50.0);
		double modifiedScionOfAir		= baseScionOfAir    * (1 + electricSpellPower / 100.0);
		
		return modifiedLawOnYourSide + modifiedScionOfAir;
	}

	private int getElectricSpellPower(StatTotals stats) {
		int spellcraft = stats.getInt("spellcraft")
				+ getMod(stats.getInt("intelligence"));
		int magnetism = stats.getInt("magnetism");
		int universal = stats.getInt("universal spell power");
		
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
