package net.henryco.rynocheck.service.exec.block;

import net.henryco.rynocheck.service.exec.active.IPartActive;

public abstract class Block<ELEMENT> {

	private IPartActive<?, ?> partActive;
	private final ELEMENT element;

	public Block(ELEMENT element) {
		this.element = element;
	}


	protected abstract void onBlockProcessing(ELEMENT element);


	public final void processBlock() {

		onBlockProcessing(element);
		if (partActive != null) {
			partActive.release();
			partActive.push();
		}
	}

	public final void bindPartActive(IPartActive<?, ?> partActive) {
		this.partActive = partActive;
	}
}