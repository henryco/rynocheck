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
@Entity(name = "money_transaction")
public class MoneyTransaction {

	public static final String ID = "id";
	public static final String SENDER_ID = "sender_id";
	public static final String RECEIVER_ID = "receiver_id";
	public static final String CURRENCY = "currency";
	public static final String TIME = "time";
	public static final String AMOUNT = "amount";

	private @Id @Column(
			name = ID
	) @GeneratedValue(
			strategy = AUTO
	) Long id;


	private @Column(
			name = SENDER_ID,
			updatable = false,
			nullable = false
	) String sender;


	private @Column(
			name = RECEIVER_ID,
			updatable = false,
			nullable = false
	) String receiver;


	private @Column(
			name = CURRENCY,
			nullable = false,
			updatable = false
	) Long currency;


	private @Column(
			name = TIME,
			nullable = false
	) @Temporal(
			TIMESTAMP
	) Date time;


	private @Column(
			name = AMOUNT,
			nullable = false
	) BigDecimal amount;

}