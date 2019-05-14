package net.henryco.rynocheck.transaction.exec.queue.extimp;

import com.github.henryco.injector.meta.annotations.Provide;
import net.henryco.rynocheck.transaction.exec.queue.PartQueue;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Provide
@Singleton
public class RunnablePartQueue extends PartQueue<Long, Runnable> {

	public RunnablePartQueue() {
		super(new HashMap<>(), new ConcurrentLinkedQueue<>());
	}
}