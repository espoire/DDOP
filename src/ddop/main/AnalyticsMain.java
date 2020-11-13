package ddop.main;

import ddop.dto.LevelRange;
import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.item.loadout.StoredLoadouts;
import ddop.optimizer.ScoredItemList;
import ddop.optimizer.valuation.ValuationContext;

public class AnalyticsMain {
    public static void main(String... s) {
        StoredLoadouts.getHealbardCandidateGear().compareTo(StoredLoadouts.getHealbardNoSetGear());
    }
    
    private static void showBestItemsByLevelRangeAndSlot(ValuationContext vc, LevelRange levelRange, ItemSlot slot) {
        ItemList items = ItemList.getAllNamedItems().filterByLevel(levelRange).filterBy(slot);
        ScoredItemList scores = new ScoredItemList(items, vc).normalizeScoresTo(100).trim(0.15);
        System.out.println();
        System.out.println(scores.toString(slot.name + " item ranking:"));
    }
}
