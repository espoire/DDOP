package ddop.optimizer.valuation;

import ddop.stat.Stat;
import ddop.stat.list.VerboseStatList;
import ddop.stat.StatSource;

import java.util.Collection;
import java.util.Set;

public class ValuationContext {
    private final StatScorer scorer;
    public final StatSource stats;
    private final double baseScore;
    
    public ValuationContext(StatScorer scorer, StatSource stats) {
        this.scorer = scorer;
        this.stats  = stats;
        this.baseScore = scorer.score(null);
    }
    
    public ValuationContext with(StatSource additionalStats) {
        if(additionalStats == null) return this;
        Collection<Stat> added = additionalStats.getStats();
        if(added.size() == 0) return this;
        
        return new ValuationContext(this.scorer, this.stats.combine(additionalStats));
    }
    
    public double score(StatSource statsToScore) {
        VerboseStatList sl = new VerboseStatList(this.stats, statsToScore);
        return scorer.score(sl) - this.baseScore;
    }

    public Set<ArmorType> getAllowedArmorTypes() { return this.scorer.getAllowedArmorTypes(); }
}
