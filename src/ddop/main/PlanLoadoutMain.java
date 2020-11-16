package ddop.main;

import ddop.constants.Time;
import ddop.dto.LevelRange;
import ddop.dto.SimResultContext;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.item.loadout.StoredLoadouts;
import ddop.item.sources.crafted.SlaversCraftedItemSource;
import ddop.main.session.DurationSession;
import ddop.main.session.ExecutionSession;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.optimizer.ScoredItemList;
import ddop.optimizer.ScoredLoadout;
import ddop.optimizer.valuation.ShintaoScorer;
import ddop.optimizer.valuation.StatScorer;
import ddop.optimizer.valuation.ValuationContext;
import ddop.threading.RunnableAnnealingSim;
import ddop.threading.RunnableSim;
import util.Array;

import java.util.*;

@SuppressWarnings("unused")
public class PlanLoadoutMain {
	private static final int MILLION = 1000000;

	// Recommended duration: 50ms per candidate item.
	private static final ExecutionSession EXECUTION_LENGTH =
//			new DurationSession(15 * Time.SECOND);
			new DurationSession(3 * Time.MINUTE);
//			new DurationSession(1 * Time.HOUR);
//			new DurationSession(6 * Time.HOUR);

	private static final boolean MULTI_THREAD = true;
	private static final int THREADS =
			Runtime.getRuntime().availableProcessors() -1;
//			2;
	
	private static final double ITEM_QUALITY_MINIMUM_RATIO = 0.15;

	private static final int TARGET_ITEMS_MIN_LEVEL = 26,
							 TARGET_ITEMS_MAX_LEVEL = 30;
	private static final LevelRange TARGET_ITEMS_LEVEL_RANGE = new LevelRange(TARGET_ITEMS_MIN_LEVEL, TARGET_ITEMS_MAX_LEVEL);

	private static final ItemSlot[] IGNORED_SLOTS = new ItemSlot[] {
			ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND,
			ItemSlot.QUIVER
	};
	
	public static void main(String... s) {
		StatScorer scorer = ShintaoScorer.create(30).r(8);

//		((SpecScorer) scorer).printReaperBuildDebug();

		EquipmentLoadout currentGear =
				StoredLoadouts.getShintaoSoulSplitterGear();
//				StoredLoadouts.getHealbardNoSetGear();
//				StoredLoadouts.getHealbardCandidateGear();
//				null;

		scorer.showVerboseScoreFor(currentGear);
		double previousGearSetScore = scorer.score(currentGear);

		simLoadouts(scorer, previousGearSetScore);

//		((SpecScorer) scorer).printReaperBuildDebug();
	}
	
	private static void simLoadouts(StatScorer ss, double baselineScore) {
		List<Item>           fixedItems = getFixedItems().toItemList();
		List<ItemSlot> skippedItemSlots = getSkipItemSlotList();
		
		ScoredLoadout best = simBestLoadout(ss, fixedItems, skippedItemSlots);
		
		System.out.println(best);
		ss.showVerboseScoreFor(best.loadout, baselineScore);
	}
	
	private static ScoredLoadout simBestLoadout(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots) {
        ValuationContext vc = new ValuationContext(ss, new EquipmentLoadout(fixedItems));
		Map<ItemSlot, RandomAccessScoredItemList> itemMap = getItemSlotScoredItemListMap(vc, skippedItemSlots);

		int numThreads = (MULTI_THREAD ? THREADS : 1);
		ExecutionSession session = EXECUTION_LENGTH.splitToThreads(numThreads);
		
		printSimStartMessage(itemMap);

		Thread[] threads = new Thread[numThreads];
		RunnableSim[] sims = new RunnableSim[numThreads];
		for(int i = 0; i < numThreads; i++) {
			sims[i] = new RunnableAnnealingSim(ss, fixedItems, skippedItemSlots, itemMap, session);
			if(i == 0) sims[i].makeMasterThread();

			threads[i] = new Thread(sims[i]);
			threads[i].start();
		}

		SimResultContext result = awaitResult(threads, sims);
		result.best.annotate(itemMap);

		result.printSimCompleteMessage();
		return result.best;
	}

