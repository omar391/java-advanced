package com.astronlab.tut.concurrency.intermediate;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**

Ref:
 http://www.artima.com/insidejvm/ed2/threadsynchP.html

 */
public class Part6 {
}

class Producer extends Thread {
	WaitNotifier waitNotifierProducer = new WaitNotifier();
	WaitNotifier waitNotifierConsumer = new WaitNotifier();

	static final int MAXQUEUE = 5;
	private Vector messages = new Vector();

	volatile boolean isFull = false;

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
		while (messages.size() == MAXQUEUE && !isFull) {
			isFull = true;
			waitNotifierProducer.doWait();
			isFull = false;
		}
		messages.addElement(new java.util.Date().toString());
		System.out.println("put message");
		waitNotifierProducer.doNotify();
	}

	// Called by Consumer
	public String getMessage() throws InterruptedException {
		while (messages.size() == 0 && !isFull) {
			waitNotifierProducer.doWait();
		}
		String message = (String) messages.firstElement();
		messages.removeElement(message);
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

	AtomicInteger waitCount = new AtomicInteger(0);

	public void doWait() {
		System.out.println(Thread.currentThread().getName()+"-wait - "+waitCount.get());
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
		System.out.println(Thread.currentThread().getName()+"-notify - "+waitCount.incrementAndGet());

		synchronized (lockObject) {
			resumeSignal = true;
			lockObject.notify();
		}
	}
}
