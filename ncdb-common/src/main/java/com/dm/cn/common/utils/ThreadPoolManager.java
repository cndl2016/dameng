package com.dm.cn.common.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author root
 * @date 2024/10/16
 */
public class ThreadPoolManager {
    private static ThreadPoolManager mInstance;
    private int corePoolSize;
    /**
     * 最大线程池数量，缓冲队列满时能继续容纳的等待任务数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 10;
    private TimeUnit unit = TimeUnit.MINUTES;
    private ThreadPoolExecutor executor;

    private ThreadPoolManager() {
    }

    /**
     * 缓冲队列
     */
    private static BlockingQueue<Runnable> workQueue = null;

    public static synchronized ThreadPoolManager getInstance() {
        if (mInstance == null) {
            mInstance = new ThreadPoolManager();
            mInstance.init();
        }
        return mInstance;
    }

    private void init() {
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        maximumPoolSize = corePoolSize;
        workQueue = new LinkedBlockingQueue<Runnable>(500);
        executor = new ThreadPoolExecutor(
                // 某核心任务执行完毕，一次从缓冲队列中取出等待任务
                corePoolSize,
                // 最大线程池数量
                maximumPoolSize,
                // 等待任务的存活时间
                keepAliveTime,
                unit,
                // 缓冲队列，用于存放等待任务
                workQueue,
                // 创建线程的工厂
                new DefaultThreadFactory(Thread.NORM_PRIORITY, "tiaoba-pool-"),
                // 超出size任务的处理策略
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }


    private static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池计数
         */
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        /**
         * 线程池计数
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            // 默认线程组
            this.group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
            // isDaemon() 是否为守护线程
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            thread.setPriority(threadPriority);
            return thread;
        }
    }

    public void execute(Runnable runnable) {
        if (executor == null) {
            init();
        }
        if (runnable != null) {
            executor.execute(runnable);
        }
    }
}