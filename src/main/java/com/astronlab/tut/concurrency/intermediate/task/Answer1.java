package com.astronlab.tut.concurrency.intermediate.task;

public final class Answer1 implements Runnable {
	private int count = 0;

	@Override public synchronized void run() {
		try {
			work();
		} catch (Exception e) {
			return;
		}
	}

	public void work() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			count++;
			System.out.println("value:" + count);
			if (Thread.interrupted()) {
				System.out.println("Thread Interrupted at----------------" + count);
				throw new InterruptedException();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Answer1 c = new Answer1();
		Thread thread = new Thread(c);
		thread.start();
		Thread.sleep(1);
		thread.interrupt();
	}
}