package net.henryco.rynocheck.transaction.exec.active.extimp;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Named;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.transaction.exec.active.PartActive;
import net.henryco.rynocheck.transaction.exec.block.Block;

import java.util.Queue;

@Component
@Singleton
public class RunnablePartActive extends PartActive<Long, Runnable> {

	@Inject
	public RunnablePartActive(@Named("TransactionPoolSize") int poolSize) {
		super(payload -> new Block<Queue<Runnable>>(payload) {
			@Override
			protected void onBlockProcessing(Queue<Runnable> queue) {
				Runnable runnable;
				while ((runnable = queue.poll()) != null) {
					runnable.run();
				}
			}
		}, poolSize);
	}

}
