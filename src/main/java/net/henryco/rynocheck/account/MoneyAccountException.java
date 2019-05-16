package net.henryco.rynocheck.account;

public class MoneyAccountException extends Exception {

	public MoneyAccountException(String message) {
		super(message);
	}

	public MoneyAccountException(String message, Throwable cause) {
		super(message, cause);
	}
}