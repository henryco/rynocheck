package net.henryco.rynocheck.data.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Henry on 12/01/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "money_account")
public class MoneyAccount {

	public static final String ACCOUNT_ID = "account_id";
	public static final String PASSWORD = "password";
	public static final String DESCRIPTION = "description";
	public static final String EMAIL = "email";

	private @Id @Column(
			name = ACCOUNT_ID,
			updatable = false,
			nullable = false,
			unique = true
	) String uid;


	private @Column(
			name = PASSWORD,
			nullable = false
	) String pass;


	private @Column(
			name = DESCRIPTION,
			length = 512
	) String description;


	private @Column(
			name = EMAIL,
			unique = true
	) String email;

}