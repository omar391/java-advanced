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


 Synchronization: Introduction <we will know more later>
 ========================================================

 Atomic variable
 =====================

 Semaphore
 =====================
 */
public class Part2 {
}
