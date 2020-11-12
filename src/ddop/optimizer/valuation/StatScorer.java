package ddop.optimizer.valuation;

import ddop.stat.list.AbstractStatList;
import ddop.stat.list.FastStatList;
import ddop.stat.StatSource;

import java.util.HashSet;
import java.util.Set;

public abstract class StatScorer {
	protected boolean verbose = false;
	protected int     skulls  = 0;
	
	public             double score(StatSource ss)                             { return this.score(ss, null); }
	private            double score(StatSource ss,  Double scoreToNormalizeTo) {
		Set<String> filter = this.getQueriedStatCategories();
		AbstractStatList stats = new FastStatList(filter, ss);
		return this.score(stats, scoreToNormalizeTo);
	}
	protected abstract double score(AbstractStatList stats, Double scoreToNormalizeTo);
	public abstract Set<String> getQueriedStatCategories();
	
	public StatScorer setVerbose(boolean b) { this.verbose = b; return this; }
	public StatScorer r(int skulls)         { this.skulls = skulls; return this; }
	protected Set<ArmorType> getAllowedArmorTypes() { return new HashSet<>(); }
	
	public void showVerboseScoreFor(StatSource ss) { this.showVerboseScoreFor(ss, null); }
	public void showVerboseScoreFor(StatSource ss, Double scoreToNormalizeTo) {
		this.setVerbose(true);
		this.score(ss, scoreToNormalizeTo);
		this.setVerbose(false);
	}

	protected static double cap(double minimum, double value, double maximum) {
		return Math.max(minimum, Math.min(value, maximum));
	}
}
