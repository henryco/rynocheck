package net.henryco.rynocheck.transaction.exec.active;

import net.henryco.rynocheck.transaction.exec.queue.IPartQueue;

public interface IPartActive<KEY, ELEMENT> {

	void bindQueuePart(IPartQueue<KEY, ELEMENT> partQueue);

	void push();

	void release();
}
