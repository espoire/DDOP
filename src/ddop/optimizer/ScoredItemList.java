package ddop.optimizer;

import ddop.item.Item;
import ddop.optimizer.valuation.ValuationContext;

import java.util.*;

public class ScoredItemList {
	List<ScoredItem> scoredItems = new ArrayList<>();
	
	private ScoredItemList() {}
	public ScoredItemList(Iterable<Item> list, ValuationContext vc) {
		for(Item i : list) {
			ScoredItem si = new ScoredItem(i, vc);
			if(si.score > 0) {
				this.scoredItems.add(si);
			}
		}
		
		this.scoredItems.sort((i1, i2) -> {
			double delta = i2.score - i1.score;
			if(delta < 0) return -1;
			if(delta > 0) return 1;
			return 0;
		});
	}
	
	/** @param minimumRatioOfBest In the range [0 .. 1]. */
	public ScoredItemList trim(double minimumRatioOfBest) {
		double minimumScore = this.findBestScore() * minimumRatioOfBest;
		
		ScoredItemList ret = new ScoredItemList();
		
		for(int i = 0; i < this.scoredItems.size(); i++) {
			ScoredItem si = this.scoredItems.get(i);
			if(si.score >= minimumScore) ret.scoredItems.add(si);
		}
		
		return ret;
	}
	
	public ScoredItemList normalizeScoresTo(double normalizationFactor) {
		if(this.size() == 0) return this;

		double best = this.findBestScore();

		List<ScoredItem> temp = new ArrayList<ScoredItem>();
		for(ScoredItem si : this.scoredItems) temp.add(si.normalizeScoreTo(normalizationFactor, best));
		this.scoredItems = temp;
		
		return this;
	}
	
	private double findBestScore() {
		ScoredItem best = this.getBest();
		if(best == null) return Double.NaN;
		return best.score;
	}
	
	public ScoredItem getBest() {
		if(this.size() == 0) return null;
		return this.scoredItems.get(0);
	}
	
	public int size() {
		return this.scoredItems.size();
	}
	
	@Override
	public String toString() {
		return this.toString("ScoredItemList");
	}
	
	public String toString(String header) {
		StringBuilder ret = new StringBuilder(header + " (" + this.scoredItems.size() + " items):\n");
		for(ScoredItem i : this.scoredItems) {
			ret.append(i).append("\n");
		}
		return ret.toString();
	}
	
	ScoredItemList rescore(ValuationContext vc) {
		Collection<Item> items = this.getItems();
		return new ScoredItemList(items, vc);
	}
	
	private List<Item> getItems() {
		List<Item> ret = new ArrayList<>();
		for(ScoredItem si : this.scoredItems) ret.add(si.source);
		return ret;
	}

    public void stripUnusedStats(Set<String> filter) {
		for(ScoredItem si : this.scoredItems) {
			si.getItem().stripUnusedStats(filter);
		}
    }

	public double getScore(Item i) {
		for(ScoredItem si : this.scoredItems)
			if(si.getItem() == i)
				return si.score;

		return Double.NaN;
	}
}
