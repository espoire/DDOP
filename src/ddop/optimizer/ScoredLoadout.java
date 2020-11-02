package ddop.optimizer;

import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.valuation.StatScorer;

public class ScoredLoadout {
    public double score = -Double.MAX_VALUE;
    public EquipmentLoadout loadout;
    
    public static ScoredLoadout score(EquipmentLoadout el, StatScorer ss) {
        ScoredLoadout ret = new ScoredLoadout();
        
        ret.score = ss.score(el);
        ret.loadout = el;
        
        return ret;
    }
}
