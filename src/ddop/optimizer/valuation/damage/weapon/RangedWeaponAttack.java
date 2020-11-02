package ddop.optimizer.valuation.damage.weapon;

import util.StatTotals;

public abstract class RangedWeaponAttack extends WeaponAttack {
	@Override
	protected int getWeaponPower(StatTotals stats) { return this.getRangedPower(stats); }
	
	@Override
	protected double getWeaponDoubles(StatTotals stats) { return this.getDoubleshot(stats); }
}
