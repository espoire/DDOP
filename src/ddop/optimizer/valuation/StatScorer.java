package ddop.optimizer.valuation;

import ddop.stat.StatFilter;
import ddop.stat.StatSource;
import ddop.stat.list.AbstractStatList;
import ddop.stat.list.FastStatList;

import java.util.HashSet;
import java.util.Set;

public abstract class StatScorer {
	protected boolean verbose = false;
	protected int     skulls  = 0;
	
	public             double score(StatSource ss)                                  { return this.score(ss, null, false); }
	public             double score(StatSource ss, boolean relaxArtifactConstraint) { return this.score(ss, null, relaxArtifactConstraint); }
	public             double score(AbstractStatList asl, boolean relaxArtifactConstraint) { return this.score(asl, null, relaxArtifactConstraint); }
	public             double score(StatSource ss, Double scoreToNormalizeTo, boolean relaxArtifactConstraint) {
		StatFilter filter = this.getQueriedStatCategories();
		AbstractStatList stats = new FastStatList(filter, ss);
		return this.score(stats, scoreToNormalizeTo, relaxArtifactConstraint);
	}
	protected abstract double score(AbstractStatList stats, Double scoreToNormalizeTo, boolean relaxArtifactConstraint);
	public abstract StatFilter getQueriedStatCategories();
	
	public StatScorer setVerbose(boolean b) { this.verbose = b;     return this; }
	public StatScorer r(int skulls)         { this.skulls = skulls; return this; }
	protected Set<ArmorType> getAllowedArmorTypes() { return new HashSet<>(); }
	
	public void showVerboseScoreFor(StatSource ss) { this.showVerboseScoreFor(ss, null); }
	public void showVerboseScoreFor(StatSource ss, Double scoreToNormalizeTo) {
		this.setVerbose(true);
		this.score(ss, scoreToNormalizeTo, false);
		this.setVerbose(false);
	}

	protected static double cap(double minimum, double value, double maximum) {
		return Math.max(minimum, Math.min(value, maximum));
	}
}
