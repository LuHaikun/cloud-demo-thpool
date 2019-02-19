package com.cloud.pool;

import com.cloud.pool.common.ThreadPoolManager;
import com.cloud.pool.task.MyTask;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ThreadPoolManager manager = ThreadPoolManager.getInstance();
        for (int i = 0; i < 100 ; i++) {
            manager.execute(new MyTask(i));
            System.out.println("线程池中线程数目：" + manager.getPoolSize() + "，队列中等待执行的任务数目："
                    + manager.getQueueSize() + "，已执行玩别的任务数目：" + manager.getCompletedTaskCount());
        }
        manager.shutdown();
    }

}
