package com.astronlab.tut.concurrency.basic.tasks;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Answer5{
    /**
     Answer 1: Yes, their example2 will face missing-signal/spurious wake up problems.
     Answer 2: See below.
     * */
}


class Producer extends Thread {
    /**
    * Here we need to use two wait-notifiers class because of 2 reasons-

     1. Main reason
     --------------
    Suppose in getMessage() method -
     Line_20: while (messages.size() == 0) {
     Line_21:   waitNotifier.doWait();
     Line_22: }
     Sometimes, in-case we use a single waitNotifier, between line 20 and 21 there is tiny fraction of time distance and in the mean time
     our producer reach its max msg limit(5) and go to wait state. At the same time Line-21 will also go to wait state. So this cause a
     infinite lock state.

     2. Design reason
     -----------------
     Here we have circular task dependency, ie. producer->consumer->producer. So, we actually have 2 tasks sets -
     - producer -> consumer
     - consumer -> producer
    Hence, we need two waitNotifiers as described in Part5 class.

    * */

    WaitNotifier waitNotifierProducer = new WaitNotifier();
    WaitNotifier waitNotifierConsumer = new WaitNotifier();

    static final int MAXQUEUE = 5;
    AtomicInteger msgCount = new AtomicInteger(0);//Using atomic count for better vector size counting
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

    private void putMessage() throws InterruptedException {
        while (msgCount.get() == MAXQUEUE) {
            waitNotifierProducer.doWait();//moving into waiting state
        }
        messages.addElement(new java.util.Date().toString());
        System.out.println("put message");
        msgCount.incrementAndGet();
        waitNotifierConsumer.doNotify();
    }

    // Called by Consumer
    public String getMessage() throws InterruptedException {
        while (msgCount.get() == 0) {
            waitNotifierConsumer.doWait();//moving into waiting state
        }
        String message = (String) messages.firstElement();
        messages.removeElement(message);
        msgCount.decrementAndGet();
        waitNotifierProducer.doNotify();//a msg removed so, notify now

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
