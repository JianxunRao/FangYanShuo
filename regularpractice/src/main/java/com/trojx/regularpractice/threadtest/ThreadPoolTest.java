package com.trojx.regularpractice.threadtest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**线程池实例
 * Created by Trojx on 2016/2/29.
 */
public class ThreadPoolTest {
    public static void main(String[] args){
//        ExecutorService executorService=Executors.newCachedThreadPool();
        ThreadPoolExecutor executor=new ThreadPoolExecutor(5,10,200, TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(10));
       long start=System.currentTimeMillis();
        for(int i=0;i<20;i++){
            MyRunable myRunable=new MyRunable(i);
            executor.execute(myRunable);
            System.out.println("线程池中线程数目："+executor.getPoolSize()+",队列中正在等待的任务数："+executor.getQueue().size());
        }
        executor.shutdown();
    }
}
class MyRunable implements Runnable{

    private int num;
    public MyRunable(int num){
        this.num=num;
    }
    @Override
    public void run() {
        System.out.println("正在运行的Runable" + num);
        try {
            Thread.currentThread().sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Runable"+num + "运行完毕");

    }
}
