package ddop.optimizer.valuation;

import ddop.stat.StatFilter;
import ddop.stat.StatSource;
import ddop.stat.list.AbstractStatList;
import ddop.stat.list.FastStatList;
import util.Pair;

import java.util.HashSet;
import java.util.Set;

public abstract class StatScorer {
	public enum Verbosity {
		NONE,
		SUMMARY,
		FULL
	}

	protected Verbosity verbosity = Verbosity.NONE;
	protected int       skulls    = 0;
	
	public Pair<Double, String> score(StatSource ss)                                  { return this.score(ss, null, false); }
	public Pair<Double, String> score(StatSource ss, boolean relaxArtifactConstraint) { return this.score(ss, null, relaxArtifactConstraint); }
	public Pair<Double, String> score(AbstractStatList asl, boolean relaxArtifactConstraint) { return this.score(asl, null, relaxArtifactConstraint); }
	public Pair<Double, String> score(StatSource ss, Double scoreToNormalizeTo, boolean relaxArtifactConstraint) {
		StatFilter filter = this.getQueriedStatCategories();
		AbstractStatList stats = new FastStatList(filter, ss);
		return this.score(stats, scoreToNormalizeTo, relaxArtifactConstraint);
	}
	protected abstract Pair<Double, String> score(AbstractStatList stats, Double scoreToNormalizeTo, boolean relaxArtifactConstraint);
	public abstract StatFilter getQueriedStatCategories();
	
	public StatScorer setVerbosity(Verbosity v) { this.verbosity = v;   return this; }
	public StatScorer r           (int  skulls) { this.skulls = skulls; return this; }
	protected Set<ArmorType> getAllowedArmorTypes() { return new HashSet<>(); }
	
	public void showVerboseScoreFor(StatSource ss) { this.showVerboseScoreFor(ss, null); }
	public void showVerboseScoreFor(StatSource ss, Double scoreToNormalizeTo) { this.showScoreSummaryFor(ss, scoreToNormalizeTo, Verbosity.FULL); }
	public void showScoreSummaryFor(StatSource ss, Double scoreToNormalizeTo, Verbosity verbosity) { System.out.println(this.getScoreSummaryFor(ss, scoreToNormalizeTo, verbosity)); }
	public String getScoreSummaryFor(StatSource ss, Double scoreToNormalizeTo, Verbosity verbosity) {
		this.setVerbosity(verbosity);
		Pair<Double, String> ret = this.score(ss, scoreToNormalizeTo, false);
		this.setVerbosity(Verbosity.NONE);

		return ret.getValue();
	}

	protected static double cap(double minimum, double value, double maximum) {
		return Math.max(minimum, Math.min(value, maximum));
	}
}
