package net.henryco.rynocheck.transaction.exec;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.extern.java.Log;
import net.henryco.rynocheck.transaction.exec.active.IPartActive;
import net.henryco.rynocheck.transaction.exec.queue.IPartQueue;

@Component
@Singleton
@Log
public class TransactionExecutor implements ITransactionExecutor {

	private final IPartActive<Long, Runnable> partActive;
	private final IPartQueue<Long, Runnable> partQueue;


	@Inject
	public TransactionExecutor(IPartActive<Long, Runnable> partActive,
							   IPartQueue<Long, Runnable> partQueue) {

		log.info("TransactionExecutor::instance");
		this.partActive = partActive;
		this.partQueue = partQueue;

		partActive.bindQueuePart(partQueue);
		partQueue.bindActivePart(partActive);
	}


	@Override
	public final void submit(Long key, Runnable runnable) {
		log.info("TransactionExecutor::submit(key: " + key + ", runnable: " + runnable + ")");
		partQueue.submit(runnable, key);
	}

	@Override
	public final void push() {
		log.info("TransactionExecutor::push");
		partActive.push();
	}

}