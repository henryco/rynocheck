package net.henryco.rynocheck.transaction.exec.block;

import lombok.extern.java.Log;
import net.henryco.rynocheck.transaction.exec.active.IPartActive;

@Log
public abstract class Block<ELEMENT> {

	private IPartActive<?, ?> partActive;
	private final ELEMENT element;

	public Block(ELEMENT element) {
		log.info(":::Block:::INSTANCE:::" + element);
		this.element = element;
	}


	protected abstract void onBlockProcessing(ELEMENT element);


	public final void processBlock() {

		log.info(":::Block:::processBlock:::");
		onBlockProcessing(element);

		if (partActive != null) {

			log.info(":::Block:::processBlock:::partActive.release");
			partActive.release();

			log.info(":::Block:::processBlock:::partActive.push");
			partActive.push();
		}
	}

	public final void bindPartActive(IPartActive<?, ?> partActive) {
		log.info(":::Block:::bindPartActive:::" + partActive);
		this.partActive = partActive;
	}
}