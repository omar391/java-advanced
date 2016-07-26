package com.astronlab.tut.concurrency.basic;

/**
 Cross thread signaling: wait(), notify(), notifyAll()
 ======================================================
 When multiple threads become dependent on the completion of tasks among them then we need to implement
 some sort of mechanism to signal those threads to cause the dependant task to wait until precedent tasks are finished.

 A common way is to declare a global boolean flag and then continuously check whether the task is finished or not.
 i.e:
 boolean isInitialTaskRunning = true; //global

 Then in a thread check:
 while(isInitialTaskRunning){
 //do nothing //This is called busy waiting
 }
 //Do your dependent task now

 However, busy waiting doesn't make a very efficient utilization of the CPU. As an alternative we have java-builtin: wait/notify/notifyAll methods from Object class.

 Wait/Notify/NotifyAll
 ----------------------
 - wait() causes a thread to sleep and notify() wakes it up
 - The waiting thread would call wait() and the notifying thread would call notify()
 - notifyAll() method will wake all waiting/sleeping/inactive threads
 - wait/notify must be synchronized with same lock/monitor object
 - We will not use wait/notify directly but via a class (this is not compulsory but it make things easier) ie. WaitNotifierOne

 [ Class WaitNotifierOne ]
 - Waiting thread would call doWait() and notifying thread would call doNotify()
 - The moment we execute the line 21, calling thread goes to sleep and releases the lockObject so that other threads may use the lock
 - When another thread calls doNotify() or the moment line 50 is executed - waiting thread wakes up, acquires the lock and executes line 22.

 Problems
 -----------
 1. If we somehow call doNotify()/notify() before we call doWait/wait() then the waiting thread will always be
    in waiting state unless another notify call is executed. This is called "missed signal" problem. This issue is solved
    in WaitNotifierTwo class.
 2. Sometimes a waiting thread is get waked up without notify() method being called. This is a natural JVM phenomenon.
    Its called "Spurious Wake ups". This issue is handled in WaitNotifierThree class.

 Class "WaitNotifierThree" is our final waitNotifier class.

 A real-world wait/notify is exampled in Part5 class. Check it out, run it!! We are done!


 Thread Join
 =====================
 If we want to wait till a thread completely finish (While wait/notify handles modular dependencies) its life cycle the we will
 need to use threadInstance.join().

 i.e: Check out ThreadJoinTest class.

 Congratulation! We are done with this part!
 **/

class WaitNotifierOne {
	//We should not use following variables as lock/monitor objects
	// - Constant string (because jvm String pool only store single reference for identical string values)
	// - Global object (outside of this class)

	private Object lockObject = new Object();

	public void doWait() {
		synchronized (lockObject) {		/*Suppose its, Line 19*/
			try {		                    /*Line 20*/
				lockObject.wait();		    /*Line 21*/
			} catch (InterruptedException e) { /*Line 22*/
			}
		}
	}

	public void doNotify() {
		synchronized (lockObject) {
			lockObject.notify(); /*Suppose its, Line 50*/
		}
	}
}

class WaitNotifierTwo {
	private Object lockObject = new Object();
	private boolean resumeSignal = false; // A boolean flag is used to detect missed signal

	public void doWait() {
		synchronized(lockObject){
			//Now even if the notify() is called earlier without a pre-wait() call then it will check the condition
			//and will found "resumeSignal" to be true hence, will not enter the waiting state.
			if(!resumeSignal){
				try{
					lockObject.wait();
				} catch(InterruptedException e){}
			}
			//Waiting state is over. So, clear the signal flag and continue running.
			resumeSignal = false;
		}
	}

	public void doNotify() {
		synchronized (lockObject) {
			resumeSignal = true;
			lockObject.notify();
		}
	}
}

class WaitNotifierThree {
	private Object lockObject = new Object();
	private boolean resumeSignal = false;

	public void doWait() {
		synchronized(lockObject){
			//Changing if block into while block
			while(!resumeSignal){
				try{
					lockObject.wait();
					//If spurious wakeup happens then this following line will execute and return to while
					//condition check. If this was truly an non-intentional/spurious call then it will
					//find resumeSignal to be false and will return to waiting state again otherwise it will just
					//exit the waiting state. This solves our spurious wake up issue.
				} catch(InterruptedException e){}
			}

			resumeSignal = false;
		}
	}

	public void doNotify() {
		synchronized (lockObject) {
			resumeSignal = true;
			lockObject.notify();
		}
	}
}


public class Part5 {
	// Our goal is to make task3 in threadB wait before task1 from threadA finishes
	// So our task execution flow is: task1()/Thread A --> task3()/Thread B
	// Task2 and task3 will run parallelly as they are not dependent on each other
	//----
	//We will use a single wait-notifier here because each single dependency between two tasks group needs only one wait-notifier.
	//So if we need to make multiple task dependent(i.e. task A->B->C) among threads then we need two wait-notifier

	private WaitNotifierThree waitNotifier = new WaitNotifierThree();

	public static void main(String[] args) {
		Part5 p4 = new Part5();
		RunnableA runnableA = p4.new RunnableA();  //instance of child class(RunnableA) is called via parent's instance

		Thread threadA = new Thread(runnableA);
		Thread threadB = new Thread(p4.new RunnableB(runnableA));

		//now start threads
		threadA.start();
		threadB.start();
	}

	class RunnableA implements Runnable{
		int count = 0;
		@Override public void run() {
			task1();
			task2();
			System.out.println("From Thread A, Total count = " + count);
		}

		private void task1(){
			System.out.println("Task 1 started");
			for (int i = 0; i < 5000; i++) {
				if(i==4997){
					System.out.println(i);
				}
				count++;
			}
			waitNotifier.doNotify();
		}

		private void task2(){
			System.out.println("Task 2 started");
			for (int i = 0; i < 100000; i++) {
				count++;
			}
		}

		public int getCurrentCount(){
			return count;
		}
	}

	class RunnableB implements Runnable{
		final RunnableA runnableA;

		public RunnableB(RunnableA runnableA){
			this.runnableA = runnableA;
		}

		@Override public void run() {
			task3();
		}

		private void task3(){
			waitNotifier.doWait();
			System.out.println("Task 3 started");
			System.out.println("From Thread B, current count = " + runnableA.getCurrentCount());
		}
	}
}


class ThreadJoinTest{
	public static void main(String[] args) throws InterruptedException {
		Part5.RunnableA runnableA = new Part5().new RunnableA();
		Thread threadA = new Thread(runnableA);

		//now start the thread
		threadA.start();

		//Wait till threadA finishes via the following join line
		threadA.join();

		System.out.println("Printing from main thread, total count from thread A: "+ runnableA.getCurrentCount());

		//Now, try with commenting out the join() line. It may not wait till the thread finishes it's works!
	}
}