package net.henryco.rynocheck.service.exec.queue;

import lombok.val;
import net.henryco.rynocheck.service.exec.active.IPartActive;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PartQueue<KEY, ELEMENT> implements IPartQueue<KEY, ELEMENT> {


	private final Map<KEY, Queue<ELEMENT>> queueMap;
	private final Queue<KEY> keyQueue;

	private IPartActive<?, ?> activePart;

	public PartQueue(Map<KEY, Queue<ELEMENT>> queueMap,
					 Queue<KEY> keyQueue) {
		this.queueMap = queueMap;
		this.keyQueue = keyQueue;
	}

	@Override
	public synchronized Queue<ELEMENT> release() {
		val queueKey = keyQueue.poll();
		if (queueKey != null)
			return queueMap.remove(queueKey);
		return null;
	}

	@Override
	public synchronized void submit(ELEMENT element, KEY key) {

		if (keyQueue.contains(key)) {
			val elementQueue = queueMap.get(key);
			if (elementQueue != null) {
				elementQueue.add(element);
				return;
			}
		}

		keyQueue.add(key);
		queueMap.put(key, new ConcurrentLinkedQueue<ELEMENT>() {{
			add(element);
		}});

		if (activePart != null)
			activePart.push();
	}

	@Override
	public void bindActivePart(IPartActive<KEY, ELEMENT> activePart) {
		this.activePart = activePart;
	}
}