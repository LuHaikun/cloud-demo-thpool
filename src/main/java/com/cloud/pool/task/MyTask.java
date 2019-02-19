package com.cloud.pool.task;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2019/1/22 10:42 AM
 * @Description: 自定义线程任务
 * @Created with cloud-demo-thpool
 * @Version: 1.0
 */
public class MyTask implements Runnable {
    private int taskNum;
    public MyTask (int taskNum) {
        this.taskNum = taskNum;
    }
    @Override
    @SuppressWarnings("static-access")
    public void run() {
        System.out.println("正在运行的task " + taskNum);
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task " + taskNum + "执行完毕");
    }
    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }
}
