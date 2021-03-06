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
 * climbing to a nearby local maximum on one of the widest peaks. Actual performance
 * depends on wide peaks also being high peaks.
 * */
public class RunnableAnnealingSim extends RunnableSim {
    private final StatScorer ss;
    private final List<Item> fixedItems;
    private final List<ItemSlot> unskippedItemSlots;
    private final Map<ItemSlot, RandomAccessScoredItemList> itemMap;

    private static final double STARTING_TEMPERATURE = 1.0;
    private double temperature;
    private ScoredLoadout current, best;
    private EquipmentLoadout working;

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
        this.working = new EquipmentLoadout(this.fixedItems);

        super.initialize();
    }

    @Override
    protected void iterate() {
        this.simAndUpdateState();
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

    private void simAndUpdateState() {
        ScoredLoadout trial = this.simLoadout();

        double ratio = trial.score / this.current.score;
        double probability = Math.exp(- (1 - ratio) / this.temperature);

        if(Random.roll(probability))
            this.working = this.current.swap(trial); // Swap to avoid memory reallocation.

        if(trial.score > this.best.score) this.best = trial;
    }

    private ScoredLoadout simLoadout() {
        mutateCurrentEquipmentLoadout();
        return ScoredLoadout.score(this.working, this.ss);
    }

    private void mutateCurrentEquipmentLoadout() {
        this.working.loadItems(current.loadout);

        ItemSlot slot = (ItemSlot) Random.random(unskippedItemSlots);
        Item item = itemMap.get(slot).getRandomUnweighted();
        this.working.put(slot, item);
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
