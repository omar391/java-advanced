package com.astronlab.tut.concurrency.basic.tasks;

import java.util.Vector;
 
class Producer extends Thread {
    WaitNotifier waitNotifier = new WaitNotifier();
 
    static final int MAXQUEUE = 5;
    private Vector messages = new Vector();
 
    @Override
    public void run() {
        try {
            while (true) {
                putMessage();
                //sleep(5000);
            }
        } catch (InterruptedException e) {
        }
    }
 
    private synchronized void putMessage() throws InterruptedException {
        System.out.println("ppp");
        waitNotifier.doNotify();
        while (messages.size() == MAXQUEUE) {
            waitNotifier.doWait();
        }
        messages.addElement(new java.util.Date().toString());
        System.out.println("put message");
        //waitNotifier.doNotify();
        //Later, when the necessary event happens, the thread that is running it calls notify() from a block synchronized on the same object.
    }
 
    // Called by Consumer
    public synchronized String getMessage() throws InterruptedException {
        System.out.println("www");
        waitNotifier.doNotify();
        while (messages.size() == 0) {
            waitNotifier.doWait();//By executing wait() from a synchronized block, a thread gives up its hold on the lock and goes to sleep.
        }
        String message = (String) messages.firstElement();
        messages.removeElement(message);
        waitNotifier.doNotify();
        return message;
    }
}
 
class Consumer extends Thread {

    Producer producer;
 
    Consumer(Producer p) {
        producer = p;
    }
 
    @Override
    public void run() {

        try {
            while (true) {
                System.out.println("rrr");
                String message = producer.getMessage();
                System.out.println("Got message: " + message);
                //sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String args[]) {
        Producer producer = new Producer();
        producer.start();
        new Consumer(producer).start();
    }
}

class WaitNotifier {
    private Object lockObject = new Object();
    private boolean resumeSignal = false;

    public void doWait() {
        synchronized(lockObject){
            //Changing if block into while block
            while(!resumeSignal){
                try{
                    lockObject.wait();
                    //If spurious wakeup happens then this following line will execute and return to while
                    //condition check. If this was truly an non-intentional/spurious call then it will
                    //find resumeSignal to be false and will return to waiting state again otherwise it will just
                    //exit the waiting state. This solves our spurious wake up issue.
                } catch(InterruptedException e){}
            }

            resumeSignal = false;
        }
    }

    public void doNotify() {
        synchronized (lockObject) {
            resumeSignal = true;
            lockObject.notify();
        }
    }
}
