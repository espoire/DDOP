package ddop.optimizer.scoring.scorers.damage.spell;

import ddop.optimizer.scoring.scorers.StatScorer;
import ddop.optimizer.scoring.scorers.damage.DamageSource;
import util.NumberFormat;
import util.Pair;
import util.StatTotals;
import util.StringFormat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Spell extends DamageSource {
    public String name;

    public double baseDamage = 0.0;
    public String scalingStat = "null";
    public double scalingWeight = 1.0;
    public String critStat = "null";

    public double cooldown = 0.0;
    public double executeTime = 0.5;

    public Spell(String name, double baseDamage) { this(name, baseDamage, 0.0, 0.5); }

    public Spell(String name, double baseDamage, double cooldown, double executeTime) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.cooldown = cooldown;
        this.executeTime = executeTime;
    }

    @Override
    public Set<String> getQueriedStatCategories() {
        Set<String> ret = new HashSet<>();

        ret.addAll(Arrays.asList(
                this.scalingStat,
                this.critStat
        ));

        return ret;
    }

    public Spell setAcid() {
        this.scalingStat = "corrosion";
        this.critStat = "acid lore";
        return this;
    }

    public Spell setCold() {
        this.scalingStat = "glaciation";
        this.critStat = "ice lore";
        return this;
    }

    public Spell setLightning() {
        this.scalingStat = "magnetism";
        this.critStat = "lightning lore";
        return this;
    }

    public Spell setFire() {
        this.scalingStat = "combustion";
        this.critStat = "fire lore";
        return this;
    }

    public Spell setSonic() {
        this.scalingStat = "resonance";
        this.critStat = "sonic lore";
        return this;
    }

    public Spell setCannotCrit() {
        this.critStat = null;
        return this;
    }

    @Override
    protected Pair<Double, String> getDamage(StatTotals stats, StatScorer.Verbosity verbosity) {
        String messages = null;

        int modifiedDamage = (int) (this.baseDamage * (1 + stats.getInt(this.scalingStat) * this.scalingWeight / 100.0));

        int lore = 0;
        if(this.critStat != null) lore = stats.getInt(this.critStat);

        double critAverage = modifiedDamage * (1 + lore / 100.0);

        if(verbosity == StatScorer.Verbosity.FULL) {
            messages = "+- " + StringFormat.padStringToLength(this.name, 9) + ": "
                    + NumberFormat.readableLargeNumber(critAverage)
                    + " (" + NumberFormat.readableLargeNumber(critAverage / this.getExecuteTime(stats)) + " DPeT)\n";
        }

        return new Pair<>(critAverage, messages);
    }

    @Override
    protected double getCooldown(StatTotals stats) {
        return Math.max(this.cooldown, this.executeTime);
    }

    @Override
    protected double getExecuteTime(StatTotals stats) {
        return this.executeTime;
    }
}
