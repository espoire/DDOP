package ddop.stat.list;

import ddop.stat.Stat;
import ddop.stat.StatMap;
import ddop.stat.StatSource;
import ddop.stat.conversions.NamedStat;
import ddop.stat.conversions.SetBonus;
import util.StatTotals;

import java.util.List;

public class FastStatList extends AbstractStatList {
    private StatMap stats;

    public FastStatList(StatSource... sources) {
        super(sources);
    }

    @Override
    protected void init() {
        this.stats = new StatMap();
    }

    @Override
    public AbstractStatList add(Stat s) {
        if(NamedStat.isNamed(s)) return this.addAll(NamedStat.convert(s));

        this.stats.put(s);

        return this;
    }

    @Override
    public StatTotals getStatTotals() {
        this.applyStatConversions();
        return this.stats.getTotals();
    }

    private void applyStatConversions() {
        List<Stat> stats = SetBonus.getBonuses(this.stats);
        this.addAll(stats);
    }
}
