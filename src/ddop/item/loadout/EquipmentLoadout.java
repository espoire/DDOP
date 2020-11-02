package ddop.item.loadout;

import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.ItemSlot;
import ddop.stat.Stat;
import ddop.stat.list.VerboseStatList;
import ddop.stat.StatSource;
import util.Array;
import util.StatTotals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EquipmentLoadout implements Cloneable, StatSource {
	private HashMap<ItemSlot, ArrayList<Item>> items = new HashMap<>();
	
	public EquipmentLoadout() {}
	public EquipmentLoadout(Item[] items) { this.put(items); }
	public EquipmentLoadout(Iterable<Item> items) { this.put(items); }
	public EquipmentLoadout(EquipmentLoadout toCopy) {
		this.items = new HashMap<>(toCopy.items);
		this.items.replaceAll((k, v) -> new ArrayList<>(v));
	}
	
	public EquipmentLoadout put(String... strings) {
		for(String s : strings) this.put(ItemList.getNamedItem(s));
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
		if(i == null) return this;
		
		ItemSlot slot = i.slot;
		
		if(slot.limit == 1) {
			ArrayList<Item> value = new ArrayList<>();
			value.add(i);
			
			this.items.put(slot, value);
		} else {
			ArrayList<Item> value = this.items.get(slot);
			if(value == null) {
				value = new ArrayList<>();
				value.add(i);
				
				this.items.put(slot, value);
			} else if(!Array.contains(value, i)) {
				if(value.size() < slot.limit) {
					value.add(i);
				} else {
					value.add(0, i);
					value.remove(slot.limit);
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
		for(ArrayList<Item> items : this.items.values()) ret.addAll(items);
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
	
	@Override
	public String toString() {
		String ret = this.getTagLine() + ":\n";
		for(ItemSlot slot : this.items.keySet()) {
			ret += "| " + slot.name + ": ";
			boolean firstOnLine = true;
			for(Item i : this.items.get(slot)) {
				if(firstOnLine) {
					firstOnLine = false;
				} else {
					ret += ", ";
				}
				ret += i.name;
			}
			ret += "\n";
		}
		for(ItemSlot slot : this.getUnfilledSlots()) {
			ret += "| " + slot.name + ": [EMPTY]\n";
		}
		return ret;
	}
	
	protected String getTagLine() {
		return "EquipmentLoadout (" + this.size() + " items)";
	}
	
	protected int size() {
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
}
