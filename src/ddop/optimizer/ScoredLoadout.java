package ddop.optimizer;

import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.valuation.StatScorer;

import java.util.Map;

public class ScoredLoadout {
    public double score = -Double.MAX_VALUE;
    public EquipmentLoadout loadout;
    public Map<ItemSlot, RandomAccessScoredItemList> context;
    
    public static ScoredLoadout score(EquipmentLoadout el, StatScorer ss) {
        ScoredLoadout ret = new ScoredLoadout();
        
        ret.score = ss.score(el);
        ret.loadout = el;
        
        return ret;
    }

    public void annotate(Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        for(ItemSlot is : itemMap.keySet())
            itemMap.get(is).normalize();

        this.context = itemMap;
    }

    public String toString() {
        return this.loadout.toString(this.context);
    }
}
