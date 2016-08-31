package com.astronlab.tut.concurrency.advance;

/*
	This is a comprehensive short overview of thread's utils, classes and their
  intended use cases.
 */


/**
 Thread vs Executor Service vs ForkJoin pool
 ============================================
 1. We may use raw Thread class for trivial single thread task (not-recommended)
 2. However, most of the time (single or multi-task) we should use Executor Service
 3. For recursive task(ie. fibonacci series, merge sort, recursively splitting file contents etc) we
   should use ForkJoin pool


 Synchronised block/methods vs Reentrant Lock
 ==============================================
 1. In most of the case we could use plain synchronised block
 2. But if we need to sort out deadlock, wait-notify , fairness in thread scheduling
    then we will use Reentrant lock instead


 Synchronised block/methods vs Atomic variable/reference
 =========================================================
 1. If we have opportunity to use atomic variable the we will
    use Atomic variable/references instead of synchronised block


 Volatile variable
 ===========================
 1. We will always use Atomic variable for visibility instead of volatile one
 2. If we must use volatile variable then the only safe use contexts are:
  - Only 1 thread will write
  - Other threads will read only


 Synchronised lock vs Semaphore
 =================================
 1. If we need single lock at a time then we will use synchronised block
 2. If we need multiple lock at a time we will use Semaphore class


 CountDownLatch vs Phaser
 =============================
 1. CountDownLatch is used for fixed counter
 2. Phaser is used for dynamic counter


 ConcurrentMap vs HashMap
 =========================
 1. We will use ConcurrentMap in multi threaded context
 2. HashMap should be used in single-threaded/immutable context

 * */
public class Summery {
}
