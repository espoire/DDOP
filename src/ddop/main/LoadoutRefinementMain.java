package ddop.main;

import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.PrunedItemMapUtil;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.optimizer.ScoredLoadout;
import ddop.optimizer.valuation.ShintaoScorer;
import ddop.optimizer.valuation.StatScorer;
import ddop.optimizer.valuation.ValuationContext;
import file.Writer;
import util.DateTime;
import util.MillisTo;
import util.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadoutRefinementMain {
    public static void main(String... s) {
        StatScorer scorer = ShintaoScorer.create(30).r(8);
        EquipmentLoadout toRefine = getRoughGear();
        ValuationContext vc = new ValuationContext(scorer, toRefine);

        runFullRefinementOn(toRefine, vc);
    }

    private static final long TARGET_TIME_MILLIS = 10 * MillisTo.MINUTES;
    private static final long THROUGHPUT_LOADOUTS_PER_MINUTE = 1500000;
    private static final long THROUGHPUT_LOADOUTS_PER_MILLI = THROUGHPUT_LOADOUTS_PER_MINUTE / MillisTo.MINUTES;

    private static class RefinementRunConfig {
        private final int    slotSwaps;
        private final double minimumItemQuality;
        private final PrunedItemMapUtil.IncludeSources itemCategories;

        private RefinementRunConfig(int slotSwaps, double minimumItemQuality, PrunedItemMapUtil.IncludeSources itemCategories) {
            this.slotSwaps = slotSwaps;
            this.minimumItemQuality = minimumItemQuality;
            this.itemCategories = itemCategories;
        }

        private Map<ItemSlot, RandomAccessScoredItemList> options;
        private Map<ItemSlot, RandomAccessScoredItemList> getOptions() { return this.getOptions(null, null); }
        private Map<ItemSlot, RandomAccessScoredItemList> getOptions(EquipmentLoadout toRefine, ValuationContext vc) {
            if(this.options == null)
                this.options = PrunedItemMapUtil.generate(vc, toRefine.getUnfilledSlots(), this.minimumItemQuality, this.itemCategories);
            return this.options;
        }

        private void initialize(EquipmentLoadout toRefine, ValuationContext vc) {
            this.getPermutationsCount(this.getOptions(toRefine, vc), this.slotSwaps);
        }

        private void printPrerunSummary(boolean includeItemsBySlot) {
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

            System.out.println("Expected run duration per upgrade found: " + NumberFormat.readableLongTime(permutations / THROUGHPUT_LOADOUTS_PER_MILLI));
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

    private static EquipmentLoadout getRoughGear() {
        EquipmentLoadout ret = new EquipmentLoadout();

        ret.put("quiver of alacrity");

        ret.put("legendary turncoat");
        ret.put("legendary hammerfist");
        ret.put("legendary omniscience");
        ret.put("legendary lionheart ring");
        ret.put("signet of the solstice (lamannia - feywild raid)");
        ret.put("legendary cloak of summer");
        ret.put("devilscale bracers");
        ret.put("legendary family recruit sigil");
        ret.put("legendary belt of the ram");
        ret.put("legendary umber brim");
        ret.put("legendary sunken slippers ([quality wisdom +5] version)");
        ret.put("ir'kesslan's most prescient lens");

        return ret;
    }

    public static void runQuickRefinementOn(EquipmentLoadout toRefine, ValuationContext vc) {
        // TODO higher bar for items by source
        RefinementRunConfig[] runConfigs = new RefinementRunConfig[] {
                new RefinementRunConfig(3, 0.15, PrunedItemMapUtil.IncludeSources.WIKI),
                new RefinementRunConfig(2, 0.15, PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS),
                new RefinementRunConfig(1, 0.15, PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS_CANNITH)
        };

        runRefinementImplementation(toRefine, vc, runConfigs);
    }
    public static void runFullRefinementOn(EquipmentLoadout toRefine, ValuationContext vc) {
        // TODO higher bar for items by source
        RefinementRunConfig[] runConfigs = new RefinementRunConfig[] {
                new RefinementRunConfig(4, 0.15, PrunedItemMapUtil.IncludeSources.WIKI),
                new RefinementRunConfig(3, 0.15, PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS),
                new RefinementRunConfig(2, 0.15, PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS_CANNITH)
        };

        runRefinementImplementation(toRefine, vc, runConfigs);
    }

    private static void runRefinementImplementation(EquipmentLoadout toRefine, ValuationContext vc, RefinementRunConfig[] runConfigs) {
        long startTime = System.currentTimeMillis();
        String outputFile = Settings.REFINEMENT_OUTPUT_DIRECTORY + "\\Refinement Log " + DateTime.getCurrentTimestamp() + ".log";

        System.out.println("Will execute the following refinement cycles:");
        for(RefinementRunConfig runConfig : runConfigs) {
            runConfig.initialize(toRefine, vc);
            runConfig.printPrerunSummary(false);
        }

        int layer = 1;
        ScoredLoadout best = vc.scoreLoadout(toRefine);
        double originalScore = best.score;

        for(RefinementRunConfig runConfig : runConfigs) {
            long layerStartTime = System.currentTimeMillis();
            System.out.println("Starting refinement layer " + layer + "...");

            count = 0;
            runConfig.printPrerunSummary(true);

            int cycles = 1;
            boolean improvedThisCycle;
            do {
                System.out.println("Starting refinement cycle " + cycles + " for layer " + layer + "...");

                improvedThisCycle = false;

                ScoredLoadout result = runRefinementCycle(toRefine, vc, runConfig);
                if(result.score > best.score) {
                    best = result;
                    improvedThisCycle = true;

                    String logContent = "\n\n" + DateTime.getCurrentTimestamp() + "\nImproved gear set found.";
                    logContent += "\n\n" + best.loadout;
                    logContent += vc.getScoreSummaryFor(best.loadout, originalScore, StatScorer.Verbosity.FULL);

                    System.out.println(logContent);
                    Writer.append(outputFile, logContent);
                }

                cycles++;
            } while(improvedThisCycle);

            long layerElapsedTime = System.currentTimeMillis() - layerStartTime;
            System.out.println("Completed layer " + layer + " in " + NumberFormat.readableLongTime(layerElapsedTime));
            System.out.println("Loadouts scored: " + NumberFormat.readableLargeNumber(count));
            System.out.println("Throughput: " + NumberFormat.readableLargeNumber(count * MillisTo.MINUTES / layerElapsedTime) + " loadouts/minute");

            layer++;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Completed refinement in " + NumberFormat.readableLongTime(elapsedTime));

        String logContent = "\n\n" + DateTime.getCurrentTimestamp() + "\nFinal refined set:";
        logContent += "\n\n" + best;
        logContent += vc.getScoreSummaryFor(best.loadout, originalScore, StatScorer.Verbosity.FULL);

        System.out.println(logContent);
        Writer.append(outputFile, logContent);
    }

    private static ScoredLoadout runRefinementCycle(EquipmentLoadout toRefine, ValuationContext vc, RefinementRunConfig runConfig) {
        Map<ItemSlot, RandomAccessScoredItemList> options = runConfig.getOptions();
        List<ItemSlot> slots = toRefine.toSlotList();
        int swaps = runConfig.slotSwaps;

        // Drop any equipped item slots for which no viable alternatives exist.
        for(int i = 0; i < slots.size(); i++)
            if(! options.containsKey(slots.get(i)))
                slots.remove(i--);

        return recursiveRefinementCycle(toRefine, vc, options, slots, swaps, 0);
    }

    private static long count = 0;
    private static ScoredLoadout recursiveRefinementCycle(EquipmentLoadout loadoutFromAbove, ValuationContext vc, Map<ItemSlot, RandomAccessScoredItemList> options, List<ItemSlot> slots, int swaps, int index) {
        ScoredLoadout best = new ScoredLoadout();
        if(slots.size() - index - 1 < swaps) return best;

        ItemSlot workingSlot = slots.get(index);
        RandomAccessScoredItemList itemOptions = options.get(workingSlot);

        if(swaps > 0) {
            for(Item item : itemOptions.getItems()) {
                EquipmentLoadout loadoutToBelow = new EquipmentLoadout(loadoutFromAbove);
                loadoutToBelow.put(workingSlot, item);

                ScoredLoadout result = recursiveRefinementCycle(loadoutToBelow, vc, options, slots, swaps - 1, index + 1);
                if(result.score > best.score) best = result;
            }

            ScoredLoadout result = recursiveRefinementCycle(loadoutFromAbove, vc, options, slots, swaps, index + 1);
            if(result.score > best.score) best = result;
        } else {
            ScoredLoadout result = vc.scoreLoadout(loadoutFromAbove);
            if(result.score > best.score) best = result;
            count++;
        }

        return best;
    }
}
