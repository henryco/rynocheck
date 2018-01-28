package net.henryco.rynocheck.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.AUTO;

@Entity(name = "currency")
@Data @NoArgsConstructor
@AllArgsConstructor
public class Currency implements Serializable {

	public static final String CURRENCY_ID = "id";
	public static final String CURRENCY_NAME = "name";
	public static final String CURRENCY_CODE = "code";
	public static final String CURRENCY_MICRO_LIM = "micro_limit";
	public static final String CURRENCY_FEE = "fee";
	public static final String CURRENCY_EMITTER = "emitter";

	private @Id @Column(
			name = CURRENCY_ID,
			updatable = false,
			nullable = false,
			unique = true
	) @GeneratedValue(
			strategy = AUTO
	) Long id;


	private @Column(
			name = CURRENCY_NAME,
			nullable = false,
			updatable = false,
			unique = true
	) String name;


	private @Column(
			name = CURRENCY_CODE,
			nullable = false,
			updatable = false,
			unique = true
	) String code;


	private @Column(
			name = CURRENCY_MICRO_LIM
	) BigDecimal microLimit;


	private @Column(
			name = CURRENCY_FEE
	) BigDecimal fee;


	private @Column(
			name = CURRENCY_EMITTER
	) String emitter;

}