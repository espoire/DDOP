package ddop.main;

import ddop.constants.Time;
import ddop.dto.SimResultContext;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.item.loadout.EquipmentLoadout;
import ddop.item.loadout.StoredLoadouts;
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

	private static final ExecutionSession EXECUTION_LENGTH =
			new DurationSession((long) (15 * Time.SECOND));
//			new DurationSession((long) (15 * Time.MINUTE));
//			new DurationSession((long) (1 * Time.HOUR));
//			new DurationSession((long) (8 * Time.HOUR));

	private static final boolean MULTI_THREAD = true;
	private static final int THREADS =
			Runtime.getRuntime().availableProcessors() -1;
//			2;
	
	private static final double ITEM_QUALITY_MINIMUM_RATIO = 0.05;

	private static final int TARGET_ITEMS_MIN_LEVEL = 26,
							 TARGET_ITEMS_MAX_LEVEL = 30;

	private static final ItemSlot[] IGNORED_SLOTS = new ItemSlot[] {
			ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND,
			ItemSlot.QUIVER
	};
	
	private static final String[] FIXED_ITEM_NAMES = new String[] {
//			"silverthread belt",
//			"epic voice of the master",
//
////			"blessed vestments",
////			"hallowed trail",
////			"hallowed castigators",
//
//			"countenance",
////			"bracers of the eagle",
//
//			"epic holistic stave",
//			"epic sanctuary",
//			"sunken slippers qcha version",
////			"sightless",
//			"circle of malevolence",
////			"shroud of ardent",
//			"cc goggles enchantment ienchantment ispellpen ml23 version",
////			"cc ring soniclore resonance icha ml26 version",
//			"cc gloves perform heal iperform ml25 version",
//
////			"drow outrunner armor",
////			"epic marguerite's necklace",

		"quiver of alacrity",
//		"legendary turncoat",
//		"legendary family recruit sigil",
//		"legendary hammerfist",
			"legendary omniscience",
			"legendary tumbleweed",

//		"radiant ring of taer valaestas",
//
//		"legendary celestial ruby ring dex version",
//		"legendary collective sight con iwis version",
//		"the cornerstone champion qwis version",
//		"legendary umber brim",
//		"silver dragonscale capelet",

//		"legendary moonrise bracers",
	};
	
	public static void main(String... s) {
		StatScorer scorer = new ShintaoScorer(30).r(8);
		EquipmentLoadout currentGear =
				StoredLoadouts.getShintaoSoulSplitterGear();
//				StoredLoadouts.getHealbardNoSetGear();
//				StoredLoadouts.getHealbardCandidateGear();
//				null;

		scorer.showVerboseScoreFor(currentGear);
		double previousGearSetScore = scorer.score(currentGear);

		simLoadouts(scorer, previousGearSetScore);
	}
	
	private static void simLoadouts(StatScorer ss, double baselineScore) {
		ArrayList<Item>            fixedItems = getFixedItems();
		List<ItemSlot>       skippedItemSlots = getSkipItemSlotList();
		
		ScoredLoadout best = simBestLoadout(ss, fixedItems, skippedItemSlots);
		
		System.out.println(best.loadout);
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
		int totalItemsConsidered = 0;
		double totalCombinations = 1;
		for(RandomAccessScoredItemList slotOptions : itemMap.values()) {
			int options = slotOptions.size();
			totalItemsConsidered += options;
			if(options > 0) totalCombinations *= options;
		}

		System.out.println("Beginning loadout sim.");
		EXECUTION_LENGTH.printSimStartMessage(totalItemsConsidered, totalCombinations);

		System.out.println();
	}
	
	private static List<ItemSlot> getSkipItemSlotList() {
		ArrayList<Item> fixedItems = getFixedItems();
		List<ItemSlot> skipSlots = new ArrayList<>();
		Collections.addAll(skipSlots, IGNORED_SLOTS);
		for(Item i : fixedItems) skipSlots.add(i.slot);
		return skipSlots;
	}
	
	private static Map<ItemSlot, RandomAccessScoredItemList> getItemSlotScoredItemListMap (ValuationContext vc, List<ItemSlot> skipSlots) {
		ItemList candidates = ItemList.getAllNamedItems()
				.filterByLevel(TARGET_ITEMS_MIN_LEVEL, TARGET_ITEMS_MAX_LEVEL)
				.filterBy(vc.getAllowedArmorTypes());
		Map<ItemSlot, ItemList> rawItemMap = candidates.mapBySlot();

		Map<ItemSlot, RandomAccessScoredItemList> ret = new HashMap<>();

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

	public static ArrayList<Item> getFixedItems() {
		ArrayList<Item> fixedItems = new ArrayList<>();

		ItemList items = ItemList.getAllNamedItems();
		for(String name : FIXED_ITEM_NAMES)
			fixedItems.add(items.getNamedItem(name));
		
		return fixedItems;
	}
	
	private static int getNumberOfUnskippedSlots(List<ItemSlot> skipSlots, ItemSlot slot) {
		int limit = slot.limit - Array.containsCount(skipSlots, slot);
		return limit;
	}
}