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

    private int trialsCompleted = 0, percent = 0;
    private long startTime, elapsedTime = 0;
    private double progress = 0.0;

    public RunnableSim(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ExecutionSession session) {
        this.ss = ss;
        this.fixedItems = fixedItems;
        this.skippedItemSlots = skippedItemSlots;
        this.itemMap = itemMap;
        this.session = session;
    }

    @Override
    public void run() {
        ScoredLoadout best = new ScoredLoadout();
        this.startTime = System.currentTimeMillis();

        while(progress < 1.0) {
            best = simAndSaveBest(this.ss, this.fixedItems, this.skippedItemSlots, this.itemMap, best);
            this.progress = this.updateProgress(this.session);

            if(this.isMasterThread) this.printProgressMessage();
        }

        this.result = new SimResultContext(best, this.trialsCompleted, this.elapsedTime);
    }

    private double updateProgress(ExecutionSession session) {
        this.trialsCompleted++;
        this.elapsedTime = System.currentTimeMillis() - this.startTime;

        return session.getCompletion(this.trialsCompleted, this.elapsedTime);
    }

    private static ScoredLoadout simAndSaveBest(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ScoredLoadout best) {
        ScoredLoadout trial = simLoadout(ss, fixedItems, skippedItemSlots, itemMap);
        if (trial.score > best.score) best = trial;

        return best;
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

    private void printProgressMessage() {
        int percentageComplete = (int) (this.progress * 100);

        if(percentageComplete > this.percent) {
            System.out.print(percentageComplete + "% ");
            if(percentageComplete / 10 > this.percent / 10) System.out.println();
        }

        this.percent = percentageComplete;
    }

    private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
        return slot.limit - Array.containsCount(skipSlots, slot);
    }
}
