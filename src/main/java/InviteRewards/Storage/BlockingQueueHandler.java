package InviteRewards.Storage;

import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueHandler implements Runnable {

    private LinkedBlockingQueue<SequentialRunnable> queue;

    public BlockingQueueHandler(LinkedBlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!this.queue.take().execute()) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
