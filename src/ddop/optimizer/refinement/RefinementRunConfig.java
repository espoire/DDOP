package ddop.optimizer.refinement;

import ddop.item.ItemSlot;
import ddop.item.PrunedItemMapUtil;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.scoring.scored.RandomAccessScoredItemList;
import ddop.optimizer.scoring.scorers.ValuationContext;
import util.MillisTo;
import util.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RefinementRunConfig {
    private static final long THROUGHPUT_ESTIMATE_LOADOUTS_PER_MINUTE = 1500000;
    private static final long THROUGHPUT_ESTIMATE_LOADOUTS_PER_MILLI = THROUGHPUT_ESTIMATE_LOADOUTS_PER_MINUTE / MillisTo.MINUTES;

    public final int    slotSwaps;
    public final double minimumItemQuality;
    public final PrunedItemMapUtil.IncludeSources itemCategories;

    protected RefinementRunConfig(int slotSwaps, double minimumItemQuality, PrunedItemMapUtil.IncludeSources itemCategories) {
        this.slotSwaps = slotSwaps;
        this.minimumItemQuality = minimumItemQuality;
        this.itemCategories = itemCategories;
    }

    private Map<ItemSlot, RandomAccessScoredItemList> options;
    public Map<ItemSlot, RandomAccessScoredItemList> getOptions() { return this.getOptions(null, null); }
    private Map<ItemSlot, RandomAccessScoredItemList> getOptions(EquipmentLoadout toRefine, ValuationContext vc) {
        if(this.options == null)
            this.options = PrunedItemMapUtil.generate(vc, toRefine.getUnfilledSlots(), this.minimumItemQuality, this.itemCategories);
        return this.options;
    }

    protected void initialize(EquipmentLoadout toRefine, ValuationContext vc) {
        this.getPermutationsCount(this.getOptions(toRefine, vc), this.slotSwaps);
    }

    protected void printPrerunSummary(boolean includeItemsBySlot) {
        System.out.println("\nItem sources:");

        System.out.print("Wiki Named items, Custom items, ");
        if(this.itemCategories == PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS || this.itemCategories == PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS_CANNITH)
            System.out.print("Slavers crafted items, ");
        if(this.itemCategories == PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS_CANNITH)
            System.out.print("Cannith crafted items, ");
        System.out.println();

        System.out.println("Using items which are at least " + NumberFormat.percent(this.minimumItemQuality) + " of best-in-slot.");

        if(includeItemsBySlot) PrunedItemMapUtil.printOptionsList(this.getOptions());

        long permutations = this.getPermutationsCount();
        System.out.println("Loadouts for " + this.slotSwaps + "-slot exhaustive search: " + NumberFormat.readableLargeNumber(permutations));

        System.out.println("Expected run duration per upgrade found: " + NumberFormat.readableLongTime(permutations / THROUGHPUT_ESTIMATE_LOADOUTS_PER_MILLI));
        System.out.println();
    }

    private long permutations;
    private long getPermutationsCount() { return this.getPermutationsCount(null, 0); }
    private long getPermutationsCount(Map<ItemSlot, RandomAccessScoredItemList> options, int slotSwaps) {
        if(this.permutations == 0)
            this.permutations =
                    recursiveComputePermutations(
                            options,
                            new ArrayList<>(options.keySet()),
                            0,
                            slotSwaps);
        return this.permutations;
    }

    private static long recursiveComputePermutations(Map<ItemSlot, RandomAccessScoredItemList> optionsMap, List<ItemSlot> slots, int index, int swaps) {
        if(swaps == 0) return 1;
        if(slots.size() - index - 1 < swaps) return 0;
        ItemSlot is = slots.get(index);

        return optionsMap.get(is).size() * recursiveComputePermutations(optionsMap, slots, index + 1, swaps - 1) +
                recursiveComputePermutations(optionsMap, slots, index + 1, swaps);
    }
}
