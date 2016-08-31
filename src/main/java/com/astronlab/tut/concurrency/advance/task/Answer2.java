package com.astronlab.tut.concurrency.advance.task;

import com.astronlab.tut.utils.http.HttpInvoker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*	2. Convert Part12 class into two section: in one section urls are fetched/invoked normally , concurrently in another and then
	calculate their time differences.*/

public class Answer2 {
	private static List<String> urls = new ArrayList<>();

	public static void main(String[] args) throws Exception {

		urls.add("http://google.com");
		urls.add("http://yahoo.com");
		urls.add("http://bing.com");
		urls.add("http://httpbin.org/status/201"); //custom response code test api
		urls.add("http://httpbin.org/status/202");
		urls.add("http://httpbin.org/status/203");
		urls.add("http://httpbin.org/status/204");
		urls.add("http://httpbin.org/status/303");
		urls.add("http://httpbin.org/status/500");

		InvokeClassWithThreadPool ict = new InvokeClassWithThreadPool();
		Date sdate = new Date();
		ict.run(urls);
		Date edate = new Date();
		Long t = (edate.getTime() - sdate.getTime()) / 1000;

		InvokeClassWithoutThreadPool icwt = new InvokeClassWithoutThreadPool();
		Date sDate1 = new Date();
		icwt.run(urls);
		Date eDate1 = new Date();

		Long t1 = (eDate1.getTime() - sDate1.getTime()) / 1000;

		System.out.println("\nThread-pool implementation is: " + (t1 - t) + " seconds faster");
	}

	static class InvokeClassWithThreadPool {
		ExecutorService executor = Executors.newFixedThreadPool(
				8); //8=Max concurrent threads

		public void run(List<String> urls) throws Exception {

			List<Future> futureList = new ArrayList<>();
			for (String url : urls) {
				Future future = executor.submit(new WebResponseChecker(url));
				futureList.add(future);

			}

			//Now print the result
			for (Future f : futureList) {
				System.out.println(f.get());
			}

			executor.shutdown();
		}
	}

	static class InvokeClassWithoutThreadPool {
		public void run(List<String> urls) throws Exception {
			for (String url : urls) {
				WebResponseChecker wc = new WebResponseChecker(url);
				System.out.println(wc.call());

			}
		}
	}

	static class WebResponseChecker implements Callable<String> {
		HttpInvoker httpInvoker;

		WebResponseChecker(String url) throws Exception {
			httpInvoker = new HttpInvoker(url);
		}

		@Override public String call() throws Exception {
			String result = httpInvoker.getUrl() + " - " + httpInvoker.getHttpResponse().code();

			//release http resource
			httpInvoker.closeNReleaseResource();

			return result;
		}
	}
}