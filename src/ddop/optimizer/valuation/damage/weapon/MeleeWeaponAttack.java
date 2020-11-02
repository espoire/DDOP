package ddop.optimizer.valuation.damage.weapon;

import util.StatTotals;

public abstract class MeleeWeaponAttack extends WeaponAttack {
	@Override
	protected int getWeaponPower(StatTotals stats) { return this.getMeleePower(stats); }
	
	@Override
	protected double getWeaponDoubles(StatTotals stats) { return this.getDoublestrike(stats); }
}
