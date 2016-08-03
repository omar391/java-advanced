package com.astronlab.tut.concurrency.intermediate.task;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by arjuda on 8/3/16.
 */
public class Answer2_v2 {

	/**
	 * 2. Solve our consume-producer problem of Question5/Basic_part via ReentrantLock class.

	  Done via: 1 ReentrantLock lock and 1 conditions : Follow origin site's solution
	 */

	//Main method is moved from Consumer class because nested child classes cant contain main method
	public static void main(String args[]) {
		Producer producer = new Answer2_v2().new Producer();
		producer.setName("Producer");
		producer.start();

		Thread consumer = new Answer2_v2().new Consumer(producer);
		consumer.setName("Consumer");
		consumer.start();
	}

	class Producer extends Thread {
		ReentrantLock lock1 = new ReentrantLock();

		//Conditions to handle await/signal (previously: wait/notify)
		SafeCondition prodCond = new SafeCondition(lock1);
		SafeCondition consumerCond = new SafeCondition(lock1);

		static final int MAXQUEUE = 5;
		//Using atomic count for better vector size counting
		AtomicInteger msgCount = new AtomicInteger(0);
		private Vector messages = new Vector();

		@Override public void run() {
			try {
				while (true) {
					putMessage();
					//sleep(5000);
				}
			} catch (InterruptedException e) {
			}
		}

		private void putMessage() throws InterruptedException {
			lock1.lock();
			while (msgCount.get() == MAXQUEUE) {
				//waitNotifierProducer.doWait();//moving into waiting state
				prodCond.await();
			}
			messages.addElement(new java.util.Date().toString());
			System.out.println("put message");
			msgCount.incrementAndGet();
			consumerCond.signal();
			lock1.unlock();
		}

		// Called by Consumer
		public String getMessage() throws InterruptedException {
			lock1.lock();
			while (msgCount.get() == 0) {
				//waitNotifierConsumer.doWait();//moving into waiting state
				consumerCond.await();
			}
			String message = (String) messages.firstElement();
			messages.removeElement(message);
			msgCount.decrementAndGet();
			//waitNotifierProducer.doNotify();//a msg removed so, notify now
			prodCond.signal();
			lock1.unlock();

			return message;
		}
	}

	class Consumer extends Thread {
		Producer producer;

		Consumer(Producer p) {
			producer = p;
		}

		@Override public void run() {
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
	}

	class SafeCondition {
		private final Condition condition;
		private final ReentrantLock lock;
		private boolean resumeSignal = false;

		public SafeCondition(ReentrantLock lock) {
			this.lock = lock;
			this.condition = lock.newCondition();
		}

		public void await() {
			try {
				lock.lock();
				while (!resumeSignal) {
					//System.out.println("Waiting via: " + Thread.currentThread().getName());
					try {
						condition.await();
					} catch (InterruptedException e) {
					}
				}
				resumeSignal = false;
			} finally {
				lock.unlock();
			}
		}

		public void signal() {
			try {
				lock.lock();
				//System.out.println("Signalled via: " + Thread.currentThread().getName());
				resumeSignal = true;
				condition.signal();
			} finally {
				lock.unlock();
			}
		}
	}
}