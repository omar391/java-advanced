package com.astronlab.tut.concurrency.basic.tasks;

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

public class Answer4 {
	// Our goal is to make task3 in threadB wait before task1 from threadA finishes
	// So our task execution flow is: task1()/Thread A --> task3()/Thread B
	// Task2 and task3 will run parallelly as they are not dependent on each other
	//----
	//We will use a single wait-notifier here because each single dependency between two tasks group needs only one wait-notifier.
	//So if we need to make multiple task dependent(i.e. task A->B->C) among threads then we need two wait-notifier
  WaitNotifierTwo waitNotifierTwo = new WaitNotifierTwo();
	WaitNotifierThree waitNotifier = new WaitNotifierThree();

	public static void main(String[] args) {
		Answer4 p4 = new Answer4();
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
			waitNotifierTwo.doWait();
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
			waitNotifierTwo.doNotify();
		}
	}
}
