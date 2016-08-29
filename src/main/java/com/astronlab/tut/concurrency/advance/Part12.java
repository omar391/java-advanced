package com.astronlab.tut.concurrency.advance;

import com.astronlab.tut.utils.http.HttpInvoker;
import com.astronlab.tut.utils.TimeAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
	Executor service and Thread pool
 =========================================
 Up until now, we were using raw/low-level "Thread" class and it's "start" method to start and execute a
 runnable tasks. However, in production scenarios we will use high-level "Executor" class to
 maintain multiple threads via Thread pool(=threads collection) mechanism.

 Let's see an example first:

 Runnable task = new MyRunnable();
 Thread thread = new Thread(task);
 thread.start();

 ~~~~~~~~~~ IS EQUIVALENT TO ~~~~~~~~~~

 ExecutorService executor = Executors.newFixedThreadPool(8); //8=Max concurrent worker(non-main) threads
 Runnable task = new MyRunnable();
 executor.execute(task);


 Advantage of Executors( vs threads):
 -------------------------------------
 - Light on cpu/ram and reuses threads: Its costly to create a new thread every time to process a task.
 - Makes managing threads easy and less error prone.
 - Graceful shutting down of threads can be achieved via Executor shutdown.


 Initiation of executor thread pool:
 ------------------------------------
 ExecutorService(or Executor) can be created by following static methods.However, we could also use constructors from
 ExecutorService class.

 i.e.
 1. ExecutorService es = Executors.newFixedThreadPool(10); //Set fixed amount of thread to be run simultaneously.

 Similarly via:
 2. Executors.newSingleThreadExecutor(); //Just start a single task via a single thread at a time
 3. Executors.newScheduledThreadPool(10); // If we need to start our tasks after certain period of time
 4. Executors.newWorkStealingPool();  //Use Fork-join framework model (we'll learn later in this part)
 5. Executors.newCachedThreadPool(); //Use one thread for each task, all at once. Normally, it should be avoided.

 and a few others.


 Starting task vai ExecutorService:
 -------------------------------------
 There are a few different ways to delegate/distribute tasks for execution to an ExecutorService:
 ie. ExecutorService es = Executors.newFixedThreadPool(10);

 1. es.execute(Runnable); //just execute/start the runnable task; returns void

 2. es.submit(Runnable); //Return "Future" class's instance
 ie.
 Future future = es.submit(Runnable);
 future.get(); //It will cause the thread to wait till the submitted task is finished (Similar to: thread.join() )

 3. es.submit(Callable); //Accept Callable interface(Equivalent to Runnable but return result from call method) and return Future object
 ie.
 Callable myCallable = new Callable(){
	 public Object call() throws Exception {
      //Do task here
		 return "done!";
	 }
 }
 Future future = es.submit(myCallable);
 future.get(); //Waits and returns "done!" result


 Special methods:
 --------------------
 Following methods do our common tasks more concisely for convenience. ie.

 ExecutorService es = Executors.newFixedThreadPool(10);
 List<Callable> callableList = new ArrayList();//Suppose, its our source callable/tasks List

 1. es.invokeAll(callableList);
 - Executes all the callable-s/tasks for us, wait till all the tasks finish their execution and then return list of
 "Future" objects for us. So its an all-in-one package for us to get the tasks done.

 2. es.invokeAny(callableList);
 - Executes and returns the first completed task and cancel others on-return.


 ExecutorService shutdown:
 ----------------------------
 Following ways we could shutdown a executor pool when we have done our work.

 1.  es.shutdown();
 - Initiates an orderly shutdown in which previously submitted tasks are executed
 - Wait for active tasks but cancel waiting tasks
 - No new tasks will be accepted by ExecutorService after this command.

2. es.shutdownNow(); //Returns List<Runnable>
 - Attempts to stop all actively executing tasks
 - Halts the processing of waiting tasks
 - Returns a list of the tasks that were awaiting execution.
 - No new tasks will be accepted by ExecutorService after this command.

 3. es.awaitTermination(long timeout, TimeUnit unit)
 - Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted,
   whichever happens first.
 - Returns "true" if executor has done its work set and "false" if termination happens by timeout


 Checkout our Part12 class for all these implementations!

 */
public class Part12 {

	public static void main(String[] args) throws Exception {
		TimeAnalyzer timeAnalyzer = TimeAnalyzer.newAnalyzer(); //using for time calculation
		ExecutorService executor = Executors.newFixedThreadPool(8); //8=Max concurrent threads

		List<String> urls = new ArrayList<>();
		urls.add("http://google.com");
		urls.add("http://yahoo.com");
		urls.add("http://bing.com");
		urls.add("http://httpbin.org/status/201"); //custom response code test api
		urls.add("http://httpbin.org/status/202");
		urls.add("http://httpbin.org/status/203");
		urls.add("http://httpbin.org/status/204");
		urls.add("http://httpbin.org/status/303");
		urls.add("http://httpbin.org/status/500");

		List<WebResponseChecker> callableList = new ArrayList<>();
		for(String url:urls){
			callableList.add(new WebResponseChecker(url));
		}

		//execute all callable-s and wait for the result
		List<Future<String>> results = executor.invokeAll(callableList);

		//Now print the result
		for(Future future: results){
			System.out.println(future.get());
		}

		executor.shutdown();

		//Now calculate total time
		timeAnalyzer.analyze();
	}

	static class WebResponseChecker implements Callable<String>{
		HttpInvoker httpInvoker;

		WebResponseChecker(String url) throws Exception {
			httpInvoker = new HttpInvoker(url);
		}

		@Override public String call() throws Exception {
			String result = httpInvoker.getUrl()+" - "+httpInvoker.getHttpResponse().code();

			//release http resource
			httpInvoker.closeNReleaseResource();

			return result;
		}
	}
}
