package ddop.optimizer.valuation;

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
        this.baseScore = scorer.score(stats, null, true);
    }
    
    public ValuationContext with(StatSource additionalStats) {
        if(additionalStats == null) return this;
        Collection<Stat> added = additionalStats.getStats();
        if(added.size() == 0) return this;
        
        return new ValuationContext(this.scorer, this.stats.combine(additionalStats));
    }
    
    public double score(StatSource statsToScore, boolean relaxArtifactConstraint) {
        AbstractStatList sl = new FastStatList(this.scorer.getQueriedStatCategories(), this.stats, statsToScore);
        return scorer.score(sl, relaxArtifactConstraint) - this.baseScore;
    }

    public StatFilter getQueriedStatCategories() { return this.scorer.getQueriedStatCategories(); }
    public Set<ArmorType> getAllowedArmorTypes() { return this.scorer.getAllowedArmorTypes(); }
}
