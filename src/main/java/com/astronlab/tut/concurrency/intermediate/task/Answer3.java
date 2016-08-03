package com.astronlab.tut.concurrency.intermediate.task;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by User on 8/2/2016.
 */
public class Answer3 {
    static ReentrantLock lock1 = new ReentrantLock();
    static ReentrantLock lock2 = new ReentrantLock();

    public static void main(String args[]) {
        ThreadDemo1 T1 = new ThreadDemo1();
        ThreadDemo2 T2 = new ThreadDemo2();
        T1.start();
        T2.start();
    }

    static class ThreadDemo1 extends Thread {
        public void run() {
            int i = 0;
            while (i< 5) {
                try {
                    if (lock1.tryLock()) {
                        System.out.println("Thread 1- "+i+"th itr: owned lock1");
                        Thread.sleep(2000);
                        if (lock2.tryLock()) {
                            System.out.println("Thread 1- "+i+"th itr: owned lock2\n");
                        }else{
                            System.out.println("Thread 1- "+i+"th itr: owned lock1 but Failed to own lock2\n");
                        }
                    }else{
                        System.out.println("Thread 1- "+i+"th itr: Failed to owned lock1\n");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(lock2.isHeldByCurrentThread()) {  //checking for: if current thread locks the lock then it unlocks.
                        lock2.unlock();
                    }
                    if(lock1.isHeldByCurrentThread()) {
                        lock1.unlock();
                    }
                }
                i++;
            }
        }
    }

    static class ThreadDemo2 extends Thread {
        public void run() {
           int i = 0;
            while(i<5){
                try{
                    if(lock2.tryLock()){
                        System.out.println("Thread 2- "+i+"th itr: owned lock2");
                        if(lock1.tryLock()){
                            System.out.println("Thread 2- "+i+"th itr: owned lock1 & lock2");
                        }else{
                            System.out.println("Thread 2- "+i+"th itr: owned lock2 but Failed to own lock1\n");
                        }
                    }else{
                        System.out.println("Thread 2- "+i+"th itr: Failed to owned lock2\n");
                    }
                }finally {
                    if(lock1.isHeldByCurrentThread()) {
                        lock1.unlock();
                    }
                    if(lock2.isHeldByCurrentThread()) {
                        lock2.unlock();
                    }
                }
                i++;
            }
        }
    }
}
