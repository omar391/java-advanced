package com.astronlab.tut.concurrency.intermediate;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
*Builtin Lock class:
=========================================
 We know what lock(also known as: intrinsic lock) generally means(A state of monitor object to handle synchronization). But java also has built in"Lock"
 interface and its implementations (ie.ReentrantLock class) to handle synchronization without using "synchronized" keyword.
		i.e.

public int inc(){
	synchronized(this){
		count++;
	}
}

----is equivalent to-------

Lock myLock = new MyLock(); //this is just a simple custom implementation (i.e:We will use built-in ReentrantLock class, described below)
public int inc(){
	myLock.lock();
  count++;
	myLock.unlock();
}

 With a Lock class we are just mimicking the synchronize keywords's functionality via wait/notify along with some other extended features.

 A sample Lock implementation is exampled in MyLock class. Check it out!! It uses wait/notify mechanisms and almost
 resembles our previous WaitNotifierThree class.

*/
class MyLock {
	private boolean isLocked = false;

	public synchronized void lock() throws InterruptedException {
		while (isLocked) {
			wait();
		}
		isLocked = true;
	}

	public synchronized void unlock() {
		isLocked = false;
		notify();
	}
}

/**
  Nested Monitor Lockout
 =========================================
  When we use locks/monitors in nested-ly, we need to be cautious. Consider following LockTest class-

 - In the lock() method, "monitorObject.wait()" will release "monitorObject" lock but will continue to hold "this" lock
    till it could get out of wait state via monitorObject's notify from unlock() method.

 -  Unlock() method's synchronised block will wait for "this" lock however, our lock() method will already be locked with "this".

 So in above scenario, lock() method will be stuck forever with "this" lock. Hence, we should always use same lock for wait/notify,
 and synchronization while trying to avoid nested locks if possible.
 */

class LockTest {
	protected Object monitorObject = new Object();
	protected boolean isLocked = false;

	public void lock() throws InterruptedException {
		synchronized (this) {
			while (isLocked) {
				synchronized (monitorObject) {
					monitorObject.wait();
				}
			}
			isLocked = true;
		}
	}

	public void unlock() {
		synchronized (this) {
			this.isLocked = false;
			synchronized (monitorObject) {
				monitorObject.notify();
			}
		}
	}
}

/**
 Reentrant lock
 =====================
 If a code block enter the same lock(monitor) again then it is called lock's reentrance. Synchronization is
 by default reentrant. ie.

 synchronize(this){
  count++;
  synchronize(this){
    count++;
  }
 }
 If thread1 won first "this" lock then only it can hold the lock second time. This is called reentrance ability.

 Java has bulit-in ReentrantLock class. So we don't need to implement custom reentrant Lock class.

 Why use ReentrantLock class when we can just use a synchronize block:
 -----------------------------------------------------------------------
 With a ReentrantLock -

 1. We could configure thread fairness policy so that thread could be scheduled fairly.
 2. We could interrupt a thread (make forced exception to close the thread) which is waiting to acquire the lock.
 3. It is possible to attempt to acquire a lock without being willing to wait for it forever with "tryLock()". Means, try to
    own the lock and if not possible then do not wait.
 4. It is not limited by any "block region" like synchronization.


 Features of ReentrantLock class: (Follow ReentrantLockTest class)
 ------------------------------------------------------------------
 1. Fair lock:
 ~~~~~~~~~~~~~~~~
 ReentrantLock class can be used to prevent starvation issue(we will know in next part) via it's constructor
 i.e. new ReentrantLock(boolean isFair)

 2. lock()/unLock():
 ~~~~~~~~~~~~~~~~~~~
 This is used in place of "synchronised" keyword. We should always use unlock() in finally{..} block because if any exceptions are returned
 from the middle of locked code then it might not execute unlock(). Hence, other threads which might be waiting will forever be waiting.

 3. wait_notify capability/newCondition():
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 We could use wait_notifier like functionality with "lock.newCondition()" method.
 Equivalent methods are,
 object.wait() === condition.await()
 object.notify()/All() === condition.signal()/All()

 However, like monitor object, Conditions also face "spurious wake-up" problems. Checkout NestedLockTestWithRLock class.
 We just handled it with a while(..) block like our WaitNotifierThree class. We could also create a separate class if we want to like the
 WaitNotifierThree class. :)

 4. tryLock():
 ~~~~~~~~~~~~~
 Also known as - "timed and polled lock-acquisition". It tries to acquires the lock. If it fails to acquire the lock then just return
 with boolean "false" value and do not wait for the lock.

 Other variant: tryLock(long maximumWaitTime, TimeUnit unit), this method will wait the specified amount of time before it could own the lock.

 Usage:
 - Where we want to wait for a specific time or immediately
 - In deadlock(we will know in next part) prevention
 - We could solve above nested lock out problem

 5. lockInterruptibly():
 ~~~~~~~~~~~~~~~~~~~~~~~~~
 Simply, lock.lock() === lock.lockInterruptibly(), both methods works for locking.

 Except, if any thread call current thread's interrupt method then all the waiting thread locked via "lockInterruptibly()" will be interrupted(killed).

 6. Other methods:
 ~~~~~~~~~~~~~~~~~~~
 - getHoldCount() : Queries the number of holds on this lock by the current thread
 - isHeldByCurrentThread() : Queries if this lock is held by the current thread
 - isLocked() : Queries if this lock is held by any thread
 - isFair() : Checks whether if it is fair or not

 and other similar methods.

 Now checkout the Part6 class. We are done!!
 * */

