package ddop.main;

import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.item.loadout.StoredLoadouts;
import ddop.optimizer.ScoredItemList;
import ddop.optimizer.valuation.ArmorType;
import ddop.optimizer.valuation.ValuationContext;

import java.util.Map;

public class AnalyticsMain {
    public static void main(String... s) {
        StoredLoadouts.getHealbardCandidateGear().compareTo(StoredLoadouts.getHealbardNoSetGear());

//        EquipmentLoadout fixed = new EquipmentLoadout(PlanLoadoutMain.getFixedItems());
//
//        ValuationContext vc = new ValuationContext(new ShintaoScorer(30).r(8), new StatCollection());
//        showBestItemsByLevelRangeAndSlot(vc, 23, 30, ItemSlot.WAIST);
    }
    
    private static void showBestItemsByLevelRangeAndSlot(ValuationContext vc, int startLevel, int endLevel, ItemSlot slot) {
        ItemList items = ItemList.getAllNamedItems().filterByLevel(startLevel, endLevel).filterBy(slot);
        ScoredItemList scores = new ScoredItemList(items, vc).normalizeScoresTo(100).trim(0.15);
        System.out.println();
        System.out.println(scores.toString(slot.name + " item ranking:"));
    }
    
    private static void listItemCountsAtLeastLevelByType(int startMinLevel, int endMinLevel) {
        ItemList allNamedItems  = ItemList.getAllNamedItems();
        ItemList highLevelItems = allNamedItems.filterByLevel(20, -1);
    
        for(int level = 20; level <= 30; level++) {
            highLevelItems = highLevelItems.filterByLevel(level, -1);
            Map<ItemSlot, ItemList> highLevelItemsBySlot = highLevelItems.mapBySlot();
            System.out.println("Items over level " + level + ", by slot:");
            for(ItemSlot slot : highLevelItemsBySlot.keySet()) {
                System.out.println("|- " + slot.name + " - " + highLevelItemsBySlot.get(slot).size());
                if(slot == ItemSlot.ARMOR) {
                    Map<ArmorType, ItemList> armorByType = highLevelItemsBySlot.get(slot).mapByArmorType();
                    for(ArmorType type : armorByType.keySet()) {
                        System.out.println("   |- " + type + " - " + armorByType.get(type).size());
                    }
                }
            }
            System.out.println();
        }
    }
}
