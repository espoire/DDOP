package util;

import ddop.stat.Stat;

import java.util.HashMap;

public class StatTotals extends HashMap<String, Double> implements Cloneable {
	private static final long serialVersionUID = -9134448548211626949L;
	
	private final Double defaultValue;

	public StatTotals() { this(0.0); }
	public StatTotals(double defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}
	
	@Override
	public Double get(Object key) {
		return super.getOrDefault(key, this.defaultValue);
	}
	
	public double getDouble(Object key) {
		return this.get(key);
	}
	
	public int getInt(Object key) {
		return (int) this.getDouble(key);
	}

	public boolean getBoolean(Object key) {
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
