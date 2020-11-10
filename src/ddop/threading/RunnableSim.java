package ddop.threading;

import ddop.dto.SimResultContext;
import ddop.main.session.ExecutionSession;
import ddop.optimizer.ScoredLoadout;

public abstract class RunnableSim implements Runnable {
    private final ExecutionSession session;

    private boolean isMasterThread = false;

    private int trialsCompleted, percent;
    private long startTime, elapsedTime;
    private double progress;

    public SimResultContext result;

    protected double getProgress() { return this.progress; }
    protected boolean isDone() { return this.progress >= 1.0; }
    public void makeMasterThread() { this.isMasterThread = true; }

    protected RunnableSim(ExecutionSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        this.initialize();
        this.result = this.produceResult();
    }

    protected void initialize() {
        this.trialsCompleted = 0;
        this.elapsedTime = 0;
        this.percent = 0;
        this.progress = 0.0;
        this.startTime = System.currentTimeMillis();
    }

    protected SimResultContext produceResult() {
        while(! this.isDone()) {
            this.iterate();
            this.updateProgress();
        }

        return this.getResult();
    }

    protected abstract void iterate();
    protected abstract SimResultContext getResult();

    protected void updateProgress() {
        this.trialsCompleted++;
        this.elapsedTime = System.currentTimeMillis() - this.startTime;

        this.progress = this.session.getCompletion(this.trialsCompleted, this.elapsedTime);

        if(this.isMasterThread) this.printProgressMessage();
    }

    private void printProgressMessage() {
        int percentageComplete = (int) (this.progress * 100);

        if(percentageComplete > this.percent) {
            System.out.print(percentageComplete + "% ");
            if(percentageComplete / 10 > this.percent / 10) System.out.println();
        }

        this.percent = percentageComplete;
    }

    protected SimResultContext generateResultContext(ScoredLoadout best) {
        return new SimResultContext(best, this.trialsCompleted, this.elapsedTime);
    }
}
