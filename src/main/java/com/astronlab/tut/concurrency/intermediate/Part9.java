package com.astronlab.tut.concurrency.intermediate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
Java ReadWriteLock class:
===========================
 If multiple threads are accessing an object for reading data, it does not make sense to use a synchronized block or any other
 mutually exclusive locks (ReentrantLock) as reading by multiple thread concurrently cause no problem like witting does.

 The ReadWriteLock offers two main methods
 - Lock readLock() :  acquire read-Lock
 - Lock writeLock() :acquire the write-Lock

 ReadWriteLock(interface) is implemented by ReentrantReadWriteLock Class. Multiple Threads can acquire multiple read Locks, but only a single Thread can acquire
 mutually-exclusive write Lock. If lock is used in writing then other threads requesting readLocks have to wait till the write Lock is released. We can
 update write lock to read lock but not vice-versa. Allowing a read thread to upgrade would lead to a deadlock as more than one thread can try to upgrade
 its lock. The ReentrantReadWriteLock also supports all the features of the Reentrant lock like providing fair mechanism , reentrantLocks, Condition
 Support (on a write Lock only), allowing interruption on read as well as write Locks.

 Checkout ThreadSafeArrayList class and run it!
 */

class ThreadSafeArrayList<E>
{
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private final Lock readLock = readWriteLock.readLock();

	private final Lock writeLock = readWriteLock.writeLock();

	private final List<E> list = new ArrayList<>();

	public static void main(String[] args)
	{
		final ThreadSafeArrayList<Integer> threadSafeArrayList = new ThreadSafeArrayList<>();
		final AtomicInteger counter = new AtomicInteger(0);

		Runnable rwRunnable = new Runnable() {
			@Override public void run() {
				for (int i = 0; i < 5; i++) {
					threadSafeArrayList.set(counter.incrementAndGet());
				}

				for (int i = 0; i < threadSafeArrayList.size(); i++) {
					threadSafeArrayList.get(i);
					//System.out.println("Reading value : "+threadSafeArrayList.get(i));
				}
			}
		};

		new Thread(rwRunnable, "Thread-A").start();
		new Thread(rwRunnable, "Thread-B").start();

		//On multiple run, what we see here is: 2 write prints pair are never mixed while 2 read prints pair are often mixed in console because writes are
		//synchronised while reads are not. We just maintain the readLock to prioritize the write lock but not to synchronise the read access.
	}

	private int size() {
		return this.list.size();
	}

	public void set(E o)
	{
		writeLock.lock();
		try
		{
			list.add(o);
			System.out.println("Adding element by thread: "+Thread.currentThread().getName());
			System.out.println("# write: "+o);

		}
		finally
		{
			writeLock.unlock();
		}
	}

	public E get(int i)
	{
		readLock.lock();
		try
		{
			System.out.println("Reading elements by thread: "+Thread.currentThread().getName());
			System.out.println("$ read: "+list.get(i));
			return list.get(i);
		}
		finally
		{
			readLock.unlock();
		}
	}
}


/**
	Java "Semaphore" and its usage:
 ===================================
 Like "monitor/intrinsic lock/mutex"(or synchronised block), semaphore is also a generic term used in computer
 science to denote access restriction on an object. Both monitor and semaphore use object locks. However,
 monitor only allow one thread to pass its region(=one active lock) while a semaphore allows maximum
 N-threads to lock an object simultaneously.
 So -
 A monitor : Allows one active lock at a time
 A semaphore : Allows maximum N active lock at a time

 Semaphore is useful in different scenarios where you have to limit the amount concurrent access to certain
 parts of your application.

 Java has built-in Semaphore class. It has following features:
 - Constructor takes no of maximum allowed active lock/threads via "public Semaphore(int permits)"
 - It can be used as a Fair scheduler like ReentrantLock class
 - It has acquire(), tryAcquire(), release() methods equivalent to
   lock(), tryLock(), unlock() methods from ReentrantLock class

 Lets checkout the SimpleSemaphoreTest class to see it in action!
 * */

