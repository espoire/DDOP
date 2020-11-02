package ddop.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertiesList extends HashMap<String, List<String>> {
	private static final long serialVersionUID = 4102890110985727401L;

	public PropertiesList() {}

    public PropertiesList(Map<String, List<String>> itemProps) {
    	this.putAll(itemProps);
    }

    public String toString() {
		return this.toString((String[]) null);
	}
	
	public String getFirst(String key) {
		key = key.toLowerCase();
		
		List<String> value = this.get(key);
		if(value == null || value.size() == 0) return null;
		return value.get(0);
	}

	public String getString(String key) {
		return this.getFirst(key);
	}

	public int getInt(String key) {
		String value = this.getFirst(key);
		if(value != null && value.equals("none")) return 0;
		
		try {
			int ret = Integer.parseInt(value);
			return ret;
		} catch(NumberFormatException nfe) {
			System.err.println("Invalid number format for key '" + key + "': " + value);
		}
		return 0;
	}
	
	public String toString(String... keys) {
		if(keys == null) keys = this.keySet().toArray(new String[this.keySet().size()]);
		
		String ret = "";
		boolean firstKey = true;
		boolean firstValue;
		
		for(String key : keys) {
			if(!firstKey) {
				ret += "\n";
			} else {
				firstKey = false;
			}
			ret += key + ": ";
			
			List<String> values = this.get(key);
			
			firstValue = true;
			for(String value : values) {
				if(!firstValue) {
					ret += ", ";
				} else {
					firstValue = false;
				}
				ret += value;
			}
		}
		
		return ret;
	}
	
	public List<String> get(String key) {
		return super.get(key.toLowerCase());
	}
}
