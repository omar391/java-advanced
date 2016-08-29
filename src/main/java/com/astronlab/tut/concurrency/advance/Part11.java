package com.astronlab.tut.concurrency.advance;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
  AtomicReference and its uses
  ====================================
	  The AtomicReference class (like other similar classes, i.e: AtomicInteger) provides a wrapped for reference
	  objects to be read and written atomically when accessed via multiple threads. Simply, with AtomicReference
	  class we could use any object atomically.

  Checkout AtomicDemo1 class. It will explain it's usage.

 when to use AtomicReference
 ------------------------------
	 You can use AtomicReference when you have a shared object and you want to change it from more than 1 thread.

	1. You can create a copy of the shared object
	2. Modify the shared object
	3. You need to check that the shared object if still the same as before - if yes, then update with the reference of the modified copy.

 As other thread might have modified it(can modify) between first 2 steps. You need to do it in an atomic operation. This is where
 AtomicReference can help.

 Checkout our NumberRange class.
 */


class AtomicDemo1 {
	public static void main(String[] args) {
		String initialRef = "initial value referenced";

		//We will use "initialRef" object atomically across multiple threads via AtomicReference class
		AtomicReference<String> atomicStringRef = new AtomicReference<>(initialRef);

		String newRef = "new value referenced";

		//1. It checks if internally stored object is actually our "initialRef"
		//2. If true then "initialRef" is replaced by "newRef". Otherwise, does nothing and returns false.

		boolean exchanged = atomicStringRef.compareAndSet(initialRef, newRef);
		System.out.println("exchanged: " + exchanged);
		System.out.println("Cur value: " + atomicStringRef);

		exchanged = atomicStringRef.compareAndSet(initialRef, newRef);
		System.out.println("exchanged: " + exchanged);
		System.out.println("Cur value: " + atomicStringRef);
	}
}

public class Part11 {

	class NumberRange {
		/**
		 * Both setLower and setUpper are check-then-act sequences, but they do not use sufficient locking to make them
		 * atomic. If the number range holds (0, 10), and one thread calls setLower(5) while another thread calls
		 * setUpper(4), with some unlucky timing both will pass the checks in the setters and both modifications will be
		 * applied. The result is that the range now holds (5, 4)an invalid state.
		 *
		 * Hence, even though we used two AtomicIntger variables but whole process is still not atomic.
		 * Which might create invalid state.
		 */

		// INVARIANT: lower <= upper
		private final AtomicInteger lower = new AtomicInteger(0);
		private final AtomicInteger upper = new AtomicInteger(0);

		public void setLower(int i) {
			// Warning -- unsafe check-then-act
			if (i > upper.get()) {
				throw new IllegalArgumentException(
						"can't set lower to " + i + " > upper");
			}
			lower.set(i);
		}

		public void setUpper(int i) {
			// Warning -- unsafe check-then-act
			if (i < lower.get()) {
				throw new IllegalArgumentException(
						"can't set upper to " + i + " < lower");
			}
			upper.set(i);
		}

		public boolean isInRange(int i) {
			return (i >= lower.get() && i <= upper.get());
		}
	}

	/**
	 * Fixing of the above state using a AtomicReference instead of using individual AtomicIntegers
	 * for upper and lower bounds.
	 */

	public class CasNumberRange {

		private final AtomicReference<IntPair> values = new AtomicReference<>(
				new IntPair(0, 0));

		public int getLower() {
			return values.get().lower;
		}

		public int getUpper() {
			return values.get().upper;
		}

		public void setLower(int i) {
			while (true) {
				IntPair oldv = values.get();
				if (i > oldv.upper) {
					throw new IllegalArgumentException(
							"Can't set lower to " + i + " > upper");
				}
				IntPair newv = new IntPair(i, oldv.upper);
				if (values.compareAndSet(oldv, newv)) {
					return;
				}
			}
		}
		// similarly for setUpper
	}

	//immutable
	final class IntPair {

		final int lower;  // Invariant: lower <= upper
		final int upper;

		IntPair(int lower, int upper) {
			this.lower = lower;
			this.upper = upper;
		}
	}

}
