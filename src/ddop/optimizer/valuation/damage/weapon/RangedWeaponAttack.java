package ddop.optimizer.valuation.damage.weapon;

import util.StatTotals;

import java.util.Arrays;
import java.util.Set;

public abstract class RangedWeaponAttack extends WeaponAttack {
	protected Set<String> getBaseQueriedStatCategories() {
		Set<String> ret = super.getBaseQueriedStatCategories();

		ret.addAll(Arrays.asList(
				"doubleshot",
				"ranged power"
		));

		return ret;
	}

	@Override
	protected int getWeaponPower(StatTotals stats) { return this.getRangedPower(stats); }
	
	@Override
	protected double getWeaponDoubles(StatTotals stats) { return this.getDoubleshot(stats); }
}
