package net.henryco.rynocheck.account;


import lombok.Value;

import java.util.Date;

public interface MoneyAccountService {

	@Value final class Account {
		private String username;
		private String description;
		private String email;
		private Date lastUpdate;
	}

	void addAccount(String username, String password, String email) throws MoneyAccountException;

	default void addAccount(String username, String password) throws MoneyAccountException {
		addAccount(username, password, null);
	}

	boolean authenticate(String name, String password);

	Account getAccount(String name) throws MoneyAccountException;

}