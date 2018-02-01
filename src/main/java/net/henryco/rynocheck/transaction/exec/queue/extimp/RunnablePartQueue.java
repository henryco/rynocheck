package net.henryco.rynocheck.transaction.exec.queue.extimp;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.transaction.exec.queue.PartQueue;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Singleton
public class RunnablePartQueue extends PartQueue<Long, Runnable> {

	public RunnablePartQueue() {
		super(new HashMap<>(), new ConcurrentLinkedQueue<>());
	}
}