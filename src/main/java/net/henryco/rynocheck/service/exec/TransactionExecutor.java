package net.henryco.rynocheck.service.exec;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.service.exec.active.IPartActive;
import net.henryco.rynocheck.service.exec.queue.IPartQueue;

@Component
@Singleton
public class TransactionExecutor implements ITransactionExecutor {

	private final IPartActive<Long, Runnable> partActive;
	private final IPartQueue<Long, Runnable> partQueue;


	@Inject
	public TransactionExecutor(IPartActive<Long, Runnable> partActive,
							   IPartQueue<Long, Runnable> partQueue) {

		this.partActive = partActive;
		this.partQueue = partQueue;

		partActive.bindQueuePart(partQueue);
		partQueue.bindActivePart(partActive);
	}


	@Override
	public final void submit(Long key, Runnable runnable) {
		partQueue.submit(runnable, key);
	}

	@Override
	public final void push() {
		partActive.push();
	}

}