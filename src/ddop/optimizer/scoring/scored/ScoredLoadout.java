package ddop.optimizer.scoring.scored;

import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.scoring.scorers.StatScorer;

import java.util.Map;

public class ScoredLoadout {
    public double score = -Double.MAX_VALUE;
    public EquipmentLoadout loadout;
    public Map<ItemSlot, RandomAccessScoredItemList> context;

    public ScoredLoadout() {}
    public ScoredLoadout(ScoredLoadout another) {
        this.score = another.score;
        this.loadout = new EquipmentLoadout(another.loadout);
        this.context = another.context;
    }

    public static ScoredLoadout score(EquipmentLoadout loadout, StatScorer scorer) {
        ScoredLoadout ret = new ScoredLoadout();

        ret.loadout = loadout;
        ret.score = scorer.score(ret.loadout).getKey();

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


    public EquipmentLoadout swap(ScoredLoadout another) {
        EquipmentLoadout ret = this.loadout;

        this.score = another.score;
        this.loadout = another.loadout;
        this.context = another.context;

        return ret;
    }
}
