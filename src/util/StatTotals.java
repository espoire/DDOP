package util;

import ddop.stat.Stat;

import java.util.HashMap;
import java.util.Set;

public class StatTotals extends HashMap<String, Double> implements Cloneable {
	private static final long serialVersionUID = -9134448548211626949L;
	
	private final Double defaultValue;
	private final Set<String> filter;

	public StatTotals()                    { this(0.0, null); }
	public StatTotals(double defaultValue) { this(defaultValue,  null); }
	public StatTotals(Set<String> filter)  { this(0.0, filter); }

	public StatTotals(double defaultValue, Set<String> filter) {
		this.defaultValue = defaultValue;
		this.filter       = filter;
	}

	@Override
	public Double get(Object key) {
		if(filter != null && ! filter.contains(key))
			throw new RuntimeException("Filtered StatTotals attempted to access an undeclared stat: " + key);

		return super.getOrDefault(key, this.defaultValue);
	}
	
	public double getDouble(String key) {
		return this.get(key);
	}
	
	public int getInt(String key) {
		return (int) this.getDouble(key);
	}

	public boolean getBoolean(String key) {
		return this.get(key) != 0;
	}
	
	public StatTotals subtract(StatTotals another) {
		StatTotals ret = new StatTotals(this.defaultValue);
		
		for(String statName : this.keySet()) {
			double magnitude = this.get(statName) - another.get(statName);
			if(magnitude > 0) ret.put(statName, magnitude);
		}
		
		return ret;
	}
	
	public String toString() {
		String ret = "";
		for(String statName : this.keySet()) {
			ret += "| " + Stat.toString(statName, this.get(statName)) + "\n";
		}
		return ret;
	}
}
