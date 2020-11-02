package ddop.optimizer;

import ddop.optimizer.valuation.ValuationContext;
import ddop.stat.StatSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScoredStatSourceList {
    List<ScoredStatSource> scoredSources = new ArrayList<>();
    
    private ScoredStatSourceList() {}
    public ScoredStatSourceList(Iterable<StatSource> list, ValuationContext vc) {
        for(StatSource ss : list) {
            ScoredStatSource sss = new ScoredStatSource(ss, vc);
            if(sss.score > 0) {
                this.scoredSources.add(sss);
            }
        }
        
        this.scoredSources.sort((i1, i2) -> {
            double delta = i2.score - i1.score;
            if(delta < 0) return -1;
            if(delta > 0) return 1;
            return 0;
        });
    }
    
    /** @param minimumRatioOfBest In the range [0 .. 1]. */
    public ScoredStatSourceList trim(double minimumRatioOfBest) {
        double minimumScore = this.findBestScore() * minimumRatioOfBest;
    
        ScoredStatSourceList ret = new ScoredStatSourceList();
        
        for(int i = 0; i < this.scoredSources.size(); i++) {
            ScoredStatSource sss = this.scoredSources.get(i);
            if(sss.score >= minimumScore) ret.scoredSources.add(sss);
        }
        
        return ret;
    }
    
    public ScoredStatSourceList normalizeScoresTo(double normalizationFactor) {
        if(this.size() == 0) return this;
        double best = this.findBestScore();
        
        for(ScoredStatSource sss : this.scoredSources) sss.normalizeScoreTo(normalizationFactor, best);

        List<ScoredStatSource> temp = new ArrayList<ScoredStatSource>();
        for(ScoredStatSource sss : this.scoredSources) temp.add(sss.normalizeScoreTo(normalizationFactor, best));
        this.scoredSources = temp;

        return this;
    }
    
    private double findBestScore() {
        ScoredStatSource best = this.getBest();
        if(best == null) return Double.NaN;
        return best.score;
    }
    
    public ScoredStatSource getBest() {
        if(this.size() == 0) return null;
        return this.scoredSources.get(0);
    }
    
    public int size() {
        return this.scoredSources.size();
    }
    
    public ScoredStatSourceList rescore(ValuationContext vc) {
        Collection<StatSource> sources = this.getSources();
        return new ScoredStatSourceList(sources, vc);
    }
    
    private Collection<StatSource> getSources() {
        Collection<StatSource> ret = new ArrayList<>();
        for(ScoredStatSource sss : this.scoredSources) ret.add(sss.source);
        return ret;
    }
}
