package ddop.main.session;

import util.NumberFormat;

public class TrialsSession extends ExecutionSession {
    private final int trials;

    public TrialsSession(int trials) { this.trials = trials; }

    @Override
    public ExecutionSession splitToThreads(int threads) {
        return new TrialsSession(this.trials / threads);
    }

    @Override
    public double getCompletion(int trialsCompleted, long elapsedTime) {
        return (double) trialsCompleted / this.trials;
    }

    @Override
    public void printSimStartMessage(int totalItemsConsidered, int totalSlots, double totalCombinations) {
        System.out.println("Testing " + NumberFormat.readableLargeNumber(this.trials) + " trials of " + totalItemsConsidered + " items over " + totalSlots + " item slots");
        System.out.println("in a maximum of " + NumberFormat.readableLargeNumber(totalCombinations) + " possible combinations.");
    }
}
