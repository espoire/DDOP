package ddop.stat.list;

import ddop.stat.Stat;
import ddop.stat.StatFilter;
import ddop.stat.StatMap;
import ddop.stat.StatSource;
import ddop.stat.conversions.NamedStat;
import ddop.stat.conversions.SetBonus;
import util.StatTotals;

import java.util.List;

public class FastStatList extends AbstractStatList {
    private StatMap stats;

    public FastStatList(StatFilter filter, StatSource... sources) { super(filter, sources); }

    @Override
    protected void init() {
        this.stats = new StatMap();
    }

    @Override
    public void addImplementation(Stat s) {
        if(NamedStat.isNamed(s)) {
            this.addAll(NamedStat.convert(s));
        } else {
            this.stats.put(s);
        }
    }

    @Override
    public StatTotals getStatTotals(StatFilter filter) {
        this.applyStatConversions();
        return this.stats.getTotals(filter);
    }

    private void applyStatConversions() {
        List<Stat> stats = SetBonus.getBonuses(this.stats);
        this.addAll(stats);
    }
}
