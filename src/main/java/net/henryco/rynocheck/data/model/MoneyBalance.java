package net.henryco.rynocheck.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Henry on 12/01/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "money_balance")
public class MoneyBalance {

	public static final String ID = "id";
	public static final String ACCOUNT_ID = "account_id";
	public static final String CURRENCY = "currency";
	public static final String AMOUNT = "amount";
	public static final String LAST_UPDATE = "last_update";


	private @Id @Column(
			name = ID
	) @GeneratedValue(
			strategy = AUTO
	) Long id;


	private @Column(
			name = ACCOUNT_ID,
			nullable = false,
			updatable = false
	) String user;


	private @Column(
			name = CURRENCY,
			nullable = false,
			updatable = false
	) Long currency;


	private @Column(
			name = AMOUNT
	) BigDecimal amount;


	private @Column(
			name = LAST_UPDATE
	) @Temporal(
			TIMESTAMP
	) Date lastUpdate;

}