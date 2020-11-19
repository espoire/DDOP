package ddop.optimizer.scoring.scorers.damage.weapon;

import util.StatTotals;

import java.util.Arrays;
import java.util.Set;

public abstract class MeleeWeaponAttack extends WeaponAttack {
	protected Set<String> getBaseQueriedStatCategories() {
		Set<String> ret = super.getBaseQueriedStatCategories();

		ret.addAll(Arrays.asList(
				"doublestrike",
				"melee power"
		));

		return ret;
	}

	@Override
	protected int getWeaponPower(StatTotals stats) { return this.getMeleePower(stats); }
	
	@Override
	protected double getWeaponDoubles(StatTotals stats) { return this.getDoublestrike(stats); }
}
