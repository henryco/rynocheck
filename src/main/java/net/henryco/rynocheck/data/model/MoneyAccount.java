package net.henryco.rynocheck.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

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
	public static final String LAST_UPDATE = "last_update";

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


	private @Column(
			name = LAST_UPDATE
	) @Temporal(
			TIMESTAMP
	) Date lastUpdate;

}