package com.astronlab.tut.concurrency.advance.task;

import com.astronlab.tut.utils.http.HttpInvoker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 3. Collect all the data from the urls in Part12 and write those data in files separately. File operations must be done in separate threads.

 */
public class Answer3 {
	private static List<String> urls = new ArrayList<>();
	private static ExecutorService executor = Executors.newFixedThreadPool(8);

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

		Runner runner = new Runner();
		runner.run(urls);
	}

	static class Runner {

		public void run(List<String> urls) throws Exception {
			Future future;
			List<Future> futureList1 = new ArrayList<>();

			for (String url : urls) {
				future =  executor.submit(new WebResponseChecker(url));
				futureList1.add(future);
			}

			List<Future> futureList2 = new ArrayList<>();
			for (Future f : futureList1) {
				future = executor.submit(new FileWritter((List)f.get()));
				futureList2.add(future);
			}

			//wait for file callable's set
			for(Future f : futureList2){
				f.get();
			}

			executor.shutdown();
		}
	}

	static class FileWritter implements Callable<String>{
		String fileText,fileName;

		FileWritter(List<String> inputArr){
			fileText = inputArr.get(0);
			fileName = inputArr.get(1).replaceAll("^.*?//","").replaceAll("[/\\.]","_");
		}

		@Override public String call() throws Exception {
      File file = new File("output/"+fileName+".txt");
			file.getParentFile().mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileText);
			writer.close();

			return null;
		}
	}

	static class WebResponseChecker implements Callable<List> {
		HttpInvoker httpInvoker;

		WebResponseChecker(String url) throws Exception {
			httpInvoker = new HttpInvoker(url);
		}

		@Override public List<String> call() throws Exception {
			List<String> resultArr = new ArrayList<>();
			String result = httpInvoker.getStringData();
      resultArr.add(result);
			resultArr.add(httpInvoker.getUrl());

			//release http resource
			httpInvoker.closeNReleaseResource();

			return resultArr;
		}
	}
}