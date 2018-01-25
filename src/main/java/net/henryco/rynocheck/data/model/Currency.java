package net.henryco.rynocheck.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity(name = "currency")
@Data @NoArgsConstructor
@AllArgsConstructor
public class Currency {

	public static final String CURRENCY_ID = "id";
	public static final String CURRENCY_NAME = "name";
	public static final String CURRENCY_CODE = "code";
	public static final String CURRENCY_MICRO_LIM = "micro_limit";

	private @Id @Column(
			name = CURRENCY_ID,
			updatable = false,
			nullable = false,
			unique = true
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

}