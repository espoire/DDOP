package ddop.item;

import ddop.optimizer.valuation.ArmorType;
import ddop.optimizer.valuation.ShieldType;
import ddop.stat.Stat;
import ddop.stat.StatSource;
import util.Json;

import java.util.ArrayList;
import java.util.Collection;

public class Item implements StatSource {
	public final String name;
	public final ItemSlot slot;
	public final int minLevel;
	private final Collection<Stat> stats;
	private final PropertiesList props;
	
	final ArmorType armorType;
	final ShieldType shieldType;

	public String toJson() {
		return Json.formatMapRemoveEmptyLists(this.props);
	}
	
	public Item(PropertiesList pl) {
		this.props = pl;

		this.name = pl.getString("Name");
		this.slot = ItemSlot.getSlot(pl);
		this.minLevel = pl.getInt("Minimum Level");
		this.stats = Stat.getStats(pl.get("Enchantments"), this.name);

		this.armorType = ArmorType.get(pl.getString("Armor Type"), this.name);
		if(this.armorType == ArmorType.LIGHT)  this.stats.add(new Stat("light armor"));
		if(this.armorType == ArmorType.MEDIUM) this.stats.add(new Stat("medium armor"));
		if(this.armorType == ArmorType.HEAVY)  this.stats.add(new Stat("heavy armor"));
		if(this.armorType == ArmorType.DOCENT) this.stats.add(new Stat("docent"));

		this.shieldType = ShieldType.get(pl.getString("Shield Type"), this.name);
		if(this.shieldType == ShieldType.SMALL) this.stats.add(new Stat("small shield"));
		if(this.shieldType == ShieldType.LARGE) this.stats.add(new Stat("large shield"));
		if(this.shieldType == ShieldType.TOWER) this.stats.add(new Stat("tower shield"));
	}

	public Collection<Stat> getStats() {
		return new ArrayList<>(this.stats);
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		ret.append(this.name).append(" - ");
		ret.append(this.slot.name).append(" - ");
		ret.append("ML: ").append(this.minLevel).append("\n");
		
		for(Stat s : this.stats) {
			ret.append(s.toString()).append("\n");
		}
		
		return ret.toString();
	}
}