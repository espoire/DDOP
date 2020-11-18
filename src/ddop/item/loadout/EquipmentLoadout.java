package ddop.item.loadout;

import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.stat.Stat;
import ddop.stat.StatSource;
import ddop.stat.conversions.SetBonus;
import ddop.stat.list.VerboseStatList;
import util.Array;
import util.NumberFormat;
import util.StatTotals;

import java.util.*;

public class EquipmentLoadout implements Cloneable, StatSource {
	private HashMap<ItemSlot, ArrayList<Item>> items = new HashMap<>();
	
	public EquipmentLoadout() {}
	public EquipmentLoadout(Item[] items) { this.put(items); }
	public EquipmentLoadout(Iterable<Item> items) { this.put(items); }
	public EquipmentLoadout(EquipmentLoadout toCopy) {
		this.loadItems(toCopy);
	}
	
	public EquipmentLoadout put(String... strings) {
		ItemList items = ItemList.getAllNamedItems();

		for(String s : strings)
			this.put(items.getNamedItem(s));

		return this;
	}
	
	public EquipmentLoadout put(Item[] items) {
		for(Item i : items) this.put(i);
		return this;
	}
	public EquipmentLoadout put(Iterable<Item> items) {
		for(Item i : items) this.put(i);
		return this;
	}

	public EquipmentLoadout put(Item i) {
		if(i.slots.size() != 1)
			throw new RuntimeException("Attempted to add a multi-slot item to an EquipmentLoadout without specifying a slot: " + i.name);

		return this.put(i.slots.stream().findFirst().get(), i);
	}

	public EquipmentLoadout put(ItemSlot slot, String itemName) {
		return this.put(slot, ItemList.getAllNamedItems().getNamedItem(itemName));
	}

	public EquipmentLoadout put(ItemSlot slot, Item item) {
		if(item == null) return this;
		if(! item.slots.contains(slot))
			throw new RuntimeException("Attempted to equip item '" + item.name + "' to slot '" + slot.name + "'. It cannot equip to this slot.");

		ArrayList<Item> equippedInSlot = this.items.get(slot);
		if(slot.limit == 1) {
			if(equippedInSlot == null) {
				equippedInSlot = new ArrayList<>();
				equippedInSlot.add(item);

				this.items.put(slot, equippedInSlot);
			} else {
				equippedInSlot.clear();
				equippedInSlot.add(item);
			}
		} else {
			if(equippedInSlot == null) {
				equippedInSlot = new ArrayList<>();
				equippedInSlot.add(item);
				
				this.items.put(slot, equippedInSlot);
			} else if(!Array.contains(equippedInSlot, item)) {
				if(equippedInSlot.size() < slot.limit) {
					equippedInSlot.add(item);
				} else {
					equippedInSlot.add(0, item);
					equippedInSlot.remove(slot.limit);
				}
			}
		}
		
		return this;
	}
	
	public List<ItemSlot> getUnfilledSlots() {
		List<ItemSlot> ret = new ArrayList<>();
		
		for(ItemSlot slot : ItemSlot.getAll()) {
			int empty = slot.limit;
			if(this.items.containsKey(slot)) empty -= this.items.get(slot).size();
			for(; empty > 0; empty--) ret.add(slot);
		}
		
		return ret;
	}
	
	private VerboseStatList toStat() {
		return new VerboseStatList(this);
	}
	
	private Collection<Item> getItems() {
		Collection<Item> ret = new ArrayList<>();

		for(List<Item> items : this.items.values())
			ret.addAll(items);

		return ret;
	}
	
	public Collection<Stat> getStats() {
		Collection<Stat> ret = new ArrayList<>();
		for(Item i : this.getItems()) ret.addAll(i.getStats());
		return ret;
	}
	
	public void compareTo(EquipmentLoadout another) {
		this.compareStatTotalsWith(another);
	}
	
