package net.henryco.rynocheck.service.exec.active;

import net.henryco.rynocheck.service.exec.queue.IPartQueue;

public interface IPartActive<KEY, ELEMENT> {

	void bindQueuePart(IPartQueue<KEY, ELEMENT> partQueue);

	void push();

	void release();
}
