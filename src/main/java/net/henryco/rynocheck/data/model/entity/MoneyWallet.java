package net.henryco.rynocheck.data.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.henryco.rynocheck.data.model.core.Currency;

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
@Entity(name = "money_wallet")
public class MoneyWallet {

	private @Id @Column(
			name = "id",
			nullable = false,
			updatable = false
	) @GeneratedValue(
			strategy = AUTO
	) Long id;


	private @Column(
			name = "account_id",
			nullable = false,
			updatable = false
	) String user;


	private @Column(
			name = "currency",
			nullable = false,
			updatable = false
	) Currency currency;


	private @Column(
			name = "amount"
	) BigDecimal amount;


	private @Column(
			name = "last_update"
	) @Temporal(
			TIMESTAMP
	) Date lastUpdate;

}