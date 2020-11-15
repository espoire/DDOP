package ddop.optimizer;

import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.valuation.StatScorer;

import java.util.Map;

public class ScoredLoadout {
    public double score = -Double.MAX_VALUE;
    public EquipmentLoadout loadout;
    public Map<ItemSlot, RandomAccessScoredItemList> context;
    
    public static ScoredLoadout score(EquipmentLoadout loadout, StatScorer scorer) {
        ScoredLoadout ret = new ScoredLoadout();

        ret.loadout = loadout;
        ret.score = scorer.score(ret.loadout);

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

    public int size() {
        return this.loadout.size();
    }
}
