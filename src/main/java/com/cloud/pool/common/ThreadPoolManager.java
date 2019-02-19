package com.cloud.pool.common;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2019/1/22 10:11 AM
 * @Description: 线程池管理(线程统一调度管理)
 * @Created with cloud-demo-thpool
 * @Version: 1.0
 */
public class ThreadPoolManager {

    private static final String TAG = "ThreadPoolManager";

    private static final int  CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池维护线程的最少数量
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    /**
     * 线程池维护线程的最大数量
     */
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;

    /**
     * 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
     */
    private static final int KEEP_ALIVE_TIME = 1;

    /**
     * 单例模式
     */
    private static ThreadPoolManager manager;

    /**
     * 线程池对象
     */
    private static ThreadPoolExecutor executor;

    private static final BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingDeque<Runnable>();

    private static final ThreadFactory threadFactory = new ThreadFactory() {
        // 一个线程安全的Int类
        private final AtomicInteger atomicInteger = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"ThreadPoolManager #" + atomicInteger.getAndIncrement());
        }
    };

    /**
     * 私有构造，防止被初始化
     */
    public ThreadPoolManager () {

    }

    /**
     * 对外单例
     *
     * @return manager 操作类
     */
    public static ThreadPoolManager getInstance () {
        if (manager == null) {
            synchronized (ThreadPoolManager.class) {
                if (manager == null) {
                    manager = new ThreadPoolManager();
                }
            }
        }
        return manager;
    }

    /**************************************************************************************************************
     * 常见的几种线程技术
     **************************************************************************************************************
     * Java通过Executors提供四种线程池，分别为：
     * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
     * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。 newSingleThreadExecutor
     * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     *
     * 1、public static ExecutorService newFixedThreadPool(int nThreads) {
     * return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()); }
     *
     * 2、 public static ExecutorService newSingleThreadExecutor() {
     * return new FinalizableDelegatedExecutorService (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())); }
     *
     * 3、public static ExecutorService newCachedThreadPool() {
     * return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()); }
     ****************************************************************************************************************/

    /**
     * 为null时创建线程池 向线程池中添加任务方法
     * @param - 池中所保存的线程数，包括空闲线程。
     * @param - 池中允许的最大线程数。
     * @param - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
     * @param - keepAliveTime 参数的时间单位。
     * @param - 执行前用于保持任务的队列。此队列仅由保持 execute 方法提交的 Runnable 任务。
     * @param - 由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序。
     * 实质就是newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
     */
    public void execute (Runnable r) {
        if (executor == null) {
            //为null时创建executor
            System.out.println("CORE_POOL_SIZE = [" + CORE_POOL_SIZE + "] " + "MAX_POOL_SIZE = [" + MAX_POOL_SIZE + "] ");
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, poolWorkQueue, threadFactory);
        } else {
            //不为空，执行
            executor.execute(r);
        }
    }

    /**
     * 把线程从线程池中移除
     *
     * @param r 移除的对象
     */
    public void remove (Runnable r) {
        if (r != null) {
            executor.getQueue().remove(r);
        }
    }

    /**
     * 对外提供 ThreadPoolExecutor
     * @return executor
     */
    public ThreadPoolExecutor getThreadPool () {
        return executor;
    }

    /**
     * 判断是否是最后一个任务
     */
    protected boolean isTaskEnd() {
        if (executor.getActiveCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取缓存大小
     */
    public int getQueueSize(){
        return executor.getQueue().size();
    }

    /**
     * 获取线程池中的线程数目
     */
    public int getPoolSize(){
        return executor.getPoolSize();
    }

    /**
     * 获取已完成的任务数
     */
    public long getCompletedTaskCount(){
        return executor.getCompletedTaskCount();
    }

    /**
     * 关闭线程池，不在接受新的任务，会把已接受的任务执行玩
     */
    public void shutdown() {
        executor.shutdown();
    }

}
