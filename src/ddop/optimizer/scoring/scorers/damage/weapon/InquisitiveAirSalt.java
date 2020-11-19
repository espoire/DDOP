package ddop.optimizer.scoring.scorers.damage.weapon;

import util.StatTotals;

import java.util.Arrays;
import java.util.Set;

public class InquisitiveAirSalt extends RangedWeaponAttack {
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
