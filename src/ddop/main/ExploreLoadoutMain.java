package ddop.main;

import ddop.item.loadout.EquipmentLoadout;
import ddop.item.loadout.StoredLoadouts;
import ddop.optimizer.valuation.HealbardScorer;
import ddop.optimizer.valuation.StatScorer;

public class ExploreLoadoutMain {
    public static void main(String... s) {
        EquipmentLoadout loadout = StoredLoadouts.getHealbardNoSetGear();
        StatScorer        scorer = HealbardScorer.create(26).r(5);
        
        displayGearPlan(loadout, scorer);
    }
    
    protected static void displayGearPlan(EquipmentLoadout el, StatScorer ss) {
        el.printItemNamesToConsole();
        el.printStatTotalsToConsole();
        ss.showVerboseScoreFor(el);
    }
}
