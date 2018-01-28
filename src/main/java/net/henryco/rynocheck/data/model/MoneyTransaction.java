package net.henryco.rynocheck.data.model;

import lombok.*;

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
	public static final String CURRENCY_CODE = "currency_code";
	public static final String TIME = "time";
	public static final String AMOUNT = "amount";
	public static final String DESCRIPTION = "description";


	private @Id @Column(
			name = ID,
			updatable = false,
			nullable = false,
			unique = true
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
	) @Setter(
			AccessLevel.NONE
	) Long currency;


	private @Column(
			name = CURRENCY_CODE,
			nullable = false,
			updatable = false
	) @Setter(
			AccessLevel.NONE
	) String currencyCode;


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


	private @Column(
			name = DESCRIPTION,
			updatable = false
	) String description;


	private @Setter(
			AccessLevel.NONE
	) Currency transactional;

	public void setCurrency(Currency currency) {
		this.currency = currency.getId();
		this.currencyCode = currency.getCode();
		this.transactional = currency;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount.abs();
	}
}