package ddop.stat.list;

import ddop.stat.Stat;
import ddop.stat.StatSource;
import util.StatTotals;

import java.util.HashMap;
import java.util.Map;

public class FastStatList extends AbstractStatList {
    private Map<String, Map<String, Double>> stats;

    public FastStatList(StatSource... sources) {
        super(sources);
    }

    @Override
    protected void init() {
        this.stats = new HashMap<>();
    }

    @Override
    public AbstractStatList add(Stat s) {
        if(!this.stats.containsKey(s.category)) this.stats.put(s.category, new HashMap<>());
        Map<String, Double> sub = this.stats.get(s.category);

        if(!sub.containsKey(s.bonusType)) {
            sub.put(s.bonusType, s.magnitude);
        } else {
            double prior = sub.get(s.bonusType);
            if(s.stacks()) {
                sub.put(s.bonusType, prior + s.magnitude);
            } else if(s.magnitude > prior) {
                sub.put(s.bonusType, s.magnitude);
            }
        }

        return this;
    }

    @Override
    public StatTotals getStatTotals() {
        StatTotals ret = new StatTotals();

        for(String category : this.stats.keySet()) {
            Map<String, Double> sub = this.stats.get(category);
            double total = 0;

            for(Double d : sub.values()) total += d;

            ret.put(category, total);
        }

        return ret;
    }
}
