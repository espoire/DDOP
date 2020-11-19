package ddop.dto.session;

public abstract class ExecutionSession {
    public abstract double getCompletion(int trialsCompleted, long elapsedTime);
    public abstract void printSimStartMessage();

    public abstract ExecutionSession splitToThreads(int threads);
}