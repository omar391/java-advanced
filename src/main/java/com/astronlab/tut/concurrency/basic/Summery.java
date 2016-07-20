package com.astronlab.tut.concurrency.basic;

/*
-  Java included high-level concurrency APIs from JDK 5.0

Reading problem
---------------
 If a thread reads a memory location while another thread writes to it,
 - what value will the first thread end up reading?
 -- The old value?
 -- The value written by the second thread? 
 Or a value that is a mix between the two?

 Writing problem
----------------
 Or, if two threads are writing to the cognate memory location simultaneously,
 what value will be left when they are done? The value written by the first thread? 
 The value written by the second thread? Or a mix of the two values written?

 Multithreading Costs
 ---------------------
 -More complex design
  :: Code executed by multiple threads accessing shared data need special attention
 -Context Switching Overhead
  ::When a CPU switches from executing one thread to executing another, the CPU needs to save the local data, program pointer etc. of the current thread, and load the local data, program pointer etc. of the next thread to execute.
 -Increased Resource Consumption


 Furhter reads
 -----------------
 - http://tutorials.jenkov.com/java-concurrency/concurrency-models.html

 Models
 -----------
 Reactive, Event Driven Systems
Actors vs. Channels
 */
public class Summery {

}
