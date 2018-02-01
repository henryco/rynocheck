package net.henryco.rynocheck.transaction.exec.queue;

import lombok.extern.java.Log;
import lombok.val;
import net.henryco.rynocheck.transaction.exec.active.IPartActive;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log
public class PartQueue<KEY, ELEMENT> implements IPartQueue<KEY, ELEMENT> {

	private final Map<KEY, Queue<ELEMENT>> queueMap;
	private final Queue<KEY> keyQueue;

	private IPartActive<?, ?> activePart;

	public PartQueue(Map<KEY, Queue<ELEMENT>> queueMap,
					 Queue<KEY> keyQueue) {

		log.info(":::PartQueue:::INSTANCE:::" + queueMap + ":::" + keyQueue);
		this.queueMap = queueMap;
		this.keyQueue = keyQueue;
	}

	@Override
	public synchronized Queue<ELEMENT> release() {

		log.info(":::PartQueue:::release:::");

		log.info(":::PartQueue:::release:::keyQueue.poll");
		val queueKey = keyQueue.poll();
		if (queueKey != null) {
			log.info(":::PartQueue:::release:::queueMap.remove:::" + queueKey);
			return queueMap.remove(queueKey);
		}
		return null;
	}

	@Override
	public synchronized void submit(ELEMENT element, KEY key) {

		log.info(":::PartQueue:::submit:::" + element + ":::" + key);

		log.info(":::PartQueue:::submit:::keyQueue.contains:::" + key);
		if (keyQueue.contains(key)) {

			log.info(":::PartQueue:::submit:::queueMap.get:::" + key);
			val elementQueue = queueMap.get(key);
			if (elementQueue != null) {

				log.info(":::PartQueue:::submit:::elementQueue.add:::" + element);
				elementQueue.add(element);
				return;
			}
		}

		log.info(":::PartQueue:::submit:::keyQueue.add:::" + key);
		keyQueue.add(key);

		log.info(":::PartQueue:::submit:::queueMap.put:::ConcurrentLinkedQueue" + element);
		queueMap.put(key, new ConcurrentLinkedQueue<ELEMENT>() {{
			add(element);
		}});


		if (activePart != null) {
			log.info(":::PartQueue:::submit:::activePart.push");
			activePart.push();
		}
	}

	@Override
	public void bindActivePart(IPartActive<KEY, ELEMENT> activePart) {
		log.info(":::PartQueue:::bindActivePart:::" + activePart);
		this.activePart = activePart;
	}
}