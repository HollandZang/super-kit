package com.holland.kit.base.thread;

import java.util.Deque;
import java.util.concurrent.*;

public class ReentrantTest {
    static final PersistenceContainer persistenceContainer = new PersistenceContainer();
    static final Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) {

        // 主要的消费动作
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1
                , 1
                , 1, TimeUnit.HOURS
                , new ArrayBlockingQueue<>(1)
                , Thread::new
                , (r, executor1) -> {
            if (r instanceof ReentrantTask) {
                final ReentrantTask reentrantTask = (ReentrantTask) r;
                System.out.printf("拒绝: %s, 等待后续重写\n", reentrantTask.args);
                // 把拒绝任务信息持久化
                reentrantTask.args.reentrantTotal += 1;
                persistenceContainer.push(reentrantTask.args);
            } else {
                System.out.printf("拒绝: %s\n", r);
            }
        }
        );

        for (int i = 0; i < 4; i++) {
            executor.execute(new ReentrantTask(new ReentrantTask.ReentrantTaskArgs(i)));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 额外的重启动作
        new Thread(() -> {
            while (true) {
                // await
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                final ReentrantTask.ReentrantTaskArgs popArgs = persistenceContainer.pop();
                if (null != popArgs) {
                    executor.execute(new ReentrantTask(popArgs));
                }
            }
        }).start();
    }

    public static class ReentrantTask implements Runnable {
        public final ReentrantTaskArgs args;

        public ReentrantTask(ReentrantTaskArgs args) {
            this.args = args;
        }

        @Override
        public void run() {
            if (args.reentrantTotal == 0) {
                System.out.printf("执行: %s\n", args);
            } else {
                System.out.printf("执行: %s, 重入次数: %d\n", args, args.reentrantTotal);
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static class ReentrantTaskArgs {
            public int reentrantTotal = 0;
            public final int arg0;

            public ReentrantTaskArgs(int arg0) {
                this.arg0 = arg0;
            }

            @Override
            public String toString() {
                return "" + arg0;
            }
        }
    }

    /**
     * 模拟持久化容器
     */
    public static class PersistenceContainer {
        private final Deque<ReentrantTask.ReentrantTaskArgs> deque = new ConcurrentLinkedDeque<>();

        /**
         * 模拟写入操作
         */
        public void push(ReentrantTask.ReentrantTaskArgs args) {
            deque.push(args);
            // notify
            semaphore.release();
        }

        /**
         * 模拟读取操作
         */
        public ReentrantTask.ReentrantTaskArgs pop() {
            return deque.pollFirst();
        }
    }

}
