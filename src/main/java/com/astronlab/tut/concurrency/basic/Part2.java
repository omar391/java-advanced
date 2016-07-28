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

 public synchronized void doX(){
	 count = 0;
	 count++;
 }

 ----Is equivalent to following one----

 public void doX(){
  synchronized(this){
	  count = 0;
	  count++;
  }
 }
	-------- OR equivalent ----------

 public void doX(){
 Object myObject = new Object();
 synchronized(myObject){
    count = 0;
    count++;
  }
 }


 Here, "this/myObject" is called a Monitor object. In a synchronized method, default Monitor is "this"/current_instance.
 With a monitor object we create a lock state for that object so that only one thread could pass that synchronize
 method/block at a time.

 Note:
 --------
 - In JVM, "every object" and "class" are logically associated with a monitor.
 - For objects, the associated monitor protects data locked on the object's instance variables. ie. Any object's instance
 - For classes,the monitor protects data locked on the class's class variables. ie. String.class


 Now run both NonSynchronizationDemo and SynchronizationDemo. NonSynchronizationDemo has mixed order of printing!
 While SynchronizationDemo prints complete block together from each thread, in other words it just executes thread's
 synchronized block one at a time for each thread.

 Notes (from SynchronizationDemo)
 --------------------------------
 - Synchronization "does not" execute the task orderly.
 - It sometime prints thread-1 before thread-0 and other time reversely. Run it multiple times to see the difference!
 - It just let synchronized blocks to be accessed by one thread at a time.


 Thread synchronization vs monitor vs lock (Important basic)
 ============================================================

 Monitor:
 ---------------
 Monitor is a generic (common in various language) concept in multi-threading scenarios. In Java, for example -

 Object myObject = new Object();
 synchronized(myObject){
  count = 0;
  count++;
 }

 Here "myObject" is a "Monitor object" which handles Synchronization. And
 {
 count = 0;
 count++;
 } is called a "monitor region".

 So a Monitor = monitor object + monitor region.


 Lock:
 -----------------
 A lock is a
 - flag/status/state of a monitor object
 - "hidden code block inside JVM" that cause the lock state.

 Suppose, in practical term, we are said: "Lock that door with a lock". So what is a lock?
 => Ans: Above, a lock refers to a hard object and it also refers to a bound state of a door

 Similarly, In threading -

 A lock is a flag/status/state done by a code block(hidden, we will know more, later) of the respective monitor object(above, the door).

 Here, in "synchronized(myObject){..}", when the code enters the monitor region then we could say - "monitor object is now locked".

 So finally, a synchronization mechanism is done by a lock(hidden code block) which makes a "lock" flag/status
 on a monitor object covering the monitor region so that only one thread could pass the monitor region at a time.

 Synchronization:
 -------------------
 Java's monitor supports two kinds of thread synchronization:
 1. Mutual exclusion : done via object lock
 2. Cooperation : done via wait/notify (we'll learn later in Part5-Part6)

 Mutual exclusion
 -----------------
 It is supported in the Java virtual machine via object locks, enables multiple threads to independently work on
 shared data without interfering with each other.

 Cooperation
 ------------
 In the Java virtual machine via the wait and notify methods of class Object, enables threads to work together towards a common goal.

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

		//Start threads
		threadA.start();
		threadB.start();
	}
}



public class Part2 {
}
