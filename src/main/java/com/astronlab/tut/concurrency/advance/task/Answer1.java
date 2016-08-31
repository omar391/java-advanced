package com.astronlab.tut.concurrency.advance.task;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Answer1/port our consumer-producer answer from intermediate/task/Answer2 via Executor service.
 */
public class Answer1 {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		Producer producer = new Producer();
		executorService.submit(producer);
		Consumer consumer = new Consumer(producer);
		executorService.submit(consumer);

	}

	static class Producer implements Callable {
		private ReentrantLock lock = new ReentrantLock();

		//Conditions to handle await/signal (previously: wait/notify)
		private SafeCondition prodCond = new SafeCondition(lock);
		private SafeCondition consumerCond = new SafeCondition(lock);

		private static final int MAXQUEUE = 5;
		//Using atomic count for better vector size counting
		private AtomicInteger msgCount = new AtomicInteger(0);
		private Vector messages = new Vector();

		@Override public Object call() {
			try {
				while (true) {
					putMessage();
					//sleep(500);
				}
			} catch (InterruptedException e) {
			}
			return null;
		}

		private void putMessage() throws InterruptedException {
			try {
				lock.lock();
				while (msgCount.get() == MAXQUEUE) {
					prodCond.await();
				}
				messages.addElement(new java.util.Date().toString());
				System.out.println("put message");
				msgCount.incrementAndGet();
				consumerCond.signal();

			} finally {
				lock.unlock();
			}
		}

		// Called by Consumer
		String getMessage() throws InterruptedException {
			String message = null;
			try {
				lock.lock();
				while (msgCount.get() == 0) {
					//moving into waiting state
					consumerCond.await();
				}
				message = (String) messages.firstElement();
				messages.removeElement(message);
				msgCount.decrementAndGet();
				//a msg removed so, notify now
				prodCond.signal();

			} finally {
				lock.unlock();
			}

			return message;
		}
	}

	static class Consumer implements Callable {
		private Producer producer;

		private Consumer(Producer p) {
			producer = p;
		}

		@Override public Object call() {
			try {
				while (true) {
					String message = producer.getMessage();
					System.out.println("Got message: " + message);
					//sleep(200);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	static class SafeCondition {
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

