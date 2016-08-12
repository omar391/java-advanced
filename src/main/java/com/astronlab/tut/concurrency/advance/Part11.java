package com.astronlab.tut.concurrency.advance;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference and its uses
 * ====================================
 * The AtomicReference class (like other similar classes, i.e: AtomicInteger) provides a wrapped reference
 * object to be read and written atomically when accessed via multiple threads. Simply, with AtomicReference
 * class we could use any object atomically.
 * <p>
 * Checkout AtomicDemo1 class. It will explain it's usage.
 * <p>
 * when to use AtomicReference
 * ------------------------------
 * //Suppose, I know nothing about the usage of the AtomicReference class
 * //Now, write this section(+an demo example) based on answers from following url so that I could understand:
 * //http://stackoverflow.com/questions/3964211/when-to-use-atomicreference-in-java
 * //Checkout Binita Bharati's answer.
 */
class AtomicDemo1 {
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
	//this creats invalid state when we use individual AtmomicInteger variables

	/**
	 * Both setLower and setUpper are check-then-act sequences, but they do not use sufficient locking to make them
	 * atomic. If the number range holds (0, 10), and one thread calls setLower(5) while another thread calls
	 * setUpper(4), with some unlucky timing both will pass the checks in the setters and both modifications will be
	 * applied. The result is that the range now holds (5, 4)an invalid state.
	 */

	public class NumberRange {
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
	 * fixing of the above state using a AtomicReference instead of using individual AtomicIntegers
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