public class Part7 {
	public static void main(String[] args) {
		final int threadCount = 3;
		final ReentrantLockTest reentrantLockTest = new ReentrantLockTest();

		Runnable sharedRunnable = new Runnable() {
			@Override public void run() {
				try {
					reentrantLockTest.lockUnlockTest();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		for (int i=0; i< threadCount; i++) {
			new Thread(sharedRunnable).start();
		}
	}
}

class NestedLockTestWithRLock {
	//we are experimenting with our above Nested lockout problem.
	//This time ThreadB will not be locked forever. However, ThreadA will be waiting as no one will be signalling/notifying it.

	public static void main(String[] args) {
		final ReentrantLockTest lockTest = new ReentrantLockTest();

		Thread threadA = new Thread(new Runnable() {
			@Override public void run() {
				try {
					lockTest.nestedLockTest();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"Thread-A");

		Thread threadB = new Thread(new Runnable() {
			@Override public void run() {
				try {
					lockTest.nestedUnLockTest();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"Thread-B");

		threadA.start();
		threadB.start();
	}
}

class ReentrantLockTest{
	ReentrantLock lock1 = new ReentrantLock();
	ReentrantLock lock2 = new ReentrantLock();

	//Conditions to handle await/signal (previously: wait/notify)
	Condition cond1 = lock1.newCondition();
	Condition cond2 = lock2.newCondition();

	private int count = 0;

	public void lockUnlockTest() throws InterruptedException {
		try {
			lock1.lock();
			System.out.println(Thread.currentThread().getName() + ": Lock acquired.");
			System.out.println("Processing...");
			for (int i = 0; i < 500; i++) {
				count++;
			}
			Thread.sleep(500);
			System.out.println("# Final value: "+ count);
		}finally {
			lock1.unlock();
		}
	}

	public boolean nestedLockTest() throws InterruptedException {
		// we are defining a stopTime
		long stopTime = System.nanoTime() + 5000;
		while (true) {
			if (lock1.tryLock()) {
				try {
					if (lock2.tryLock()) {
						try {
							boolean isTrulyWaked = false; //handle spurious wake ups
							while(!isTrulyWaked){
								cond2.await();
								isTrulyWaked = true;
							}

						} finally {
							lock2.unlock();
						}
					}

				} finally {
					lock1.unlock();
				}
			}
			if(System.nanoTime() > stopTime)
				return false;

			Thread.sleep(100);
		}
	}

	public boolean nestedUnLockTest() throws InterruptedException {
		// we are defining a stopTime
		long stopTime = System.nanoTime() + 5000;
		while (true) {
			if (lock1.tryLock()) {
				try {
					if (lock2.tryLock()) {
						try {
								cond1.signal();
						} finally {
							lock2.unlock();
						}
					}

				} finally {
					lock1.unlock();
				}
			}
			if(System.nanoTime() > stopTime){
				System.out.println(Thread.currentThread().getName()+": I am leaving...enough waiting");
				return false;
			}

			Thread.sleep(100);
		}
	}
}
