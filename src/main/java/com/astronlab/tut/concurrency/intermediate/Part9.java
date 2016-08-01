package com.astronlab.tut.concurrency.intermediate;

import java.util.ArrayList;
import java.util.List;
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

public class Part9 {
}
