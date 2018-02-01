package net.henryco.rynocheck.transaction.exec.queue;

import net.henryco.rynocheck.transaction.exec.active.IPartActive;

import java.util.Queue;

public interface IPartQueue<KEY, ELEMENT> {

	Queue<ELEMENT> release();

	void submit(ELEMENT transaction, KEY key);

	void bindActivePart(IPartActive<KEY, ELEMENT> activePart);
}