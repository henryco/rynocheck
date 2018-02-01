package net.henryco.rynocheck.transaction.exec.block;

public interface BlockFactory<T> {

	Block<T> createBlock(T payload);


	static Block<Runnable> runnableBlock(Runnable runnable) {
		return new Block<Runnable>(runnable) {
			@Override
			protected void onBlockProcessing(Runnable runnable) {
				runnable.run();
			}
		};
	}


}