	private void compareStatTotalsWith(EquipmentLoadout another) {
		StatTotals mine = this.toStat().getStatTotals(),
				  thine = another.toStat().getStatTotals();

		System.out.println(this);
		System.out.println(another);

		System.out.println(this.getTagLine() + " has the following stats, relative to the second set: ");
		System.out.println(mine.subtract(thine));
		
		System.out.println(another.getTagLine() + " has the following stats, relative to the first set: ");
		System.out.println(thine.subtract(mine));
	}
	
	public void printItemNamesToConsole() {
		System.out.println(this.toString());
	}
	
	public void printStatTotalsToConsole() {
		System.out.println(this.toStat());
	}

	public List<Item> toItemList() {
		List<Item> ret = new ArrayList<>();

		for(List<Item> items : this.items.values())
			ret.addAll(items);

		return ret;
	}

	/** Returns a List of the filled ItemSlots. Will include duplicate entries if wearing 2 rings. */
	public List<ItemSlot> toSlotList() {
		List<ItemSlot> ret = new ArrayList<>();

		for(ItemSlot is : this.items.keySet())
			for(int i = 0; i < this.items.get(is).size(); i++)
				ret.add(is);

		return ret;
	}

	@Override
	public String toString() { return this.toString(null); }
	public String toString(Map<ItemSlot, RandomAccessScoredItemList> context) {
		StringBuilder ret = new StringBuilder(this.getTagLine() + ":\n");

		for(ItemSlot slot : this.items.keySet()) {
			List<Item> equippedInSlot = this.items.get(slot);
			if(equippedInSlot.size() == 0) continue;

			ret.append("| ").append(slot.name).append(": ");

			boolean firstOnLine = true;
			for(Item i : equippedInSlot) {
				if(firstOnLine) {
					firstOnLine = false;
				} else {
					ret.append(", ");
				}
				ret.append(i.name);
				if(context != null) {
					RandomAccessScoredItemList scores = context.get(slot);
					if(scores != null)
						ret.append(" - ").append(NumberFormat.percent(scores.getScore(i)));
				}
			}

			ret.append("\n");
		}

		for(ItemSlot slot : this.getUnfilledSlots()) {
			ret.append("| ").append(slot.name).append(": [EMPTY]\n");
		}

		ret.append("\nSet bonuses:\n");
		ret.append(SetBonus.listAttainment(this));
		ret.append("\n");

		return ret.toString();
	}
	
	protected String getTagLine() {
		return "EquipmentLoadout (" + this.size() + " items)";
	}
	
	public int size() {
		int count = 0;

		for(ArrayList<Item> equipped : this.items.values()) {
			count += equipped.size();
		}

		return count;
	}
	
	@Override
	public EquipmentLoadout clone() {
		EquipmentLoadout ret = null;
		try {
			ret = (EquipmentLoadout) super.clone();
			
			ret.items = (HashMap<ItemSlot, ArrayList<Item>>) ret.items.clone();
			
			for(ItemSlot slot : ret.items.keySet()) {
				ret.items.put(slot, (ArrayList<Item>) ret.items.get(slot).clone());
			}
		} catch (CloneNotSupportedException ignored) {}
		return ret;
	}

	/** Quickly replaces the items in this EquipmentLoadout with the items
	 *  in another. No error checking; it is assumed safe to copy the state
	 *  of an already-valid state. */
	public void loadItems(EquipmentLoadout another) {
		if(this == another)
			return;
//			throw new Error();

		for(ArrayList<Item> equippedInSlot : this.items.values())
			equippedInSlot.clear();

		for(Map.Entry<ItemSlot, ArrayList<Item>> entry : another.items.entrySet()) {
			if(entry.getValue().size() == 0) continue;

			ArrayList<Item> equippedInSlot = this.items.get(entry.getKey());

			if(equippedInSlot == null) {
				this.items.put(entry.getKey(), new ArrayList<>(entry.getValue()));
			} else {
				equippedInSlot.addAll(entry.getValue());
			}
		}
	}
}
