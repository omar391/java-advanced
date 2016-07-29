package com.astronlab.tut.concurrency.basic.tasks;

/**
 * Ans:
 *
 * Synchronization :
 * ---------------------
 * Synchronization access/releases lock on monitors which force only one thread at a time to execute a code block.
 *
 * Atomic variable:
 * -------------------
 * While atomic integer doesn't uses lock, it is entirely possible to synchronize only some accesses to a variable
 * and allow other accesses to be un-synchronized (e.g., synchronize all writes to a variable but none of the reads from it).
 * atomic operation is more faster than synchronization.
 *
 * Volatile variable:
 * --------------------
 * A Volatile variable modify its value immediately and update it in main memory. Unlike above two, it only solve the visibility issues without preventing
 * that race condition that might harm the application.
 *
 */
public class Answer1 {
}
