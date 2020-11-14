package ddop.optimizer;

import ddop.optimizer.valuation.ValuationContext;
import ddop.stat.StatSource;

public class ScoredStatSource<Source extends StatSource> {
    public final double score;
    public final Source source;
    
    protected ScoredStatSource(double score, Source source) {
        this.score = score;
        this.source = source;
    }
    public ScoredStatSource(Source source, ValuationContext context, boolean relaxArtifactConstraint) {
        this.source = source;
        this.score  = context.score(source, relaxArtifactConstraint);
    }
    
    public ScoredStatSource<Source> normalizeScoreTo(double normalizationFactor, double bestScore) {
        double score = this.score / bestScore * normalizationFactor;

        return new ScoredStatSource<>(score, this.source);
    }
}
