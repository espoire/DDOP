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
import util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Implements a Simulated Annealing algorithm to search the possible equipment space.
 *
 * This algorithm is a loose analog to metallurgical annealing, the process of heating
 * a material to melt its crystal structure, and very slowly cooling it to produce a
 * single (or very few) large crystals.
 *
 * The system will begin by selecting a random equipment loadout, and iteratively trying
 * single-item alterations to the set. If the alteration results in a higher score, the
 * alteration will be accepted. If the alteration does not result in a higher score, it
 * has a probability of being accepted, based on the magnitude of the reduction and the
 * system's current "temperature". Temperature starts at a constant value and decreases
 * to zero as the remaining computation time depletes. The smaller the temperature, or
 * the larger the performance reduction, the less likely the system will be to accept
 * a reduction in equipment performance.
 *
 * This should hopefully result in the system behaving initially-randomly, before hill
 * climbing to a nearby local maximum on one of the highest peaks. */
public class RunnableAnnealingSim extends RunnableSim {
    private final StatScorer ss;
    private final List<Item> fixedItems;
    private final List<ItemSlot> unskippedItemSlots;
    private final Map<ItemSlot, RandomAccessScoredItemList> itemMap;

    private static final double STARTING_TEMPERATURE = 1.0;
    private double temperature;
    private ScoredLoadout current, best;

    public RunnableAnnealingSim(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ExecutionSession session) {
        super(session);

        this.ss = ss;
        this.fixedItems = fixedItems;
        this.itemMap = itemMap;
        this.unskippedItemSlots = generateUnskippedSlots(skippedItemSlots, itemMap);
    }

    @Override
    protected void initialize() {
        this.temperature = STARTING_TEMPERATURE;
        this.best = new ScoredLoadout();
        this.current = ScoredLoadout.score(new EquipmentLoadout(this.fixedItems), this.ss);

        super.initialize();
    }

    @Override
    protected void iterate() {
        this.simAndUpdateState(this.ss, this.unskippedItemSlots, this.itemMap);
    }

    @Override
    protected SimResultContext getResult() {
        return this.generateResultContext(best);
    }

    @Override
    protected void updateProgress() {
        super.updateProgress();
        double portion = (1 - this.getProgress());
        this.temperature = STARTING_TEMPERATURE * portion * portion;
    }

    private void simAndUpdateState(StatScorer ss, List<ItemSlot> unskippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        ScoredLoadout trial = simLoadout(ss, unskippedItemSlots, itemMap, current);

        double ratio = trial.score / this.current.score;
        double probability = Math.exp(- (1 - ratio) / this.temperature);
        if(ratio >= 1) probability = 1;

        if(Random.roll(probability)) this.current = trial;
        if(trial.score > this.best.score) this.best = trial;
    }

    private static ScoredLoadout simLoadout(StatScorer ss, List<ItemSlot> unskippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ScoredLoadout current) {
        EquipmentLoadout trialLoadout = permuteTrialEquipmentLoadout(unskippedItemSlots, itemMap, current);
        return ScoredLoadout.score(trialLoadout, ss);
    }

    private static EquipmentLoadout permuteTrialEquipmentLoadout(List<ItemSlot> unskippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap, ScoredLoadout current) {
        EquipmentLoadout trialLoadout = new EquipmentLoadout(current.loadout);

        ItemSlot slot = (ItemSlot) Random.random(unskippedItemSlots);
        Item item = itemMap.get(slot).getRandom();
        trialLoadout.put(item, slot);

        return trialLoadout;
    }

    private static List<ItemSlot> generateUnskippedSlots(List<ItemSlot> skippedItemSlots, Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        final List<ItemSlot> ret = new ArrayList<>();

        for(ItemSlot slot : itemMap.keySet()) {
            int limit = getNumberOfUnskippedSlots(skippedItemSlots, slot);
            for(int i = 0; i < limit; i++) ret.add(slot);
        }

        return ret;
    }

    private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
        return slot.limit - Array.containsCount(skipSlots, slot);
    }
}
