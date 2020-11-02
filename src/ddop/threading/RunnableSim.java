package ddop.threading;

import ddop.dto.SimResultContext;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.main.session.ExecutionSession;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.optimizer.ScoredLoadout;
import ddop.optimizer.valuation.StatScorer;
import util.Array;

import java.util.List;
import java.util.Map;

public class RunnableSim implements Runnable {
    private final StatScorer ss;
    private final List<Item> fixedItems;
    private final List<ItemSlot> skippedItemSlots;
    private final Map<ItemSlot, RandomAccessScoredItemList> itemMap;
    private final ExecutionSession session;
    public boolean isMasterThread = false;
    public SimResultContext result;

    public RunnableSim(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ExecutionSession session) {
        this.ss = ss;
        this.fixedItems = fixedItems;
        this.skippedItemSlots = skippedItemSlots;
        this.itemMap = itemMap;
        this.session = session;
    }

    @Override
    public void run() {
        this.result = RunnableSim.runSim(this.ss, this.fixedItems, this.skippedItemSlots, this.itemMap, this.session, this.isMasterThread);
    }

    private static SimResultContext runSim(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ExecutionSession session, boolean showConsoleLog) {
        ScoredLoadout best = new ScoredLoadout();
        long startTime = System.currentTimeMillis();

        int trialsCompleted = 0;
        long elapsedTime = 0;

        int percent = 0;
        double progress = 0.0;

        while(progress < 1.0) {
            for (int i = 0; i < 10000; i++) {
                ScoredLoadout trial = simLoadout(ss, fixedItems, skippedItemSlots, itemMap);
                if (trial.score > best.score) best = trial;
            }

            trialsCompleted += 10000;
            elapsedTime = System.currentTimeMillis() - startTime;
            progress = session.getCompletion(trialsCompleted, elapsedTime);

            if(showConsoleLog) percent = printProgressMessage(percent, progress);
        }

        return new SimResultContext(best, trialsCompleted, elapsedTime);
    }

    private static ScoredLoadout simLoadout(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        EquipmentLoadout trialLoadout = fillTrialEquipmentLoadout(fixedItems, skippedItemSlots, itemMap);
        return ScoredLoadout.score(trialLoadout, ss);
    }

    private static EquipmentLoadout fillTrialEquipmentLoadout(List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        EquipmentLoadout trialLoadout = new EquipmentLoadout(fixedItems);

        for(ItemSlot slot : itemMap.keySet()) {
            int limit = getNumberOfUnskippedSlots(skippedItemSlots, slot);
            for(int i = 0; i < limit; i++) {
                Item item = itemMap.get(slot).getRandom();
                trialLoadout.put(item);
            }
        }
        return trialLoadout;
    }

    private static int printProgressMessage(int previousPercentageComplete, double completeZeroToOne) {
        int percentageComplete = (int) (completeZeroToOne * 100);

        if(percentageComplete > previousPercentageComplete) {
            System.out.print(percentageComplete + "% ");
            if(percentageComplete % 10 == 0) System.out.println();
        }

        return percentageComplete;
    }

    private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
        int limit = slot.limit - Array.containsCount(skipSlots, slot);
        return limit;
    }
}
