package com.astronlab.tut.concurrency.intermediate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import static java.lang.Thread.sleep;

/**
 CountDownLatch:
 =======================================
 It is used when we want to wait for more than one thread to complete its task. It is similar to "join()"
 in threads.

 Consider a scenario where we have three threads "A", "B" and "C" and we want to start thread "C" only
 when "A" and "B" threads completes or partially completes their task.

 Checkout our CountDownLatchExample class.


 Phaser:
 =================
 With CountDownLatch we need to set number of counter in the constructor. However, we can't change this counter
 later. To dynamically manage this issue we could use Phaser class.

 Checkout our PhaserExample class and PhaserExample2 for more detailed demonstrations.

*/

class CountDownLatchExample {
	private static int N = 4;

	public static void main(String args[]) throws InterruptedException {
		CountDownLatch doneSignal = new CountDownLatch(N);

		for (int i = 0; i < N; ++i) // create and start threads
		{
			new Thread(new WorkerRunnable(doneSignal)).start();
		}

		doneSignal.await();           // wait for all N threads to finish via calling countDown()
		System.out.println("finished");
	}

	 private static class WorkerRunnable implements Runnable {
		private final CountDownLatch doneSignal;

		WorkerRunnable(CountDownLatch doneSignal) {
			this.doneSignal = doneSignal;
		}

		public void run() {
			try {
				doWork();
				doneSignal.countDown();
			} catch (Exception ex) {
			} // return;
		}

		void doWork() {
			System.out.println(Thread.currentThread().getName()+"------ Working");
		}
	}
}

class PhaserExample {
	private static int N = 4;
	static Phaser phaser = new Phaser();

	public static void main(String args[]) throws InterruptedException {

		//initially register itself(main-thread)
		phaser.register();

		for (int i = 0; i < N; ++i) // create and start threads
		{
			phaser.register(); // register this task prior to execution
			new Thread(new WorkerRunnable()).start();
		}

		phaser.arriveAndAwaitAdvance();// wait for all N threads to finish via calling arrive()
		System.out.println("finished");
	}

	private static class WorkerRunnable implements Runnable {
		public void run() {
			try {
				doWork();
				phaser.arrive();
			} catch (Exception ex) {
			} // return;
		}

		void doWork() throws InterruptedException {
			System.out.println(Thread.currentThread().getName()+"------ Working");
			sleep(1000);
		}
	}
}

