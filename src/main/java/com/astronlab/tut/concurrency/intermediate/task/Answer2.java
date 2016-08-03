package com.astronlab.tut.concurrency.intermediate.task;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by arjuda on 8/3/16.
 */
public class Answer2 {

	/**
	 * 3. Example a deadlock prevention class via ReentrantLock's tryLock() methods.
	 */
}

class Producer extends Thread {

	ReentrantLock lock1 = new ReentrantLock();
	ReentrantLock lock2 = new ReentrantLock();

	//Conditions to handle await/signal (previously: wait/notify)
	SafeCondition prodCond = new SafeCondition(lock1.newCondition());
  SafeCondition consumerCond = new SafeCondition(lock1.newCondition());

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
			//waitNotifierProducer.doWait();//moving into waiting state
			prodCond.await();
		}
		messages.addElement(new java.util.Date().toString());
		System.out.println("put message");
		msgCount.incrementAndGet();
		//waitNotifierConsumer.doNotify();
		consumerCond.signal();
	}

	// Called by Consumer
	public String getMessage() throws InterruptedException {
		while (msgCount.get() == 0) {
			//waitNotifierConsumer.doWait();//moving into waiting state
			consumerCond.await();
		}
		String message = (String) messages.firstElement();
		messages.removeElement(message);
		msgCount.decrementAndGet();
		//waitNotifierProducer.doNotify();//a msg removed so, notify now
		prodCond.signal();

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

class SafeCondition{
	private final Condition condition;
	private boolean resumeSignal = false;

	public SafeCondition(Condition condition){
		this.condition = condition;
	}

	public synchronized void await() {
		while (!resumeSignal) {
			try {
				condition.await();
			} catch (InterruptedException e) {
			}
		}
		resumeSignal = false;
	}

	public synchronized void signal() {
		resumeSignal = true;
		condition.signal();
	}

}
