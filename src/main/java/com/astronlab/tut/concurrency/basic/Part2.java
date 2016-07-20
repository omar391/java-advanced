package com.astronlab.tut.concurrency.basic;

/**
Java threading model introduce some problem domain, such as -

 Reading problems
 -----------------
 If a thread reads a memory location while another thread writes to it,
 - what value will the first thread end up reading?
 -- The old value?
 -- The value written by the second thread?
 Or a value that is a mix between the two?

 Writing problems
 ------------------
 Or, if two threads are writing to the cognate memory location simultaneously,
 what value will be left when they are done? The value written by the first thread?
 The value written by the second thread? Or a mix of the two values written?


 So we could solve above problem by allowing only one thread to access our code block at a time!

 Synchronization: Introduction <we will know more later>
 ========================================================
 Synchronization refers to a mechanism to execute only one task/code_block at a time. Synchronization is implemented with
 "synchronized" keyword. It could only be applied on methods or a code block but not on any variable.
 i.e.

 public synchronized void doX(){..}
 --OR--
 synchronized(this){
  //...
 }

 Now run both NonSynchronizationDemo and SynchronizationDemo. NonSynchronizationDemo has mixed order of printing!
 While SynchronizationDemo prints complete block together from each thread, in other words it just executes thread's
 synchronized block one at a time for each thread.

 Notes (from SynchronizationDemo)
 --------------------------------
 - Synchronization "does not" execute the task orderly.
 - It sometime prints thread-1 before thread-0 and other time reversely. Run it multiple times to see the difference!
 - It just let synchronized blocks to be accessed by one thread at a time.

 */

class NonSynchronizationDemo{

	public static void main(String[] args){
		Runnable printerRunnable = new Runnable() {
			@Override public void run() {
				String name = "From " + Thread.currentThread().getName();

				for (int i = 0; i < 100; i++) {
					System.out.println(name+" ===> "+ i);
				}
			}
		};

		Thread threadA = new Thread(printerRunnable);
		Thread threadB = new Thread(printerRunnable);

		//Start thread
		threadA.start();
		threadB.start();
	}
}

class SynchronizationDemo{

	public static void main(String[] args){
		Runnable printerRunnable = new Runnable() {
			@Override public void run() {
				String name = "From " + Thread.currentThread().getName();

				synchronized (this){
					for (int i = 0; i < 100; i++) {
						System.out.println(name+" ===> "+ i);
					}
				}
			}
		};

		Thread threadA = new Thread(printerRunnable);
		Thread threadB = new Thread(printerRunnable);

		//Start thread
		threadA.start();
		threadB.start();
	}
}



public class Part2 {
}
