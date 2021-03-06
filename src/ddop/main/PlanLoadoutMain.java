package ddop.main;

import ddop.Settings;
import ddop.constants.Time;
import ddop.dto.SimResultContext;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.PrunedItemMapUtil;
import ddop.item.loadout.EquipmentLoadout;
import ddop.item.loadout.StoredLoadouts;
import ddop.dto.session.DurationSession;
import ddop.dto.session.ExecutionSession;
import ddop.optimizer.scoring.scored.RandomAccessScoredItemList;
import ddop.optimizer.scoring.scored.ScoredLoadout;
import ddop.optimizer.refinement.LoadoutRefinementMain;
import ddop.optimizer.scoring.scorers.ShintaoScorer;
import ddop.optimizer.scoring.scorers.StatScorer;
import ddop.optimizer.scoring.scorers.ValuationContext;
import ddop.threading.RunnableAnnealingSim;
import ddop.threading.RunnableSim;

import java.util.*;

@SuppressWarnings("unused")
public class PlanLoadoutMain {
	private static final int MILLION = 1000000;

	private static final ExecutionSession EXECUTION_LENGTH =
//			new DurationSession(15 * Time.SECOND);
			new DurationSession(15 * Time.MINUTE);
//			new DurationSession(1 * Time.HOUR);
//			new DurationSession(6 * Time.HOUR);

	private static final double ITEM_QUALITY_MINIMUM_RATIO = 0.25;

	private static final boolean MULTI_THREAD = true;
	private static final int THREADS =
			Runtime.getRuntime().availableProcessors() -1;
//			2;

	public static void main(String... s) {
		StatScorer scorer = ShintaoScorer.create(30).r(8);

		EquipmentLoadout currentGear =
				StoredLoadouts.getShintaoFeywildGear();

		System.out.println(currentGear);
		scorer.showVerboseScoreFor(currentGear);

		double previousGearSetScore = scorer.score(currentGear).getKey();

		simLoadouts(scorer, previousGearSetScore);
	}
	
	private static void simLoadouts(StatScorer ss, double baselineScore) {
		List<Item>           fixedItems = getFixedItems().toItemList();
		List<ItemSlot> skippedItemSlots = getSkipItemSlotList();
		
		ScoredLoadout best = simBestLoadout(ss, fixedItems, skippedItemSlots);
		
		System.out.println(best);
		ss.showVerboseScoreFor(best.loadout, baselineScore);

		LoadoutRefinementMain.runQuickRefinementOn(best.loadout, new ValuationContext(ss, new EquipmentLoadout(fixedItems)));
	}
	
	private static ScoredLoadout simBestLoadout(StatScorer ss, List<Item> fixedItems, List<ItemSlot> skippedItemSlots) {
        ValuationContext vc = new ValuationContext(ss, new EquipmentLoadout(fixedItems));
		Map<ItemSlot, RandomAccessScoredItemList> itemMap = PrunedItemMapUtil.generate(vc, skippedItemSlots, ITEM_QUALITY_MINIMUM_RATIO, PrunedItemMapUtil.IncludeSources.WIKI_SLAVERS);

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
		PrunedItemMapUtil.printOptionsList(itemMap);

		System.out.println("Beginning loadout sim.");
		EXECUTION_LENGTH.printSimStartMessage();

		System.out.println();
	}
	
	private static List<ItemSlot> getSkipItemSlotList() {
		List<ItemSlot> ret = new ArrayList<>();
		Collection<ItemSlot> fixedItems = getFixedItems().toSlotList();

		Collections.addAll(ret, Settings.IGNORED_SLOTS);
		ret.addAll(fixedItems);

		return ret;
	}

	public static EquipmentLoadout getFixedItems() {
		EquipmentLoadout ret = new EquipmentLoadout();

		ret.put("quiver of alacrity");

//		ret.put("legendary omniscience");
//		ret.put("legendary tumbleweed");

//		ret.put("patience through peril");
//		ret.put("the invisible cloak of strahd");
//		ret.put("legendary braided cutcord");
//		ret.put("staggershockers");

		ret.put("legendary turncoat");
		ret.put("legendary family recruit sigil");
		ret.put("legendary hammerfist");

//		ret.put("the cornerstone champion ([quality wisdom +5] version)");


//		ret.put("legendary moonrise bracers");
//		ret.put("legendary collective sight ([wisdom +21, insightful constitution +10] version)");
//		ret.put("doctor leroux's curious implant");

//		ret.put("visions of precision");
		
		return ret;
	}
}