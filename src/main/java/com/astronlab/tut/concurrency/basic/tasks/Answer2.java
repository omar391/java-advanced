package com.astronlab.tut.concurrency.basic.tasks;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2. Based on Part3 and Part4, write an example class which proves, volatile variable doesn't prevent harmful race conditions.
 Hint: You could just copy the required code sections from Part3.
 */
public class Answer2 {

	private Answer2() {
	}


	public static void main(String[] args) throws InterruptedException {

		//#1. Non-Harmful data-race
		System.out.println(
				"Atomic Operation(Doesn't occur race condition) \n======================");
		new criticalSectionWithVolatile().initAtomic();

		//#2. harmful; data-races*
		//Using: Volatile
		System.out.println("\nUsing volatile variable\n=================");
		new criticalSectionWithVolatile().initVolatile();

	}
		}

		class criticalSectionWithVolatile{
			private final CriticalSection cSection = new CriticalSection();
			private static final int TOTAL_THREADS = 100;
			private static final int NO_OF_EXECUTIONS = 100;


			public void initAtomic() throws InterruptedException {
				boolean raceCondAffected = false;

				for (int i = 0; i < NO_OF_EXECUTIONS; i++) {

					ArrayList<Thread> threadsList = new ArrayList<>();

					for (int j = 0; j < TOTAL_THREADS; j++) {
						Thread thread = new Thread(new Runnable() {
							@Override public void run() {
								cSection.atomicInteger.getAndIncrement();
							}
						});

						thread.start();
						threadsList.add(thread);
					}

					//Wait till all threads finish(done via join; we'll know more about join later)
					for (Thread thread : threadsList) {
						thread.join();
					}

					if (cSection.atomicInteger.get() != TOTAL_THREADS) {
						System.out.println(
								"Race condition affected data in:" + i + "th execution");
						System.out.println(
								"Incremented value of " + i + " threads is:" + cSection.getCount());
						raceCondAffected = true;
						break;
					} else {
						cSection.atomicInteger.set(0);
					}
				}

				if (!raceCondAffected) {
					System.out.println(
							"Race condition didn't affect data in:" + NO_OF_EXECUTIONS + " executions");
				}
			}

			public void initVolatile() throws InterruptedException {
				boolean raceCondAffected = false;

				for (int i = 0; i < NO_OF_EXECUTIONS; i++) {

					ArrayList<Thread> threadsList = new ArrayList<>();

					for (int j = 0; j < TOTAL_THREADS; j++) {
						Thread thread = new Thread(new Runnable() {
							@Override public void run() {
								cSection.doIncrement();
							}
						});

						thread.start();
						threadsList.add(thread);
					}

					//Wait till all threads finish(done via join; we'll know more about join later)
					for (Thread thread : threadsList) {
						thread.join();
					}

					if (cSection.getCount() != TOTAL_THREADS) {
						System.out.println(
								"Race condition affected data in:" + i + "th execution");
						System.out.println(
								"Incremented value of " + TOTAL_THREADS + " threads is:" + cSection.getCount());
						raceCondAffected = true;
						break;
					} else {
						cSection.resetCount();
					}
				}

				if (!raceCondAffected) {
					System.out.println(
							"Race condition didn't affect data in:" + NO_OF_EXECUTIONS + " executions");
				}
			}
		}



class CriticalSection {
	private  int count = 0;
	public AtomicInteger atomicInteger = new AtomicInteger(0);
	private volatile static int count_v = 0;

	public void doIncrement() {
		this.count_v++;
	}

	public int getCount() {
		return count_v;
	}

	public void resetCount() {
		count_v = 0;
	}
}