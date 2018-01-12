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

	private @Id @Column(
			name = "account_id",
			updatable = false,
			nullable = false,
			unique = true
	) String uid;


	private @Column(
			name = "password",
			nullable = false
	) String pass;


	private @Column(
			name = "description",
			length = 512
	) String description;


	private @Column(
			name = "email",
			unique = true
	) String email;

}