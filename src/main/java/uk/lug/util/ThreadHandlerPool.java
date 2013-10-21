package uk.lug.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class ThreadHandlerPool {
	private static ExecutorService executorService;
	
	private static ExecutorService getExecutor() {
		if (executorService==null) {
			executorService = Executors.newCachedThreadPool();
		}
		return executorService;
	}
	
	public static Future run(Runnable task) {
		Future<?> ret = getExecutor().submit(task);
		Integer count = Thread.getAllStackTraces().keySet().size();
		return ret;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if ( executorService!=null ) {
			executorService.shutdown();
		}
	}
}
