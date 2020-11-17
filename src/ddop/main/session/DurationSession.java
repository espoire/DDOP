package ddop.main.session;

import util.NumberFormat;

public class DurationSession extends ExecutionSession {
    private final long duration;

    public DurationSession(long duration) { this.duration = duration; }

    @Override
    public ExecutionSession splitToThreads(int threads) { return this; }

    @Override
    public double getCompletion(int trialsCompleted, long elapsedTime) {
        return (double) elapsedTime / this.duration;
    }

    @Override
    public void printSimStartMessage() {
        System.out.println("Running sim for the next " + NumberFormat.readableLongTime(this.duration) + ".");
    }
}
