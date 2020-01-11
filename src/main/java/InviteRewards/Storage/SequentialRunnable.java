package InviteRewards.Storage;

import InviteRewards.Main.Main;

import java.util.concurrent.CompletableFuture;

public abstract class SequentialRunnable {

    private CompletableFuture<Boolean> completableFuture;

    public SequentialRunnable() {
        this.completableFuture = new CompletableFuture<>();
    }

    public static class RunnableResult {
        private boolean success, continueExecuting;

        public RunnableResult(boolean success, boolean continueExecuting) {
            this.success = success;
            this.continueExecuting = continueExecuting;
        }

        public boolean isSuccessful() {
            return success;
        }

        public boolean continueExecuting() {
            return continueExecuting;
        }
    }

    public abstract RunnableResult run();

    public boolean execute() {
        RunnableResult result = run();
        completableFuture.complete(result.isSuccessful());
        Main.info("Continue Executing? " + result.continueExecuting);
        return result.continueExecuting();
    }

    public CompletableFuture<Boolean> getCompletableFuture() {
        return completableFuture;
    }


}
