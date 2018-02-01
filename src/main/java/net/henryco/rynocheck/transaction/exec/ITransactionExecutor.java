package net.henryco.rynocheck.transaction.exec;

public interface ITransactionExecutor {

	void submit(Long key, Runnable runnable);

	void push();
}