package com.astronlab.tut.concurrency.intermediate;

/**
 Following topics are only for acquaintance purposes. These will help you in interviews.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 ConcurrentMap / ConcurrentHashMap:
 ========================================
 The ConcurrentHashMap is very similar to the HashTable class. It has following features:

 - Capable of handling concurrent access (puts and gets) to it.
 - ConcurrentHashMap offers better concurrency than HashTable does.
 - ConcurrentHashMap does not lock the Map while you are reading from it.
 - ConcurrentHashMap does not lock the entire Map when writing to it.
 - It only locks the part of the Map that is being written to, internally.
 - It does not throw ConcurrentModificationException if the ConcurrentHashMap is changed while being iterated.

 CountDownLatch:
 =================================
 It is used when we want to wait for more than one thread to complete its task. It is similar to "join()"
 in threads.

 Consider a scenario where we have three threads "A", "B" and "C" and we want to start thread "C" only
 when "A" and "B" threads completes or partially completes their task.

 Pseudo code can be:

 - Main thread starts
 - Create CountDownLatch for N threads
 - Create and start N threads
 - Main thread waits on latch
 - N threads completes there tasks are returns
 - Main thread resume execution

 */
public class Part10 {
}
