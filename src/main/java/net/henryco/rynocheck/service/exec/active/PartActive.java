package net.henryco.rynocheck.service.exec.active;

import net.henryco.rynocheck.service.exec.block.Block;
import net.henryco.rynocheck.service.exec.block.BlockFactory;
import net.henryco.rynocheck.service.exec.queue.IPartQueue;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class PartActive<KEY, ELEMENT> implements IPartActive<KEY, ELEMENT> {

	private final BlockFactory<Queue<ELEMENT>> blockFactory;
	private final ExecutorService executorService;
	private final AtomicInteger poolCount;
	private final int poolSize;

	private IPartQueue<KEY, ELEMENT> partQueue;

	public PartActive(BlockFactory<Queue<ELEMENT>> blockFactory, int poolSize) {
		this.executorService = Executors.newFixedThreadPool(poolSize);
		this.poolCount = new AtomicInteger(0);
		this.blockFactory = blockFactory;
		this.poolSize = poolSize;
	}


	@Override
	public void bindQueuePart(IPartQueue<KEY, ELEMENT> partQueue) {
		this.partQueue = partQueue;
	}

	@Override
	public synchronized void push() {

		if (poolCount.get() < poolSize && partQueue != null) {

			Queue<ELEMENT> elementQueue = partQueue.release();
			if (elementQueue == null) return;

			Block<Queue<ELEMENT>> block = blockFactory.createBlock(elementQueue);
			block.bindPartActive(this);

			poolCount.incrementAndGet();

			executorService.submit(block::processBlock);
		}
	}

	@Override
	public void release() {
		poolCount.set(Math.max(0, poolCount.decrementAndGet()));
	}

}