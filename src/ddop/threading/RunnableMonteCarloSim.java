package ddop.threading;

import ddop.dto.SimResultContext;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.dto.session.ExecutionSession;
import ddop.optimizer.scoring.scored.RandomAccessScoredItemList;
import ddop.optimizer.scoring.scored.ScoredLoadout;
import ddop.optimizer.scoring.scorers.StatScorer;
import util.Array;

import java.util.List;
import java.util.Map;

public class RunnableMonteCarloSim extends RunnableSim {
    private final StatScorer ss;
    private final List<Item> fixedItems;
    private final List<ItemSlot> skippedItemSlots;
    private final Map<ItemSlot, RandomAccessScoredItemList> itemMap;

    private ScoredLoadout best = new ScoredLoadout();

    public RunnableMonteCarloSim(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ExecutionSession session) {
        super(session);

        this.ss = ss;
        this.fixedItems = fixedItems;
        this.skippedItemSlots = skippedItemSlots;
        this.itemMap = itemMap;
    }

    @Override
    protected void iterate() {
        ScoredLoadout trial = simLoadout();
        if (trial.score > this.best.score) this.best = trial;
    }

    @Override
    protected SimResultContext getResult() {
        return this.generateResultContext(this.best);
    }

    private ScoredLoadout simLoadout() {
        EquipmentLoadout trialLoadout = fillTrialEquipmentLoadout(this.fixedItems, this.skippedItemSlots, this.itemMap);
        return ScoredLoadout.score(trialLoadout, this.ss);
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

    private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
        return slot.limit - Array.containsCount(skipSlots, slot);
    }
}
