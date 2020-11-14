package ddop.optimizer;

import ddop.item.Item;
import ddop.optimizer.valuation.ValuationContext;

public class ScoredItem extends ScoredStatSource<Item> implements Cloneable {
    public ScoredItem(Item source, ValuationContext context) {
        super(source, context, true);
    }
    private ScoredItem(double score, Item source) {
        super(score, source);
    }
    
    public Item getItem() {
        return (Item) this.source;
    }
    
    public String toString() {
        return Math.round(this.score) + ": " + this.getItem().name + " (ML:" + this.getItem().minLevel + ")";
    }

    @Override
    public ScoredItem normalizeScoreTo(double normalizationFactor, double bestScore) {
        double score = this.score / bestScore * normalizationFactor;

        return new ScoredItem(score, this.source);
    }
}
