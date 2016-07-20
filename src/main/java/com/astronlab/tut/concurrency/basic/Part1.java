package com.astronlab.tut.concurrency.basic;

//Ways to create a thread
//========================
//
// 1. Via extending Thread class
// 2. Via anonymous thread class
// 3. Via Runnable interface
/*
* Notes:
* ------
* - Thread execution order is not guaranteed
* - Run the code multiple time to check the varying exec order
* - We must call "start()" method to init a thread
* - We could use thread names in thread constructors
* */

public class Part1 {
	private Part1(){}

	public static void main(String[] args) {

		//Way 1
		MyThread myThread = new MyThread(
				"Thread 1");//this could be without any constructor
		myThread.start();

		//Way 2
		Thread thread = new Thread() {
			@Override public void run() {
				System.out.println("Thread 2 running");
			}
		};
		thread.start();

		//Way 3
		thread = new Thread(new MyRunnable());
		thread.start();
	}

}

class MyThread extends Thread {
	public MyThread() {
		super();
	}

	public MyThread(String s) {
		super(s);
	}

	public void run() {
		System.out.println(this.getName() + " running");
	}
}

class MyRunnable implements Runnable {
	public void run() {
		System.out.println("Thread 3 running");
	}
}