	private static SimResultContext awaitResult(Thread[] threads, RunnableSim[] sims) {
		SimResultContext[] results = new SimResultContext[threads.length];
		for(int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch(Exception e) {
				e.printStackTrace();
			}

			results[i] = sims[i].result;
		}

		return SimResultContext.merge(results);
	}

	private static void printSimStartMessage(Map<ItemSlot, RandomAccessScoredItemList> itemMap) {
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

		System.out.println("Beginning loadout sim.");
		EXECUTION_LENGTH.printSimStartMessage(totalItemsConsidered, itemMap.size(), totalCombinations);

		System.out.println();
	}
	
	private static List<ItemSlot> getSkipItemSlotList() {
		List<ItemSlot> ret = new ArrayList<>();
		Collection<ItemSlot> fixedItems = getFixedItems().toSlotList();

		Collections.addAll(ret, IGNORED_SLOTS);
		ret.addAll(fixedItems);

		return ret;
	}
	
	private static Map<ItemSlot, RandomAccessScoredItemList> getItemSlotScoredItemListMap (ValuationContext vc, List<ItemSlot> skipSlots) {
		Map<ItemSlot, RandomAccessScoredItemList> ret = new HashMap<>();
		Set<ItemSlot> includedItemSlots = ItemSlot.getUnskippedSlots(skipSlots);

		ItemList candidates = ItemList.getAllNamedItems()
				.merge(SlaversCraftedItemSource.generateList(TARGET_ITEMS_LEVEL_RANGE, vc.getQueriedStatCategories()))
//				.merge(CannithCraftedItemSource.generateList(TARGET_ITEMS_LEVEL_RANGE, ItemSlot.getUnskippedSlots(skipSlots), vc.getAllowedArmorTypes(), vc.getQueriedStatCategories()))
				.filterByLevel(TARGET_ITEMS_LEVEL_RANGE)
				.filterByAllowedArmorTypes(vc.getAllowedArmorTypes());
		Map<ItemSlot, ItemList> rawItemMap = candidates.mapBySlot(includedItemSlots);

		for(ItemSlot slot : rawItemMap.keySet()) {
			int limit = getNumberOfUnskippedSlots(skipSlots, slot);
			if(limit > 0) {
				ScoredItemList options = new ScoredItemList(rawItemMap.get(slot), vc).trim(ITEM_QUALITY_MINIMUM_RATIO);
				options.stripUnusedStats(vc.getQueriedStatCategories());
				ret.put(slot, new RandomAccessScoredItemList(options));
			}
		}
		
		return ret;
	}

	public static EquipmentLoadout getFixedItems() {
		EquipmentLoadout ret = new EquipmentLoadout();

		ret.put("quiver of alacrity");

		ret.put("staggershockers");
		ret.put("silver dragonscale capelet ([insightful dexterity +10] version)");
		ret.put("signet of the solstice (lamannia - feywild raid)");
		ret.put("legendary slave rags");


//		ret.put("legendary omniscience");
//		ret.put("legendary tumbleweed");

//		ret.put("patience through peril");
//		ret.put("the invisible cloak of strahd");
//		ret.put("legendary braided cutcord");
//		ret.put("staggershockers");

//		ret.put("legendary turncoat");
//		ret.put("legendary family recruit sigil");
//		ret.put("legendary hammerfist");

//		ret.put("the cornerstone champion ([quality wisdom +5] version)");


//		ret.put("legendary moonrise bracers");
//		ret.put("legendary collective sight ([wisdom +21, insightful constitution +10] version)");
//		ret.put("doctor leroux's curious implant");

//		ret.put("visions of precision");
		
		return ret;
	}
	
	private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
		int limit = slot.limit - Array.containsCount(skipSlots, slot);
		return limit;
	}
}