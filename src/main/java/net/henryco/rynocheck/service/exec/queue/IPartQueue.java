package net.henryco.rynocheck.service.exec.queue;

import net.henryco.rynocheck.service.exec.active.IPartActive;

import java.util.Queue;

public interface IPartQueue<KEY, ELEMENT> {

	Queue<ELEMENT> release();

	void submit(ELEMENT transaction, KEY key);

	void bindActivePart(IPartActive<KEY, ELEMENT> activePart);
}