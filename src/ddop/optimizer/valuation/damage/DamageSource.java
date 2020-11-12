package ddop.optimizer.valuation.damage;

import util.StatTotals;

import java.util.Set;

public abstract class DamageSource {
	public int skulls = 0;

	public double getDpET(StatTotals stats, boolean verbose) {
		double damage = this.getDamage(stats, verbose);
		double executeTime = this.getExecuteTime(stats);
		
		return damage / executeTime;
	}

	public double getActiveTime(StatTotals stats) {
		double executeTime = this.getExecuteTime(stats);
		double cooldown = this.getCooldown(stats);
		
		return executeTime / (executeTime + cooldown);
	}
	
	protected double getActionsPerSecond(StatTotals stats) {
		return 1 / this.getExecuteTime(stats);
	}
	
	/** Must return the expected mean damage per activation. */
	protected abstract double getDamage(StatTotals stats, boolean verbose);
	/** Must return the minimum time between starting activations, in seconds. */
	protected abstract double getCooldown(StatTotals stats);
	/** Must return the minimum time before starting a different ability, in seconds. */
	protected abstract double getExecuteTime(StatTotals stats);
	/** Must return a set of all the stat category names which this damage source will query during scoring. */
    public abstract Set<String> getQueriedStatCategories();
}
