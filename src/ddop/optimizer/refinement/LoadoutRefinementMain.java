package ddop.optimizer.refinement;

import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.PrunedItemMapUtil;
import ddop.item.loadout.EquipmentLoadout;
import ddop.item.loadout.StoredLoadouts;
import ddop.optimizer.scoring.scored.RandomAccessScoredItemList;
import ddop.optimizer.scoring.scored.ScoredLoadout;
import ddop.optimizer.scoring.scorers.ShintaoScorer;
import ddop.optimizer.scoring.scorers.StatScorer;
import ddop.optimizer.scoring.scorers.ValuationContext;
import file.Writer;
import org.jetbrains.annotations.NotNull;
import util.DateTime;
import util.MillisTo;
import util.NumberFormat;

import java.util.List;
import java.util.Map;

public class LoadoutRefinementMain {
    public static void main(String... s) {
        StatScorer scorer = ShintaoScorer.create(30).r(8);
        EquipmentLoadout toRefine = StoredLoadouts.getShintaoSoulSplitterGear();
        ValuationContext vc = new ValuationContext(scorer, toRefine);

        runFullRefinementOn(toRefine, vc);
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

        for(RefinementRunConfig runConfig : runConfigs)
            runConfig.initialize(toRefine, vc);
        printRefinementStartLog(runConfigs);

        int layer = 1;
        ScoredLoadout best = vc.scoreLoadout(toRefine);
        double originalScore = best.score;

        for(RefinementRunConfig runConfig : runConfigs) {
            long layerStartTime = System.currentTimeMillis();

            printStartingLayerLog(layer);
            runConfig.printPrerunSummary(true);

            loadoutsThisLayer = 0;
            int cycle = 1;
            boolean improvedThisCycle;

            do {
                improvedThisCycle = false;
                printStartingCycleLog(cycle, layer);

                ScoredLoadout result = runRefinementCycle(toRefine, vc, runConfig);
                if(result.score > best.score) {
                    best = result;
                    improvedThisCycle = true;
                    writeCycleCompletionLog(outputFile, best, originalScore, vc);
                }

                cycle++;
            } while(improvedThisCycle);

            printLayerCompletionLog(layer++, layerStartTime);
        }

        writeRefinementCompletionLog(outputFile, startTime, best, originalScore, vc);
    }

    private static void printRefinementStartLog(RefinementRunConfig[] runConfigs) {
        System.out.println("Will execute the following refinement cycles:");

        for(RefinementRunConfig runConfig : runConfigs)
            runConfig.printPrerunSummary(false);
    }

    private static void printStartingLayerLog(int layer) {
        System.out.println("Starting refinement layer " + layer + "...");
    }

    private static void printLayerCompletionLog(int layer, long layerStartTime) {
        long layerElapsedTime = System.currentTimeMillis() - layerStartTime;
        System.out.println("Completed layer " + layer + " in " + NumberFormat.readableLongTime(layerElapsedTime));
        System.out.println("Loadouts scored: " + NumberFormat.readableLargeNumber(loadoutsThisLayer));
        System.out.println("Throughput: " + NumberFormat.readableLargeNumber(loadoutsThisLayer * MillisTo.MINUTES / layerElapsedTime) + " loadouts/minute");
    }

    private static void printStartingCycleLog(int cycles, int layer) {
        System.out.println("Starting refinement cycle " + cycles + " for layer " + layer + "...");
    }

    private static void writeCycleCompletionLog(String outputFile, ScoredLoadout best, double originalScore, ValuationContext vc) {
        String logContent = "\n\n" + DateTime.getCurrentTimestamp() + "\nImproved gear set found.";
        logContent += "\n\n" + best.loadout;
        logContent += vc.getScoreSummaryFor(best.loadout, originalScore, StatScorer.Verbosity.FULL);

        System.out.println(logContent);
        Writer.append(outputFile, logContent);
    }

    private static void writeRefinementCompletionLog(String outputFile, long startTime, ScoredLoadout best, double originalScore, ValuationContext vc) {
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

    private static long loadoutsThisLayer = 0;
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
                best = betterOf(best, result);
            }

            ScoredLoadout result = recursiveRefinementCycle(loadoutFromAbove, vc, options, slots, swaps, index + 1);
            best = betterOf(best, result);
        } else {
            ScoredLoadout result = vc.scoreLoadout(loadoutFromAbove);
            best = betterOf(best, result);
            loadoutsThisLayer++;
        }

        return best;
    }

    @NotNull
    private static ScoredLoadout betterOf(ScoredLoadout first, ScoredLoadout second) {
        if (first.score < second.score) return second;
        return first;
    }
}
