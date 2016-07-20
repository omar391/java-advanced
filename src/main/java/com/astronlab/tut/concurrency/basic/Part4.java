package com.astronlab.tut.concurrency.basic;

/*
Java memory to cpu communication
=====================================

 Memory structure inside jvm:
 -----------------------------
 There are two types of memory space inside JVM -

 1. Thread's stack/space <store "only  method's" local primitive variables and reference of heap objects> : Each thread contains its own version of same local variable
 2. Heap space <store actual objects, static class/objects> : This space is shared between threads

 i.e: jvm[ thread_stack[int, char, byte, etc],  heap[class_obj, enum] ]

 Memory to CPU communication flow:
 ---------------------------------
 [Main memory/RAM] ---> cpu[ [CPU cache (L1, L2)] --> [CPU register] ]  //L1,L2 refers to layer 1, layer 2 etc

 Different hardware memory storage-size comparison:
 --------------------------------------------------
	main_memory size > cpu_cache size > cpu_register size

 Communication example:
 ----------------------
 count = count + 1 // OR count++;

 1. Read count's value from main_memory to cpu_cache
 2 From cpu_cache to cpu_register
 3. Add 1 value to count variable in resister
 4. Store value from register to cpu cache
 5. Store value from cpu_cache to main_memory

* If things are not clear then check the ref bellow / Ask me
Ref: http://tutorials.jenkov.com/java-concurrency/java-memory-model.html

 The problem:
 --------------
 Exact execution times of steps 1 or 5 are not guaranteed.
 Read-write from/to cpu_cache<-->main_memory can happen immediately or sometime later.
 Hence, threads might not see each other's changes immediately. This is called visibility problem.


Volatile variable
=====================================
By declaring a variable volatile all writes to that variable will be written back to main memory immediately.
Also, all reads of the counter variable will be read directly from main memory. Declaring a variable volatile
thus solve visibility issue.

Syntax: public volatile int counter = 0;

Notes:
------
1. Volatile variable doesn't prevent harmful race conditions. Because volatile merely make changes visible
to threads but it doesn't cause them to access shared variable sequentially (Remember: count=5; count++; problem)

Example: Try changing CriticalSection's "int count = 0" into "volatile int count = 0" in class Part3 then run the class.
It will detect critical harmful race conditions!

2. When a thread writes to a volatile variable, then not just the volatile variable itself is written to main memory.
Also all other variables changed by the thread before writing to the volatile variable are also flushed to main memory.
When a thread reads a volatile variable it will also read all other variables from main memory which were flushed to
main memory together with the volatile variable.

3. Sample use-case: Using a global(shared) volatile boolean flag variable and update it from multiple threads.
 So all threads will see the updated changes.


ThreadLocal class
======================
The ThreadLocal class in Java enables you to create variables that can only be read and written by the same thread.
Thus, even if two threads are executing the codes with shared reference to a ThreadLocal variable,
then the two threads can not see the value of each other's ThreadLocal variables.

Syntax: private ThreadLocal myThreadLocal = new ThreadLocal();
 */
public class Part4 {
}

class VolatileTest {
	private volatile static int MY_INT = 0;

	public static void main(String[] args) {
		new ChangeListener().start();
		new ChangeMaker().start();
	}

	static class ChangeMaker extends Thread{
		@Override
		public void run() {

			int local_value = MY_INT;
			while (MY_INT <5){
				System.out.println("Incrementing MY_INT to "+ (local_value+1));
				MY_INT = ++local_value;

				//Using thread sleep to get the print output in order
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
	}

	static class ChangeListener extends Thread {
		@Override
		public void run() {
			int local_value = MY_INT;
			while ( local_value < 5){
				if(local_value!= MY_INT){
					System.out.println("Got Change for MY_INT : "+ MY_INT);
					local_value= MY_INT;
				}

				//-If we remove volatile keyword and comment out following thread-sleep block then the above while block
				// will keep the jvm busy and it will run forever without finishing
				//- But If we un-comment thread-sleep block then we could get it working without volatile variable
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
	}
}