class PhaserExample2 {
	public static void main(String[] args) throws InterruptedException {

		List<Runnable> tasks = new ArrayList<>();

		for (int i = 0; i < 2; i++) {

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName() + ":go  :" + new Date());
					int a = 0, b = 1;
					for (int i = 0; i < 2000000000; i++) {
						a = a + b;
						b = a - b;
					}
					System.out.println(Thread.currentThread().getName() + ":done:" + new Date());
				}
			};

			tasks.add(runnable);
		}

		new PhaserExample2().runTasks(tasks);
	}

	void runTasks(List<Runnable> tasks) throws InterruptedException {

		/** The phaser is a nice synchronization barrier. */
		final Phaser phaser = new Phaser(1) {
			/**
			 * onAdvance() is invoked when all threads reached the synchronization barrier. It returns true if the
			 * phaser should terminate, false if phaser should continue with next phase. When terminated: (1) attempts
			 * to register new parties have no effect and (2) synchronization methods immediately return without waiting
			 * for advance. When continue:
			 *
			 * <pre>
			 *       -> set unarrived parties = registered parties
			 *       -> set arrived parties = 0
			 *       -> set phase = phase + 1
			 * </pre>
			 *
			 * This causes another iteration for all thread parties in a new phase (cycle).
			 *
			 */
			protected boolean onAdvance(int phase, int registeredParties) {
				System.out.println("On advance" + " -> Registered: " + getRegisteredParties() + " - Unarrived: "
						+ getUnarrivedParties() + " - Arrived: " + getArrivedParties() + " - Phase: " + getPhase());
				/**
				 * This onAdvance() implementation causes the phaser to cycle 1 time (= 2 iterations).
				 */
				return phase >= 1 || registeredParties == 0;
			}
		};

		dumpPhaserState("After phaser init", phaser);

		/** Create and start threads. */
		for (final Runnable task : tasks) {
			/**
			 * Increase the number of unarrived parties -> equals the number of parties required to advance to the next
			 * phase.
			 */
			phaser.register();
			dumpPhaserState("After register", phaser);
			new Thread() {
				public void run() {
					do {
						/**
						 * Wait for all threads reaching the synchronization barrier: more precisely, wait for arrived
						 * parties = registered parties. If arrived parties = registered parties: phase advances and
						 * onAdvance() is invoked.
						 */
						phaser.arriveAndAwaitAdvance();
						dumpPhaserState(Thread.currentThread().getName()+"- After waiting finished:", phaser);
						task.run();
					} while (!phaser.isTerminated());
				}
			}.start();
			Thread.sleep(500);
			dumpPhaserState("After arrival", phaser);
		}

		phaser.arrive();
		dumpPhaserState("After all arrival's done:", phaser);
		Thread.sleep(500);

		/**
		 * When the final party for a given phase arrives, onAdvance() is invoked and the phase advances. The
		 * "face advances" means that all threads reached the barrier and therefore all threads are synchronized and can
		 * continue processing.
		 */
		dumpPhaserState("Before main thread arrives and deregisters", phaser);
		/**
		 * The arrival and deregistration of the main thread allows the other threads to start working. This is because
		 * now the registered parties equal the arrived parties.
		 */
		phaser.arriveAndDeregister();
		dumpPhaserState("After main thread arrived and deregistered", phaser);
		System.out.println("Main thread will terminate ...");
	}

	private void dumpPhaserState(String when, Phaser phaser) {
		System.out.println(when + " -> Registered: " + phaser.getRegisteredParties() + " - Unarrived: "
				+ phaser.getUnarrivedParties() + " - Arrived: " + phaser.getArrivedParties() + " - Phase: "
				+ phaser.getPhase());
	}
}

public class Part10 {
}

/**
 Following topics are only for acquaintance purposes. These will help you in interviews.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 ConcurrentMap / ConcurrentHashMap:
 ========================================
 The ConcurrentHashMap is very similar to the HashTable class. It has following features:

 - Capable of handling concurrent access (puts and gets) to it.
 - ConcurrentHashMap offers better concurrency than HashTable does.
 - ConcurrentHashMap does not lock the Map while you are reading from it.
 - ConcurrentHashMap does not lock the entire Map when writing to it.
 - It only locks the part of the Map that is being written to, internally.
 - It does not throw ConcurrentModificationException if the ConcurrentHashMap is changed while being iterated.


 Compare and swap:
 =======================================
 Compare and swap is used designing concurrent algorithms. Basically, compare and swap compares an expected value to the
 concrete value of a variable, and if the concrete value of the variable is equals to the expected value, swaps the value of the variable
 for a new variable. Atomic operations are done via "compare and swap"/CAS mechanism.

 i.e.
 AtomicBoolean x = new AtomicBoolean(false);
 x.compareAndSet(false, true);//expect, update


 Amdahl's Law:
 ========================================
 Amdahl's law can be used to calculate how much a computation can be speed up by running part of it in parallel. Amdahl's law is named after
 Gene Amdahl who presented the law in 1967. The law is as follows:

 Total execution time = Total time of serial execution + Total time of parallel/concurrent execution
 T = S + P
 = S + (T - S)

 If there is N no of threads then -
 T(N) = S + (T - S) / N

 If T=1, S=0.4 (or 40%) then -
 T(1) = 0.4 + 0.6/1 = 1  So, performance increased = 1/1 = 1 = no increase
 T(2) = 0.4 + 0.6/2 = 0.4 + 0.3 = 0.7   So, performance increased = 1/0.7 = increased 1.42 fold
 T(5) = 0.4 + 0.6/5 = 0.4 + 0.1 = 0.5   So, performance increased = 1/0.5 = increased 2 fold

 From experiment, optimal no of Threads = Total no of cpu core
 (Or 2x the core if threads are doing blocking IO/network operations, you need to experiment with the no).

 */
