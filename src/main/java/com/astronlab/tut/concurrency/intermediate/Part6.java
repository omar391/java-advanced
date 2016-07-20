package com.astronlab.tut.concurrency.intermediate;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
Thread's Deadlock example and prevention
==========================================
 A deadlock is when two or more threads are waiting to obtain locks that some of the other threads
 in the deadlock are holding. The situation is illustrated below:

 Thread 1  locks with obj A, waits to hold another lock with obj B
 Thread 2  locks with obj B, waits to hold another lock with obj A

 So Deadlock "might" occur when multiple threads hold(or try to hold) same locks set in reverse order.
 Checkout our DeadlockExample class.

 Prevention
 ------------
 So what do you think how can we solve our little deadlock problem above? Well, its obvious that if we hadn't
 make locks in reverse order in both threads then it would be working just fine.

 And following actions can be taken to prevent probable deadlocks -

 1. Lock order: We should try to use locks in threads in same order.
 2. Lock Timeout: We shouldn't wait for a lock forever rather sleep it for a time.
    If it doesn't come out of that lock yet then we need to release all hold locks and then retry it few  times later.
 3. Lock Only What is Required: Lock only on lines which absolutely require locks. We don't need to lock complete block.
 4. Avoid nested lock: If possible we should always avoid nested locks.

 Checkout our DeadlockPrevention class bellow.


 Starvation and Fairness
 =================================
 If a thread is not granted CPU time because other threads grab it all, it is called "starvation".

 Causes of Starvation in Java
 -----------------------------
 1. Threads with high priority(we can set a thread's priority between 1 and 10) swallow all CPU time from threads with lower priority.
 2. Threads are blocked indefinitely waiting to enter a synchronized block, because other threads are constantly allowed access before it.
 3. Threads waiting on an object (called wait() on it) remain waiting indefinitely because other threads are constantly awakened instead of it.

 Checkout and run StarvationDemo class.

 Implementing fairness
 ----------------------
 Starvation is controlled by System's thread schedulers. So it is not possible to implement 100% fairness. But we can increase
 fairness by using inbuilt ReentrantLock class.

 Checkout FairnessDemo class and run it!! We are done!

 */

class DeadlockExample {
		private static Object Lock1 = new Object();
		private static Object Lock2 = new Object();

		public static void main(String args[]) {

			ThreadDemo1 T1 = new ThreadDemo1();
			ThreadDemo2 T2 = new ThreadDemo2();
			T1.start();
			T2.start();
		}

		private static class ThreadDemo1 extends Thread {
			public void run() {
				synchronized (Lock1) {
					System.out.println("Thread 1: Holding lock 1");

					//With forced "Thread.sleep(10)" we are trying to simulate that thread is working on something for 10 milliseconds.
					//This let us to demonstrate the deadlock scenario better.
					try { Thread.sleep(10); }
					catch (InterruptedException ignored) {}

					System.out.println("Thread 1: Waiting for lock 2");
					synchronized (Lock2) {
						System.out.println("Thread 1: Holding lock 1 & 2");
					}
				}
			}
		}

		private static class ThreadDemo2 extends Thread {
			public void run() {
				synchronized (Lock2) {
					System.out.println("Thread 2: Holding lock 2");

					try { Thread.sleep(10); }
					catch (InterruptedException ignored) {}

					System.out.println("Thread 2: Waiting for lock 1");
					synchronized (Lock1) {
						System.out.println("Thread 2: Holding lock 1 & 2");
					}
				}
			}
		}
	}

class DeadlockPrevention {
	private static Object Lock1 = new Object();
	private static Object Lock2 = new Object();

	public static void main(String args[]) {

		ThreadDemo1 T1 = new ThreadDemo1();
		ThreadDemo2 T2 = new ThreadDemo2();
		T1.start();
		T2.start();
	}

	private static class ThreadDemo1 extends Thread {
		public void run() {
			synchronized (Lock1) {
				System.out.println("Thread 1: Holding lock 1");

				try { Thread.sleep(10); }
				catch (InterruptedException ignored) {}

				System.out.println("Thread 1: Waiting for lock 2");
				synchronized (Lock2) {
					System.out.println("Thread 1: Holding lock 1 & 2");
				}
			}
		}
	}

	private static class ThreadDemo2 extends Thread {
		public void run() {

			//just locks orders are changed, it works now!
			synchronized (Lock1) {
				System.out.println("Thread 2: Holding lock 2");

				try { Thread.sleep(10); }
				catch (InterruptedException ignored) {}

				System.out.println("Thread 2: Waiting for lock 1");
				synchronized (Lock2) {
					System.out.println("Thread 2: Holding lock 1 & 2");
				}
			}
		}
	}
}

class StarvationDemo {
	private static Object sharedObject = new Object();


	public static void main (String[] args) {
		JFrame frame = createFrame();
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));

		for (int i = 0; i < 5; i++) {
			ProgressThread progressThread = new ProgressThread();
			frame.add(progressThread.getProgressComponent());
			progressThread.start();
		}

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static JFrame createFrame () {
		JFrame frame = new JFrame("Starvation Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(300, 200));
		return frame;
	}

	private static class ProgressThread extends Thread {
		JProgressBar progressBar;

		ProgressThread () {
			progressBar = new JProgressBar();
			progressBar.setString(this.getName());
			progressBar.setStringPainted(true);
		}

		JComponent getProgressComponent () {
			return progressBar;
		}

		@Override
		public void run () {

			int c = 0;
			while (true) {
				//We are using a reentrantLock rather than Synchronized block to ensure fairness
				synchronized (sharedObject) {

					if (c == 100) {
						break;
					}
					progressBar.setValue(++c);
					try {
						//sleep the thread to simulate long running task
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}
}

class FairnessDemo {
	private static ReentrantLock reentrantLock = new ReentrantLock(true);


	public static void main (String[] args) {
		JFrame frame = createFrame();
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));

		for (int i = 0; i < 5; i++) {
			ProgressThread progressThread = new ProgressThread();
			frame.add(progressThread.getProgressComponent());
			progressThread.start();
		}

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static JFrame createFrame () {
		JFrame frame = new JFrame("Fairness Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(300, 200));
		return frame;
	}

	private static class ProgressThread extends Thread {
		JProgressBar progressBar;

		ProgressThread () {
			progressBar = new JProgressBar();
			progressBar.setString(this.getName());
			progressBar.setStringPainted(true);
		}

		JComponent getProgressComponent () {
			return progressBar;
		}

		@Override
		public void run () {

			int c = 0;
			while (true) {
				//We are using a reentrantLock rather than Synchronized block to ensure fairness
				reentrantLock.lock();

					if (c == 100) {
						break;
					}
					progressBar.setValue(++c);
					try {
						//sleep the thread to simulate long running task
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					reentrantLock.unlock();
			}
		}
	}
}

public class Part6 {
}
