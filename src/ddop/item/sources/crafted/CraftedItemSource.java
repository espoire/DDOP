package ddop.item.sources.crafted;

import ddop.item.ItemSlot;
import ddop.optimizer.ScoredStatSource;
import ddop.optimizer.ScoredStatSourceList;
import ddop.optimizer.valuation.ValuationContext;
import ddop.stat.StatSource;
import util.Permutations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CraftedItemSource {
    public abstract boolean appliesTo(ItemSlot slot);
    
    public CraftedItem getBest(ItemSlot slot, ValuationContext vc) {
        if(! this.appliesTo(slot)) return null;
    
        Map<String, ScoredStatSourceList> affixLists = this.getScoredAffixLists(vc);
        Permutations<String> affixOrderOptions = new Permutations<>(affixLists.keySet().toArray(new String[] {}));
    
        ScoredStatSource<CraftedItem> best = null;
        for(List<String> affixOrder : affixOrderOptions.permutations) {
            
            CraftedItem candidateItem = new CraftedItem();
            for(String affix : affixOrder) {
                ScoredStatSourceList affixRankings = affixLists.get(affix).rescore(vc.with(candidateItem));
                CraftedOption bestAffix = (CraftedOption) affixRankings.getBest().source;
                candidateItem.addStats(bestAffix);
            }
            
            ScoredStatSource<CraftedItem> thisItem = new ScoredStatSource<CraftedItem>(candidateItem, vc);
            if(best == null || thisItem.score > best.score) best = thisItem;
            
        }
        
        return best.source;
    }
    
    protected Map<String, ScoredStatSourceList> getScoredAffixLists(ValuationContext vc) {
        Map<String, Set<StatSource>> affixLists = this.getAffixLists();
        Map<String, ScoredStatSourceList> ret = new HashMap<>();
        
        for(Map.Entry<String, Set<StatSource>> pair : affixLists.entrySet()) {
            ret.put(pair.getKey(), new ScoredStatSourceList(pair.getValue(), vc));
        }
        
        return ret;
    }
    
    protected abstract Map<String, Set<StatSource>> getAffixLists();
}
