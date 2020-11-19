package ddop.optimizer.scoring.scorers.damage;

import ddop.optimizer.scoring.scorers.StatScorer;
import util.Pair;
import util.StatTotals;

import java.util.Set;

public abstract class DamageSource {
	public int skulls = 0;

	public Pair<Double, String> getDpET(StatTotals stats, StatScorer.Verbosity verbosity) {
		Pair<Double, String> result = this.getDamage(stats, verbosity);

		double damage = result.getKey();
		double executeTime = this.getExecuteTime(stats);
		
		return new Pair<>(damage / executeTime, result.getValue());
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
	protected abstract Pair<Double, String> getDamage(StatTotals stats, StatScorer.Verbosity verbose);
	/** Must return the minimum time between starting activations, in seconds. */
	protected abstract double getCooldown(StatTotals stats);
	/** Must return the minimum time before starting a different ability, in seconds. */
	protected abstract double getExecuteTime(StatTotals stats);
	/** Must return a set of all the stat category names which this damage source will query during scoring. */
    public abstract Set<String> getQueriedStatCategories();
}
