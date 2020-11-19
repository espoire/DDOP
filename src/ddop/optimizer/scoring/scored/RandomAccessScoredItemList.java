package ddop.optimizer.scoring.scored;

import ddop.item.Item;
import ddop.optimizer.scoring.scorers.ValuationContext;
import util.Random;

import java.util.List;

public class RandomAccessScoredItemList {
    private final ScoredItemList inner;
    private final double totalScore;
    
    public RandomAccessScoredItemList(ScoredItemList toWrap) {
        this.inner = toWrap;
        double totalScore = 0;
        for(ScoredItem si : this.inner.scoredItems) { totalScore += si.score; }
        this.totalScore = totalScore;
    }
    
    public Item getRandom() {
        double roll = Random.random(0.0, this.totalScore);
        for(ScoredItem si : this.inner.scoredItems) {
            roll -= si.score;
            if(roll <= 0) return si.getItem();
        }
        return null;
    }

    public Item getRandomUnweighted() {
        return ((ScoredItem) Random.random(this.inner.scoredItems)).getItem();
    }
    
    public int size() {
        return this.inner.size();
    }
    
    public ScoredItem getBest() {
        return inner.getBest();
    }
    
    RandomAccessScoredItemList rescore(ValuationContext vc) {
        return new RandomAccessScoredItemList(this.inner.rescore(vc));
    }

    public ScoredItemList getInner() {
        return inner;
    }

    public void normalize() {
        inner.normalizeScoresTo(1);
    }

    public double getScore(Item i) {
        return inner.getScore(i);
    }

    public List<Item> getItems() {
        return this.inner.getItems();
    }
}
