package ddop.optimizer.scoring.scorers;

import ddop.constants.ArmorType;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.scoring.scored.ScoredLoadout;
import ddop.stat.Stat;
import ddop.stat.StatFilter;
import ddop.stat.StatSource;
import ddop.stat.list.AbstractStatList;
import ddop.stat.list.FastStatList;

import java.util.Collection;
import java.util.Set;

public class ValuationContext {
    private final StatScorer scorer;
    public final StatSource stats;
    private final double baseScore;
    
    public ValuationContext(StatScorer scorer, StatSource stats) {
        this.scorer = scorer;
        this.stats  = stats;
        this.baseScore = scorer.score(stats, null, true).getKey();
    }
    
    public ValuationContext with(StatSource additionalStats) {
        if(additionalStats == null) return this;
        Collection<Stat> added = additionalStats.getStats();
        if(added.size() == 0) return this;
        
        return new ValuationContext(this.scorer, this.stats.combine(additionalStats));
    }
    
    public double score(StatSource statsToScore, boolean relaxArtifactConstraint) {
        AbstractStatList sl = new FastStatList(this.scorer.getQueriedStatCategories(), this.stats, statsToScore);
        return scorer.score(sl, relaxArtifactConstraint).getKey() - this.baseScore;
    }
    public ScoredLoadout scoreLoadout(EquipmentLoadout toRefine) {
        return ScoredLoadout.score(toRefine, this.scorer);
    }

    public StatFilter getQueriedStatCategories() { return this.scorer.getQueriedStatCategories(); }
    public Set<ArmorType> getAllowedArmorTypes() { return this.scorer.getAllowedArmorTypes(); }

    public void showScoreSummaryFor(StatSource ss, Double scoreToNormalizeTo, StatScorer.Verbosity verbosity) {
        System.out.println(this.getScoreSummaryFor(ss, scoreToNormalizeTo, verbosity));
    }

    public String getScoreSummaryFor(StatSource ss, Double scoreToNormalizeTo, StatScorer.Verbosity verbosity) {
        return scorer.getScoreSummaryFor(ss, scoreToNormalizeTo, verbosity);
    }
}
