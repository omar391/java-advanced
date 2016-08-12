package com.astronlab.tut.concurrency.advance;

import java.util.concurrent.atomic.AtomicReference;

/**
  AtomicReference and its uses
 ====================================
 The AtomicReference class (like other similar classes, i.e: AtomicInteger) provides a wrapped reference
 object to be read and written atomically when accessed via multiple threads. Simply, with AtomicReference
 class we could use any object atomically.

 Checkout AtomicDemo1 class. It will explain it's usage.

 when to use AtomicReference
 ------------------------------
 //Suppose, I know nothing about the usage of the AtomicReference class
 //Now, write this section(+an demo example) based on answers from following url so that I could understand:
 //http://stackoverflow.com/questions/3964211/when-to-use-atomicreference-in-java
 //Checkout Binita Bharati's answer.
 */
class AtomicDemo1{
	public static void main(String[] args) {
		String initialRef = "initial value referenced";

		//We will use "initialRef" object atomically across multiple threads via AtomicReference class
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
