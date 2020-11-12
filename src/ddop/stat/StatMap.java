package ddop.stat;

import util.StatTotals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatMap extends HashMap<String, Map<String, Double>> {
    public StatMap put(Stat s) {
        return this.put(s.category, s.bonusType, s.magnitude);
    }

    public StatMap put(String category, String bonusType, double magnitude) {
        if(!this.containsKey(category)) this.put(category, new HashMap<>());
        Map<String, Double> sub = this.getSubmap(category);

        if(!sub.containsKey(bonusType)) {
            sub.put(bonusType, magnitude);
        } else {
            double prior = sub.get(bonusType);
            if(Stat.stacks(bonusType)) {
                sub.put(bonusType, prior + magnitude);
            } else if(magnitude > prior) {
                sub.put(bonusType, magnitude);
            }
        }

        return this;
    }

    private Map<String, Double> getSubmap(String category) {
        Map<String, Double> sub = this.get(category);
        return sub;
    }

    public StatTotals getTotals(Set<String> filter) {
        StatTotals ret = new StatTotals(filter);

        for(String category : this.keySet())
            if(filter == null || filter.contains(category))
                ret.put(category, this.getCategoryTotal(category));

        return ret;
    }

    public double getCategoryTotal(String category) {
        Map<String, Double> sub = this.getSubmap(category);

        double total = 0;
        if(sub != null)
            for(Double d : sub.values())
                total += d;

        return total;
    }
}
