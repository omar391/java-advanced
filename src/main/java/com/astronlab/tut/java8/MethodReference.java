package com.astronlab.tut.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

/**
 A method reference is described using :: (double colon) symbol. A method reference can be used to point the following types of methods −

 Static methods
 Instance methods
 Constructors using new operator (TreeSet::new)

 First of all, a method reference can't be used for any method.
 They can only be used to replace a single-method lambda expression.

 Usecase:

 Instead of using
  - AN ANONYMOUS CLASS
 you can use
 - A LAMBDA EXPRESSION
 And if this just calls one method, you can use
 - A METHOD REFERENCE

 */
public class MethodReference {
	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(12,5,45,18,33,24,40);

		// Using an anonymous class
		Numbers.findNumbers(list, new BiPredicate<Integer, Integer>() {
			public boolean test(Integer i1, Integer i2) {
				return Numbers.isMoreThanFifty(i1, i2);
			}
		});

		// Using a lambda expression
		Numbers.findNumbers(list, (i1, i2) -> Numbers.isMoreThanFifty(i1, i2));

		// Using a method reference
		Numbers.findNumbers(list, Numbers::isMoreThanFifty);
	}

}

class Numbers {
	public static boolean isMoreThanFifty(int n1, int n2) {
		return (n1 + n2) > 50;
	}
	public static List<Integer> findNumbers(
			List<Integer> l, BiPredicate<Integer, Integer> p) {
		List<Integer> newList = new ArrayList<>();
		for(Integer i : l) {
			if(p.test(i, i + 10)) {
				newList.add(i);
			}
		}
		return newList;
	}
}