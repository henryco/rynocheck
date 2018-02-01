package net.henryco.rynocheck.service.exec;

public interface ITransactionExecutor {

	void submit(Long key, Runnable runnable);

	void push();
}