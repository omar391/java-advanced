package com.astronlab.tut.concurrency.intermediate;

/**

 Thread class and some important methods:
 ===========================================

 Static methods:
 -------------------
 1. int activeCount() : This method returns the number of active threads in the current thread's thread group.

 2. Thread currentThread() : This method returns a reference to the currently executing thread object.

 3. void dumpStack() : This method prints a stack trace of the current thread to the standard error stream.

 4.	boolean holdsLock(Object obj) : This method returns true if and only if the current thread holds the monitor lock on the specified object.

 5. boolean interrupted() : This method tests whether the current thread has been interrupted.

 6. void sleep(long millis) : This method causes the currently executing thread to sleep (temporarily cease execution) for the specified number of milliseconds, subject to the precision and accuracy of system timers and schedulers.


 General methods:
 -------------------
 1. void interrupt() : This method interrupts this thread and cause an exception.

 2. boolean isAlive() : This method tests if this thread is alive (run method is completely executed).

 3. boolean isDaemon() : This method tests if this thread is a daemon thread.

 4. boolean isInterrupted() : This method tests whether this thread has been interrupted.

 5. void join()/join(long millis) : Waits for this thread to die.

 6. void setDaemon(boolean on) : This method marks this thread as either a daemon thread(background threads) or a user thread.

 7. void setName(String name) : This method changes the name of this thread to be equal to the argument name.

 8. void setPriority(int newPriority) : This method changes the priority of this thread.



 Thread's Interruption:
 =====================================
 If any thread is in sleeping or in waiting state (i.e. sleep() or wait() is invoked), calling the interrupt() method on the thread,
 breaks out the sleeping or waiting state throwing InterruptedException. If the thread is not in the sleeping or waiting state,
 calling the interrupt() method performs normal behaviour and doesn't interrupt the thread but sets the interrupt flag to true.

 Lets see an example -


 */
public class Part6 implements Runnable {
	public void run() {
		try {
			System.out.println("in run() - about to work2()");
			work2();
			System.out.println("in run() - back from  work2()");
		} catch (InterruptedException x) {
			System.out.println("in run() - interrupted in work2()");
			return;
		}
		System.out.println("in run() - doing stuff after nap");
		System.out.println("in run() - leaving normally");
	}

	public void work2() throws InterruptedException {
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				System.out.println(
						"C isInterrupted()=" + Thread.currentThread().isInterrupted());
				Thread.sleep(2000);
				System.out.println(
						"D isInterrupted()=" + Thread.currentThread().isInterrupted());
			}
		}
	}

	public void work() throws InterruptedException {
		while (true) {
			for (int i = 0; i < 100000; i++) {
				int j = i * 2;
			}
			System.out.println(
					"A isInterrupted()=" + Thread.currentThread().isInterrupted());
			if (Thread.interrupted()) {
				System.out.println(
						"B isInterrupted()=" + Thread.currentThread().isInterrupted());
				throw new InterruptedException();
			}
		}
	}

	public static void main(String[] args) {
		Part6 si = new Part6();
		Thread t = new Thread(si);
		t.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException x) {
		}
		System.out.println("in main() - interrupting other thread");
		t.interrupt();
		System.out.println("in main() - leaving");
	}
}