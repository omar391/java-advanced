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
                        System.out.println("Thread 1: Holding lock1");
                        if (lock2.tryLock()) {
                            System.out.println("Thread 1: holding lock2");
                        }
                        System.out.println("Thread 1: holding lock1 & lock2");
                    }
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
                        System.out.println("Thread 2: Holding lock2");
                        if(lock1.tryLock()){
                            System.out.println("Thread 2: Holding lock1");

                        }
                        System.out.println("Thread 2: Holding lock1 & lock2");
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
