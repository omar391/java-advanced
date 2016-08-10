package com.astronlab.tut.concurrency.advance;

import java.util.concurrent.atomic.AtomicReference;

/**
  AtomicReference and its uses
 ====================================
 The AtomicReference class (like other similar classes, i.e: AtomicInteger) provides reference objects to be read and
 written atomically when accessed via multiple threads. Simply, with AtomicReference class we could use any object atomically.

 Checkout AtomicDemo1 class. It will explain it's usage.

 when to use AtomicReference
 ------------------------------
 //http://stackoverflow.com/questions/3964211/when-to-use-atomicreference-in-java
 */
class AtomicDemo1{
	public static void main(String[] args) {
		String initialRef = "initial value referenced";

		AtomicReference<String> atomicStringRef = new AtomicReference<>(initialRef);

		String newRef = "new value referenced";
		boolean exchanged = atomicStringRef.compareAndSet(initialRef, newRef);
		System.out.println("exchanged: " + exchanged);

		exchanged = atomicStringRef.compareAndSet(initialRef, newRef);
		System.out.println("exchanged: " + exchanged);
	}
}

public class Part11 {


}
