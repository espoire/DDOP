package ddop.optimizer.scoring.scorers.damage.weapon;

import util.StatTotals;

import java.util.Arrays;
import java.util.Set;

public class CenteredUnarmedFalconer extends MeleeWeaponAttack {
	@Override
	public Set<String> getQueriedStatCategories() {
		Set<String> ret = super.getBaseQueriedStatCategories();

		ret.addAll(Arrays.asList(
				"reinforced fists",
				"balanced ki strike"
		));

		return ret;
	}

	protected static final double	BONUS_W_PER_REINFORCED_FISTS   = 0.5,
									SIM_BALANCED_KI_STRIKE_UPTIME  = 0.25;
	protected static final int		BALANCED_KI_STRIKE_MELEE_POWER = 5;
	
	@Override
	protected double getAlacrityScaling() { return 1.16; }
	
	@Override
	protected int getBaseSwingsPerMinute() { return 98; }
	
	@Override
	protected String getWeaponAttackAbility() { return "wisdom"; }
	
	@Override
	protected String getWeaponDamageAbility() { return "wisdom"; }
	
	@Override
	protected double getBonusWs(StatTotals stats) {
		return super.getBonusWs(stats) + stats.get("reinforced fists") * BONUS_W_PER_REINFORCED_FISTS;
	}
	
	@Override
	protected double getWeaponDamageStatScaling() { return 1.5; }
	
	@Override
	protected int getMeleePower(StatTotals stats) {
		return super.getMeleePower(stats) + (int) (stats.getBoolean("balanced ki strike") ? BALANCED_KI_STRIKE_MELEE_POWER * SIM_BALANCED_KI_STRIKE_UPTIME : 0);
	}

	@Override
	protected double getScalingBonusDamage(StatTotals stats) { return 0; }

	@Override
	protected double getWeaponBonusDamage(StatTotals stats) { return 63; }
}
