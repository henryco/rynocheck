package net.henryco.rynocheck.transaction.exec.active;

import lombok.extern.java.Log;
import net.henryco.rynocheck.transaction.exec.block.Block;
import net.henryco.rynocheck.transaction.exec.block.BlockFactory;
import net.henryco.rynocheck.transaction.exec.queue.IPartQueue;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Log
public class PartActive<KEY, ELEMENT> implements IPartActive<KEY, ELEMENT> {

	private final BlockFactory<Queue<ELEMENT>> blockFactory;
	private final ExecutorService executorService;
	private final AtomicInteger poolCount;
	private final int poolSize;

	private IPartQueue<KEY, ELEMENT> partQueue;

	public PartActive(BlockFactory<Queue<ELEMENT>> blockFactory, int poolSize) {

		log.info(":::PartActive:::INSTANCE:::PoolSize:::" + poolSize);

		this.executorService = Executors.newFixedThreadPool(poolSize);
		this.poolCount = new AtomicInteger(0);
		this.blockFactory = blockFactory;
		this.poolSize = poolSize;
	}


	@Override
	public void bindQueuePart(IPartQueue<KEY, ELEMENT> partQueue) {
		log.info(":::PartActive:::bindQueuePart:::" + partQueue);
		this.partQueue = partQueue;
	}

	@Override
	public synchronized void push() {

		log.info(":::PartActive:::push:::");
		if (poolCount.get() < poolSize && partQueue != null) {

			log.info(":::PartActive:::push:::partQueue.release");
			Queue<ELEMENT> elementQueue = partQueue.release();
			if (elementQueue == null) return;

			log.info(":::PartActive:::push:::blockFactory.createBlock");
			Block<Queue<ELEMENT>> block = blockFactory.createBlock(elementQueue);

			log.info(":::PartActive:::push:::block.bindPartActive:::this");
			block.bindPartActive(this);

			poolCount.incrementAndGet();

			log.info(":::PartActive:::push:::executorService.SUBMIT");
			executorService.submit(block::processBlock);
		}
	}

	@Override
	public void release() {
		log.info(":::PartActive:::release:::");
		poolCount.set(Math.max(0, poolCount.decrementAndGet()));
	}

}