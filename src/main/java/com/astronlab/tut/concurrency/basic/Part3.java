package com.astronlab.tut.concurrency.basic;

/*
Race conditions
=======================================
The problems arise when multiple threads access shared resources
i.e.
variables, arrays, or objects, systems(databases, web services etc.) or files

In fact, problems only arise if one or more of the threads "write" to these resources.
If multiple threads read the same resource, race conditions do not occur.

A code section that leads to race conditions is called a critical section.

Suppose two threads run on following code block (critical section):

count = 5; //Shared variable reference/instance among threads
count++;

Here problem happens (if not synchronized)
-------------------------------------------
1. thread-1 reads value 5
2. thread-2 also reads value 5 before threads-1 could write a new value, because they runs concurrently without being synchronized
3. thread-1 increase it into 6 and writes it
4. thread-2 increase it into 6 too and writes it

So, the final value is 6 but it should have been 7. This effect is called "Harmful race condition"

Ways to prevent "harmful" race conditions:
--------------------------------------------
- By proper thread synchronization in critical sections
- By using atomic variables (AtomicInteger, AtomicLong etc. classes)
- By making the shared objects immutable(non-changeable)

 Atomic variable: Introduction <we will know more later>
 -----------------------------------------------------------
 Atomic variables are built-in classes(AtomicInteger, AtomicLong etc.) that support
 atomic operations(no other threads can interfere in the operation, hence the name) on single variables.
 In case of above count example, we would write it as follows via Atomic variable -

		AtomicInteger atomicInteger = new AtomicInteger(5);
		atomicInteger.getAndIncrement();

Notes
----------------
Above mentioned ways are not meant to prevent data races. Those are meant to prevent data races from doing harm.
However, If a program is so heavily synchronized so that no data race is possible, then it'd be better off just
making it single threaded as it would always execute one task at a time. Since, the app will never be concurrent!
*/

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Part3 {
	private Part3(){}

	//In this following code section we'll try detect harmful race condition with 3 mechanisms
	//Via Normal threading, Synchronized threading, Threading with Atomic variables

	public static void main(String[] args) throws InterruptedException {

		//#1. Harmful data-race
		System.out.println(
				"Normal threading operation (race-condition might overwrite data)\n======================");
		new CriticalSectionTest().init();

		//#2. Non harmful; data-races*
		//Using: synchronization
		System.out.println("\nSynchronized threading operation\n=================");
		new CriticalSectionTest().initWithSync();

		//#3. Non-harmful race* conditions
		//Using: Atomic variables
		System.out.println("\nAtomic threading operation\n=======================");
		new CriticalSectionTest().initAtomic();

		//*=thread's execution sequence/order is still not controlled but it does no harm

		//run the programme multiple times to see the effect
	}
}

class CriticalSectionTest {
	//Using a single instance to make it "shared" among threads
	private final CriticalSection cSection = new CriticalSection();

	//We are using loop to detect harmful data race because it might not happen in a single execution
	//i.e: trying 100(NO_OF_EXECUTIONS)times with 100(TOTAL_THREADS) threads executing each time
	// concurrently to find the effect of race-conditions
	private static final int TOTAL_THREADS = 100;
	private static final int NO_OF_EXECUTIONS = 100;

	public void init() throws InterruptedException {
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

			//Wait till all threads finish(done via join; we'll know more about join, later)
			for (Thread thread : threadsList) {
				thread.join();
			}

			if (cSection.getCount() != TOTAL_THREADS) {
				System.out.println(
						"Race condition affected data in:" + i + " th/st/rd execution");
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

	public void initWithSync() throws InterruptedException {
		boolean raceCondAffected = false;

		for (int i = 0; i < NO_OF_EXECUTIONS; i++) {

			ArrayList<Thread> threadsList = new ArrayList<>();

			for (int j = 0; j < TOTAL_THREADS; j++) {
				Thread thread = new Thread(new Runnable() {
					@Override public void run() {
						cSection.doSynchronizedIncrement();
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
						"Incremented value of " + i + " threads is:" + cSection.getCount());
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
}

class CriticalSection {
	private int count = 0;
	public AtomicInteger atomicInteger = new AtomicInteger(0);

	public void doIncrement() {
		this.count++;
	}

	public synchronized void doSynchronizedIncrement() {
		this.count++;
	}

	public int getCount() {
		return count;
	}

	public void resetCount() {
		count = 0;
	}
}