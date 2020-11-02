package ddop.optimizer;

import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.optimizer.valuation.ArmorType;
import ddop.optimizer.valuation.StatScorer;
import ddop.optimizer.valuation.ValuationContext;
import util.Array;
import util.Pair;

import java.util.*;

public class LoadoutOptimizer {
    private StatScorer scorer;
    private int minGearLevel = 24, maxGearLevel = -1;
    private ArmorType requiredArmorType;
    
    public LoadoutOptimizer(StatScorer scorer) {
        this.scorer = scorer;
    }
    
    public LoadoutOptimizer setItemLevelRange(int min, int max) {
        this.minGearLevel = min;
        this.maxGearLevel = max;
        return this;
    }
    
    public LoadoutOptimizer setRequiredArmorType(ArmorType at) {
        this.requiredArmorType = at;
        return this;
    }
    
    
    
    
    public ScoredLoadout planOptimalLoadout() {
        EquipmentLoadout planSoFar = new EquipmentLoadout();
        
        // TODO Pick an artifact and a set bonus to add first, rather than using the fixed options below
        
        planSoFar.put("Doctor LeRoux's Curious Implant",
                      "Legendary Blessed Vestments",
                      "Legendary Hallowed Trail",
                      "Legendary Hallowed Castigators",
                      "Epic Dynamistic Quiver");
        
        Map<ItemSlot, RandomAccessScoredItemList> candidateItems =
                ItemList.getAllNamedItems()
                        .filterByLevel    (this.minGearLevel, this.maxGearLevel)
                        .filterBy         (this.requiredArmorType)
                        .toScoredMapBySlot(new ValuationContext(this.scorer, planSoFar));
        
        Collection<ItemSlot> candidateSlotsToSelectNext = filterIgnoredSlots(planSoFar.getUnfilledSlots());
        
        while(candidateSlotsToSelectNext.size() > 2) {
            Map<ItemSlot, Pair<Item, ScoredLoadout>> bestBySlotPickOrder = new HashMap<>();
            for(ItemSlot slot : removeDuplicates(candidateSlotsToSelectNext)) {
                bestBySlotPickOrder.put(slot, new Pair<>(candidateItems.get(slot).getBest().getItem(), null));
            }
    
            while(bestBySlotPickOrder.size() > 1) {
                for(ItemSlot slot : bestBySlotPickOrder.keySet()) {
                    Pair<Item, ScoredLoadout> pair = bestBySlotPickOrder.get(slot);
            
                    Item bestInSlot = pair.getKey();
                    EquipmentLoadout planWithAddition = new EquipmentLoadout(planSoFar).put(bestInSlot);
            
                    Map<ItemSlot, RandomAccessScoredItemList> itemMap = this.rescore(candidateItems, planWithAddition);
                    Collection<ItemSlot> slotsToFill = new ArrayList<>(candidateSlotsToSelectNext);
                    slotsToFill.remove(slot);
            
                    ScoredLoadout best = simBestLoadout(planWithAddition, itemMap, slotsToFill, 20000);
            
                    System.out.print(slot.name + " ");
                    if(pair.getValue() == null || best.score > pair.getValue().score) {
                        bestBySlotPickOrder.put(slot, new Pair<>(bestInSlot, best));
                    }
                }
                System.out.println();
        
                removeWorstHalf(bestBySlotPickOrder);
            }
            
            Item best = null;
            for(Pair<Item, ScoredLoadout> pair : bestBySlotPickOrder.values()) best = pair.getKey();
            
            planSoFar.put(best);
    
            candidateItems = this.rescore(candidateItems, planSoFar);
            
            candidateSlotsToSelectNext = filterIgnoredSlots(planSoFar.getUnfilledSlots());
        }
        
        return ScoredLoadout.score(planSoFar, this.scorer);
    }
    
    private ScoredLoadout simBestLoadout(EquipmentLoadout planWithAddition, Map<ItemSlot, RandomAccessScoredItemList> itemMap, Collection<ItemSlot> slotsToFill, int trials) {
        ScoredLoadout best = null;
        for(int i = 0; i < trials; i++) {
            ScoredLoadout trial = simLoadout(planWithAddition, slotsToFill, itemMap);
            if(best == null || trial.score > best.score) best = trial;
        }
        return best;
    }
    
    private static void removeWorstHalf(Map<ItemSlot, Pair<Item, ScoredLoadout>> map) {
        int toRemove = map.size() / 2;
        for(int i = 0; i < toRemove; i++) removeWorst(map);
    }
    
    private static void removeWorst(Map<ItemSlot, Pair<Item, ScoredLoadout>> map) {
        ItemSlot worstKey = null;
        double worstScore = Double.MAX_VALUE;
        
        for(ItemSlot slot : map.keySet()) {
            double score = map.get(slot).getValue().score;
            if(score < worstScore) {
                worstScore = score;
                worstKey = slot;
            }
        }
        
        assert worstKey != null;
        map.remove(worstKey);
    }
    
    private ScoredLoadout simLoadout(EquipmentLoadout planSoFar, Collection<ItemSlot> slotsToFill, Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        EquipmentLoadout trialLoadout = fillTrialEquipmentLoadout(planSoFar, slotsToFill, itemMap);
        return ScoredLoadout.score(trialLoadout, this.scorer);
    }
    
    private static EquipmentLoadout fillTrialEquipmentLoadout(EquipmentLoadout planSoFar, Collection<ItemSlot> slotsToFill, Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
        EquipmentLoadout trialLoadout = new EquipmentLoadout(planSoFar);
        
        for(ItemSlot slot : itemMap.keySet()) {
            Item item = itemMap.get(slot).getRandom();
            trialLoadout.put(item);
        }
        
        return trialLoadout;
    }
    
    private Map<ItemSlot, RandomAccessScoredItemList> rescore(Map<ItemSlot, RandomAccessScoredItemList> candidateItems, EquipmentLoadout planWithAddition) {
        Map<ItemSlot, RandomAccessScoredItemList> ret = new HashMap<ItemSlot, RandomAccessScoredItemList>(candidateItems);
        ret.replaceAll((k, v) -> v.rescore(new ValuationContext(this.scorer, planWithAddition)));
        return ret;
    }
    
    private static final ItemSlot[] FILTERED_SLOTS = new ItemSlot[] { ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND };
    private static Collection<ItemSlot> filterIgnoredSlots(Collection<ItemSlot> slots) {
        List<ItemSlot> ret = new ArrayList<>();
        List<ItemSlot> toFilter = new ArrayList<>();
        Collections.addAll(toFilter, FILTERED_SLOTS);
        
        for(ItemSlot slot : slots) {
            if(Array.contains(toFilter, slot)) {
                toFilter.remove(slot);
            } else {
                ret.add(slot);
            }
        }
        
        return ret;
    }
    
    private static Collection<ItemSlot> removeDuplicates(Collection<ItemSlot> slots) {
        return new HashSet<>(slots);
    }
}
