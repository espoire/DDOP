package ddop.dto;

import ddop.optimizer.ScoredLoadout;

public class SimResultContext {
    public final ScoredLoadout best;
    public final int trialsCompleted;
    public final long elapsedTime;

    public SimResultContext(ScoredLoadout best, int trialsCompleted, long elapsedTime) {
        this.best = best;
        this.trialsCompleted = trialsCompleted;
        this.elapsedTime = elapsedTime;
    }

    public static SimResultContext merge(SimResultContext[] results) {
        int count = 0;
        ScoredLoadout best = null;
        int trialsCompleted = 0;
        long elapsedTime = 0;

        for(SimResultContext c : results) {
            if(c == null) continue;

            if(best == null || c.best.score > best.score) best = c.best;
            trialsCompleted += c.trialsCompleted;
            if(c.elapsedTime > elapsedTime) elapsedTime = c.elapsedTime;

            count++;
        }

        System.out.println("Merged results from " + count + " threads.");
        return new SimResultContext(best, trialsCompleted, elapsedTime);
    }
}