class SimpleSemaphoreTest implements Runnable {
	Semaphore semaphore = new Semaphore(3);

	public static void main(String[] args) {
		final int threadCount = 10;
		final SimpleSemaphoreTest semaphoreTest = new SimpleSemaphoreTest();

		for (int i = 0; i < threadCount; i++) {
			new Thread(semaphoreTest).start();
		}

		//Check the output, it acquires initial 5 locks at once. If it were a normal mutex/synchronised
		//block then it would only acquire one lock at a time.
		//Change "new Semaphore(5)" into "new Semaphore(1)" to see the effect.
	}

	@Override public void run() {
		boolean isPermitted = false;
		try {
			isPermitted = semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
			if (isPermitted) {
				System.out.println(Thread.currentThread().getName()+": Semaphore acquired");
				Thread.sleep(500);

			} else {
				System.out.println("Could not acquire semaphore");
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			if (isPermitted) {
				System.out.println(Thread.currentThread().getName()+": Semaphore released");
				semaphore.release();
			}
		}
	}
}

/**

 Java BlockingQueue and its implementations:
 =============================================
 Remember our Producer-Consumer problem. When producer reached its production limit then it would hold and similarly
 consumer would also stop if it was empty. Java have following implementations for BlockingQueue interface:

 - ArrayBlockingQueue    : A bounded BlockingQueue backed by an array. This queue orders elements in FIFO style.

 - DelayQueue            : DelayQueue blocks the elements internally until a certain delay has expired.

 - LinkedBlockingQueue   : The LinkedBlockingQueue keeps the elements internally in a linked structure (linked nodes).

 - PriorityBlockingQueue : All elements inserted into the PriorityBlockingQueue must implement the Comparable interface.
                          The elements thus order themselves according to whatever priority you decide in your Comparable implementation.

 - SynchronousQueue      : The SynchronousQueue is a queue that can only contain a single element internally.
                           A thread inserting an element into the queue is blocked until another thread takes that element from the queue.


 Checkout BlockingQueueExample class for an example usage of ArrayBlockingQueue class.
 */

class BlockingQueueExample {
	public static void main(String[] args) throws Exception {
		BlockingQueue bq = new ArrayBlockingQueue(1000);//1000: capacity

		Producer producer = new Producer(bq);
		Consumer consumer = new Consumer(bq);

		new Thread(producer).start();
		new Thread(consumer).start();
	}
}

class Producer implements Runnable {
	private BlockingQueue bq = null;

	public Producer(BlockingQueue queue) {
		this.bq = queue;
	}

	public void run() {
		Random rand = new Random();
		int res = 0;
		try {
			res = addition(rand.nextInt(100), rand.nextInt(50));
			System.out.println("Produced: " + res);
			bq.put(res);//consumer is already waiting for this
			Thread.sleep(500);

			res = addition(rand.nextInt(100), rand.nextInt(50));
			System.out.println("Produced: " + res);
			bq.put(res);//consumer is already waiting for this
			Thread.sleep(500);

			res = addition(rand.nextInt(100), rand.nextInt(50));
			System.out.println("Produced: " + res);
			bq.put(res);//consumer is already waiting for this

			//Make it wait bit longer, so the consumer will also wait 2000ms
			Thread.sleep(2000);
			res = addition(rand.nextInt(100), rand.nextInt(50));
			System.out.println("Produced: " + res);
			bq.put(res);//consumer is already waiting for this
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int addition(int x, int y) {
		int result = 0;
		result = x + y;
		return result;
	}
}

class Consumer implements Runnable {
	protected BlockingQueue queue = null;

	public Consumer(BlockingQueue queue) {
		this.queue = queue;
	}

	public void run() {
		try {
			System.out.println("Consumed: " + queue.take());
			System.out.println("Consumed: " + queue.take());
			System.out.println("Consumed: " + queue.take());
			System.out.println("Consumed: " + queue.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class Part9 {
}