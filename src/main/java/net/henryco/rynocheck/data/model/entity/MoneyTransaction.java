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
@Entity(name = "money_transaction")
public class MoneyTransaction {

	private @Id @Column(
			name = "id",
			updatable = false,
			nullable = false
	) @GeneratedValue(
			strategy = AUTO
	) Long id;


	private @Column(
			name = "sender_id",
			updatable = false,
			nullable = false
	) Long sender;


	private @Column(
			name = "receiver_id",
			updatable = false,
			nullable = false
	) Long receiver;


	private @Column(
			name = "currency",
			nullable = false,
			updatable = false
	) Currency currency;


	private @Column(
			name = "time"
	) @Temporal(
			TIMESTAMP
	) Date time;


	private @Column(
			name = "amount"
	) BigDecimal amount;

}