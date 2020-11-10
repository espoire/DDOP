package ddop.stat.list;

import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.stat.Stat;
import ddop.stat.StatSource;
import ddop.stat.conversions.NamedStat;
import ddop.stat.conversions.SetBonus;
import util.StatTotals;
import util.Vector2;

import java.util.*;

public class VerboseStatList extends AbstractStatList implements StatSource {
	private List<Stat> all;
	private List<Stat> best = null;
	private List<Stat> redundant = null;
	private StatTotals totals = null;

	public VerboseStatList(StatSource... sources) { super(sources); }

	@Override
	protected void init() {
		this.all = new ArrayList<>();
	}

	@Override
	public VerboseStatList add(Stat s) {
		if(s == null) return this;

		this.all.add(s);
		this.best = null;
		this.redundant = null;
		this.totals = null;
		
		return this;
	}

	private List<Stat> getBestList() {
		if(this.best == null) this.enforceStackingRules();
		return this.best;
	}
	
	private List<Stat> getRedundantList() {
		if(this.redundant == null) this.enforceStackingRules();
		return this.redundant;
	}

	@Override
	public StatTotals getStatTotals() {
		if(this.totals == null) {
			this.applyStatConversions();
			this.enforceStackingRules();
		}
		return this.totals;
	}
	
	private void applyStatConversions() {
		this.all = SetBonus.convertAll(this.all);
		this.all = NamedStat.convertAll(this.all);
	}
	
	private void enforceStackingRules() {
		Map<Vector2<String, String>, Stat> best = new HashMap<>();
		
		this.best = new ArrayList<>();
		this.redundant = new ArrayList<>();
		
		for(Stat s : this.all) {
			Vector2<String, String> categoryType = new Vector2<>(s.category, s.bonusType);
			if(best.containsKey(categoryType)) {
				if(s.stacks()) {
					double previousMagnitude = best.get(categoryType).magnitude;
					best.put(categoryType, new Stat(s.category, s.bonusType, s.magnitude + previousMagnitude));
				} else {
					double bestMagnitude = best.get(categoryType).magnitude;
					if(s.magnitude > bestMagnitude) {
						Stat oldBest = best.put(categoryType, s);
						this.redundant.add(oldBest);
					} else if(s.magnitude <= bestMagnitude) {
						this.redundant.add(s);
					}
				}
			} else {
				best.put(categoryType, s);
			}
		}
		
		this.best.addAll(best.values());
		
		this.generateStatTotals();
	}

	private void generateStatTotals() {
		assert this.best != null;
		
		this.totals = new StatTotals();
		
		for(Stat s : this.best) {
			Double magnitude = s.magnitude;
			if(this.totals.containsKey(s.category)) magnitude += this.totals.get(s.category);
			this.totals.put(s.category, magnitude);
		}
		
	}
	
	public static Set<String> getAllExistingStatsTypes() {
		VerboseStatList sl = new VerboseStatList((StatSource) ItemList.getAllNamedItems().filterBy());
		
		return sl.getStatTotals().keySet();
	}
	
	public void printSummary() {
		System.out.println(this.toString());
	}
	
	public String toString() { return this.toString(true); }
	public String toString(boolean verbose) {
		String ret = "StatList (" + this.all.size() + " stats):\n";
		
		ret += "+- Totals:\n";
		ret += this.getStatTotals();
		
		if(verbose) {
			ret += "|\n";
			ret += "+- Redundant:\n";
			ret += statListToStringBySource(this.getRedundantList());
			
			ret += "|\n";
			ret += "+- Using:\n";
			ret += this.getBestList();
			
			if(Settings.SHOW_FULL_STATLIST_DEBUG) {
				ret += "|\n";
				ret += "+- All:\n";
				ret += this.all;
			}
		}
		
		return ret;
	}

	private String statListToStringBySource(List<Stat> stats) {
		Map<String, List<Stat>> bySource = new HashMap<>();

		for(Stat s : stats) {
			String source = s.getSource();
			if (source == null) continue;

			if(!bySource.containsKey(source)) {
				List<Stat> blankList = new ArrayList<>();
				bySource.put(source, blankList);
			}

			bySource.get(source).add(s);
		}

		StringBuilder ret = new StringBuilder();
		for(String source : bySource.keySet()) {
			ret.append("|\n   +- ").append(source).append("\n");

			for(Stat s : bySource.get(source)) {
				ret.append("   | ").append(s).append("\n");
			}
		}
		ret.append("|\n");

		return ret.toString();
	}

	public static void printAllStats(boolean showItemName) {
		Set<String> stats = VerboseStatList.getAllExistingStatsTypes();
		
		for(String statName : stats) {
			String line = statName;
			if(showItemName) {
outer:			for(Item i : ItemList.getAllNamedItems().filterBy()) {
					for(Stat s : i.getStats()) {
						if(s.category.equals(statName)) {
							line += " - " + i.name;
							break outer;
						}
					}
				}
			}
			System.out.println(line);
		}
	}
	
	@Override @SuppressWarnings("unchecked")
	public Collection<Stat> getStats() {
		return (Collection<Stat>) ((ArrayList<Stat>) this.all).clone();
	}
}
