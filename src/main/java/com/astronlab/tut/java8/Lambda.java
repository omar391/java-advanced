package com.astronlab.tut.java8;

/**
 Source: https://www.tutorialspoint.com/java8/java8_lambda_expressions.htm

 Syntax
 =============
 A lambda expression is characterized by the following syntax −

 parameter -> expression body

 Characteristics of a lambda expression −

 Optional type declaration:
 Optional parenthesis around parameter:
 Optional curly braces:
 Optional return keyword:

Scope:
 - The body of a lambda expression has the same scope as a nested block.

 See the following Lambda test class
 */

public class Lambda {
	public static void main(String args[]){
		Lambda tester = new Lambda();

		//Java 7 format
		MathOperation addition7 = new MathOperation() {
			@Override public int operation(int a, int b) {
				return a+b;
			}
		};

		//Java 8 Lambda equivalents

		//with type declaration
		MathOperation addition = (int a, int b) -> a + b;

		//with out type declaration
		MathOperation subtraction = (a, b) -> a - b;

		//with return statement along with curly braces
		MathOperation multiplication = (int a, int b) -> { return a * b; };

		//without return statement and without curly braces
		MathOperation division = (int a, int b) -> a / b;

		System.out.println("10 + 5 = " + tester.operate(10, 5, addition));
		System.out.println("10 - 5 = " + tester.operate(10, 5, subtraction));
		System.out.println("10 x 5 = " + tester.operate(10, 5, multiplication));
		System.out.println("10 / 5 = " + tester.operate(10, 5, division));

		//without parenthesis
		GreetingService greetService1 = message ->
				System.out.println("Hello " + message);

		//with parenthesis
		GreetingService greetService2;
		greetService2 = (message) -> System.out.println("Hello " + message);

		greetService1.sayMessage("Mahesh");
		greetService2.sayMessage("Suresh");
	}

	interface MathOperation {
		int operation(int a, int b);
	}

	interface GreetingService {
		void sayMessage(String message);
	}

	private int operate(int a, int b, MathOperation mathOperation){
		return mathOperation.operation(a, b);
	}
}
