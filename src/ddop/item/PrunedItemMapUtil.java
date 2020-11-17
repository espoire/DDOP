package ddop.item;

import ddop.Settings;
import ddop.dto.LevelRange;
import ddop.item.sources.crafted.CannithCraftedItemSource;
import ddop.item.sources.crafted.SlaversCraftedItemSource;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.optimizer.ScoredItemList;
import ddop.optimizer.valuation.ValuationContext;
import util.Array;
import util.NumberFormat;

import java.util.*;

public class PrunedItemMapUtil {
    public enum IncludeSources {
        WIKI,
        WIKI_SLAVERS,
        WIKI_SLAVERS_CANNITH,
    }

    public static Map<ItemSlot, RandomAccessScoredItemList> generate(ValuationContext vc, double minimumQuality) {
        return generate(vc, new ArrayList<>(Arrays.asList(Settings.IGNORED_SLOTS)), minimumQuality);
    }

    public static Map<ItemSlot, RandomAccessScoredItemList> generate(ValuationContext vc, List<ItemSlot> skipSlots, double minimumQuality) {
        return generate(vc, skipSlots, minimumQuality, IncludeSources.WIKI_SLAVERS_CANNITH);
    }

    public static Map<ItemSlot, RandomAccessScoredItemList> generate(ValuationContext vc, List<ItemSlot> skipSlots, double minimumQuality, IncludeSources sources) {
        Map<ItemSlot, RandomAccessScoredItemList> ret = new HashMap<>();
        LevelRange levelRange = Settings.TARGET_ITEMS_LEVEL_RANGE;
        Set<ItemSlot> includedItemSlots = ItemSlot.getUnskippedSlots(skipSlots);

        ItemList candidates = ItemList.getAllNamedItems()
                .filterByLevel(levelRange)
                .filterByAllowedArmorTypes(vc.getAllowedArmorTypes());

        if(sources == IncludeSources.WIKI_SLAVERS || sources == IncludeSources.WIKI_SLAVERS_CANNITH)
            candidates = candidates.merge(SlaversCraftedItemSource.generateList(levelRange, vc.getQueriedStatCategories()));

        if(sources == IncludeSources.WIKI_SLAVERS_CANNITH)
            candidates = candidates.merge(CannithCraftedItemSource.generateList(levelRange, ItemSlot.getUnskippedSlots(skipSlots), vc.getAllowedArmorTypes(), vc.getQueriedStatCategories()));

        Map<ItemSlot, ItemList> rawItemMap = candidates.mapBySlot(includedItemSlots);

        for(ItemSlot slot : rawItemMap.keySet()) {
            int limit = getNumberOfUnskippedSlots(skipSlots, slot);
            if(limit > 0) {
                ScoredItemList options = new ScoredItemList(rawItemMap.get(slot), vc).trim(minimumQuality);
                options.stripUnusedStats(vc.getQueriedStatCategories());

                if(options.size() > 0)
                    ret.put(slot, new RandomAccessScoredItemList(options));
            }
        }

        return ret;
    }

    private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
        int limit = slot.limit - Array.containsCount(skipSlots, slot);
        return limit;
    }

    public static void printOptionsList(Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        System.out.println("\nItem option list:");

        int totalItemsConsidered = 0;
        double totalCombinations = 1;
        for(Map.Entry<ItemSlot, RandomAccessScoredItemList> slotEntry : itemMap.entrySet()) {
            int options = slotEntry.getValue().size();
            totalItemsConsidered += options;
            if(options > 0) totalCombinations *= options;

            System.out.println("| " + slotEntry.getKey().name + ": " + options + " options.");
        }
        System.out.println();

        System.out.println("Testing " + totalItemsConsidered + " items over " + itemMap.size());
        System.out.println("item slots in a maximum of");
        System.out.println(NumberFormat.readableLargeNumber(totalCombinations) + " possible combinations.");
    }
}
