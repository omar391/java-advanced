package com.astronlab.tut.concurrency.basic.tasks;

/**
 * ThreadLocal test
 */
public class Answer3 {

	public static void main(String[] args) throws InterruptedException {
		//shared runnable instance
		MyRunnable printerRunnable = new Answer3().new MyRunnable();

		Thread threadA = new Thread(printerRunnable);
		Thread threadB = new Thread(printerRunnable);

		//Start threads
		threadA.start();
		threadB.start();

		//wait till both threads are finished
		threadA.join();
		threadB.join();

		//Now let print once again from main thread
		//From main(this) thread we have not set any threadLocal's value
		// (even though it's a shared variable and already used by other threads), so it will print "null"
		printerRunnable.printValues();
	}

	private class MyInteger {
		int x = 0;

		void set(int x1) {
			x = x1;
		}

		int get() {
			return x;
		}
	}

	private class MyRunnable implements Runnable{
		int i = 0;
		ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
		MyInteger nonThreadLocal = new Answer3().new MyInteger(); //nested class's instance

		@Override public void run() {

			//let threads execute following block sequentially to check whether
			// they overwrite each-other's value
			synchronized (this) {
				i++;
				threadLocal.set(i);
				nonThreadLocal.set(i);
			}

			//Now let the current thread to sleep(simulating a few work moments) till the other thread could come
			// in and overwrite a new value to our cross-thread shared variables (i/threadLocal/nonThreadLocal).
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//If the threadLocal was not working properly then both threads would print the same value(=2) (last overwritten one)
			//as our nonThreadLocal will print because it was overwritten by last thread
			printValues();
		}

		synchronized void printValues(){
			String threadName = Thread.currentThread().getName();
			System.out.println(threadName + "'s i value: " + i);
			System.out.println(threadName + "'s ThreadLocal value: " + threadLocal.get());
			System.out.println(threadName + "'s non ThreadLocal value: " + nonThreadLocal.get());
			System.out.println("=====================");
		}
	}
}
