package com.astronlab.tut.concurrency.intermediate;

import java.util.concurrent.CountDownLatch;

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


 CountDownLatch:
 =======================================
 It is used when we want to wait for more than one thread to complete its task. It is similar to "join()"
 in threads.

 Consider a scenario where we have three threads "A", "B" and "C" and we want to start thread "C" only
 when "A" and "B" threads completes or partially completes their task.

 Checkout our SimpleCountDownLatchExample class.


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

class SimpleCountDownLatchExample {
	static int N = 4;

	public static void main(String args[]) throws InterruptedException {
		CountDownLatch doneSignal = new CountDownLatch(N);

		for (int i = 0; i < N; ++i) // create and start threads
		{
			new Thread(new SimpleCountDownLatchExample().new WorkerRunnable(doneSignal)).start();
		}

		doneSignal.await();           // wait for all to finish
		System.out.println("finished");
	}

	 class WorkerRunnable implements Runnable {
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

public class Part10 {
}
